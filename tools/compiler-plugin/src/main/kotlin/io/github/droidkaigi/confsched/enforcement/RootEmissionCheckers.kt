package io.github.droidkaigi.confsched.enforcement

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.cfa.util.previousCfgNodes
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.cfa.FirControlFlowChecker
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.CFGNode
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.CFGNodeWithSubgraphs
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.ControlFlowGraph
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.FunctionCallExitNode
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.PostponedLambdaExitNode
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.SplitPostponedLambdasNode
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeTypeParameterType
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneTypeOrNull
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

private val LAYOUT_PACKAGE = FqName("androidx.compose.foundation.layout")

private val LAYOUT_SCOPE_IDS = setOf(
    ClassId(LAYOUT_PACKAGE, Name.identifier("BoxScope")),
    ClassId(LAYOUT_PACKAGE, Name.identifier("RowScope")),
    ClassId(LAYOUT_PACKAGE, Name.identifier("ColumnScope")),
    ClassId(LAYOUT_PACKAGE, Name.identifier("FlowRowScope")),
    ClassId(LAYOUT_PACKAGE, Name.identifier("FlowColumnScope")),
    ClassId(FqName("androidx.compose.foundation.lazy"), Name.identifier("LazyItemScope")),
    ClassId(FqName("androidx.compose.foundation.lazy.grid"), Name.identifier("LazyGridItemScope")),
    ClassId(
        FqName("androidx.compose.foundation.lazy.staggeredgrid"),
        Name.identifier("LazyStaggeredGridItemScope"),
    ),
    ClassId(FqName("androidx.compose.foundation.pager"), Name.identifier("PagerScope")),
)

// A repeated emitter saturates here rather than being counted: the rule only asks whether a path
// carries two, and an iteration count is not known statically.
private const val SATURATED_EMISSION_COUNT = 2

private val FUNCTION_GRAPH_KINDS = setOf(ControlFlowGraph.Kind.Function, ControlFlowGraph.Kind.LocalFunction)

internal object SingleRootEmissionChecker : FirControlFlowChecker(MppCheckerKind.Platform) {
    context(reporter: DiagnosticReporter, context: CheckerContext)
    override fun analyze(graph: ControlFlowGraph) {
        val session = context.session
        for (function in graph.functionGraphs()) {
            val declaration = function.declaration as? FirNamedFunction ?: continue
            if (!declaration.symbol.hasAnnotation(COMPOSABLE_ANNOTATION_ID, session)) continue
            if (declaration.declaresLayoutScope(session)) continue
            if (!function.emitsFlatSiblings(session)) continue
            reporter.reportOn(declaration.source, RootEmissionErrors.COMPOSABLE_EMITS_FLAT_SIBLINGS, context)
        }
    }
}

// Only a graph nothing enters reaches a control-flow checker on its own, so a function nested in
// another declaration — a local function, or a member of a local class — is reached from here.
private fun ControlFlowGraph.functionGraphs(): List<ControlFlowGraph> {
    val found = mutableListOf<ControlFlowGraph>()

    fun collect(graph: ControlFlowGraph, isRoot: Boolean) {
        if (graph.kind in FUNCTION_GRAPH_KINDS && (isRoot || graph.isSubGraph)) found += graph
        for (sub in graph.subGraphs) collect(sub, isRoot = false)
    }
    collect(this, isRoot = true)
    return found
}

private fun FirNamedFunction.declaresLayoutScope(session: FirSession): Boolean {
    val declared = listOfNotNull(receiverParameter?.typeRef) + contextParameters.map(FirValueParameter::returnTypeRef)
    return declared.any { it.declaresLayoutScope(session) }
}

private fun FirTypeRef.declaresLayoutScope(session: FirSession): Boolean {
    val bounds = (coneTypeOrNull as? ConeTypeParameterType)
        ?.lookupTag?.typeParameterSymbol?.resolvedBounds
        ?: return toRegularClassSymbol(session)?.isLayoutScope(session) == true
    return bounds.any { it.toRegularClassSymbol(session)?.isLayoutScope(session) == true }
}

private fun FirClassSymbol<*>.isLayoutScope(session: FirSession): Boolean {
    if (classId in LAYOUT_SCOPE_IDS) return true
    return lookupSuperTypes(this, lookupInterfaces = true, deep = true, useSiteSession = session)
        .any { it.classId in LAYOUT_SCOPE_IDS }
}

// Forward analysis over the graph: every node holds the largest number of emissions a live path
// reaching it has produced, joined by taking the larger and saturating at two. A loop is a back
// edge, so the sweep repeats until no value moves.
private fun ControlFlowGraph.emitsFlatSiblings(session: FirSession): Boolean {
    val paths = emissionPaths(session)
    val nodes = paths.counted.keys.flatMap(ControlFlowGraph::nodes)
    val reached = HashMap<CFGNode<*>, Int>(nodes.size)
    var changed = true
    while (changed) {
        changed = false
        for (node in nodes) {
            if (node.isDead) continue
            val incoming = paths.predecessorsOf(node).maxOfOrNull { reached[it] ?: 0 } ?: 0
            val emitted = if (paths.counts(node) && node.emitsNode(session)) 1 else 0
            val value = minOf(incoming + emitted, SATURATED_EMISSION_COUNT)
            if (reached.put(node, value) != value) changed = true
        }
    }
    return reached.any { (node, value) -> value >= SATURATED_EMISSION_COUNT && paths.counts(node) }
}

// Every graph the analysis walks, each mapped to whether its own emissions belong to the checked
// function, together with the edges the builder leaves out between a lambda and the call holding it.
private class EmissionPaths(
    val counted: Map<ControlFlowGraph, Boolean>,
    private val resumedBy: Map<CFGNode<*>, CFGNode<*>>,
) {
    fun counts(node: CFGNode<*>): Boolean = counted.getValue(node.owner)

    fun predecessorsOf(node: CFGNode<*>): List<CFGNode<*>> {
        val entered = node.previousCfgNodes.filterNot { node.repeatsContentFrom(it) }
        return resumedBy[node]?.let { entered + it } ?: entered
    }
}

// Two edges stand for a lambda its callee may run any number of times: a back edge into the call
// holding it, and, where the callee is not known to run it in place, one from the lambda's own exit
// to its enter. Such repetition belongs to the callee, which is handed one content per call site.
private fun CFGNode<*>.repeatsContentFrom(predecessor: CFGNode<*>): Boolean =
    (this is SplitPostponedLambdasNode && edgeFrom(predecessor).kind.isBack) ||
        (this === owner.enterNode && predecessor === owner.exitNode)

private fun ControlFlowGraph.emissionPaths(session: FirSession): EmissionPaths {
    val counted = LinkedHashMap<ControlFlowGraph, Boolean>()
    val resumedBy = HashMap<CFGNode<*>, CFGNode<*>>()

    fun collect(graph: ControlFlowGraph, counts: Boolean) {
        if (counted.put(graph, counts) != null) return
        for (node in graph.nodes) {
            if (node !is CFGNodeWithSubgraphs<*>) continue
            val kind = node.callEmissionKind(session)
            for (sub in node.subGraphs) {
                val calledInPlace = sub.kind == ControlFlowGraph.Kind.AnonymousFunctionCalledInPlace
                // A call that emits owns its lambdas: they are the content of the node it places.
                // Anything else leaves their emissions flat in the caller, but only a lambda called
                // in place is on the caller's path already; a transparent call's content joins it
                // here, at the node where an in-place lambda would resume.
                if (kind == EmissionKind.Transparent && !calledInPlace) {
                    node.resumptionOf(sub)?.let { resumedBy[it] = sub.exitNode }
                }
                val flat = kind == EmissionKind.Transparent || (calledInPlace && kind != EmissionKind.Node)
                collect(sub, counts && flat)
            }
        }
    }
    collect(this, counts = true)
    return EmissionPaths(counted, resumedBy)
}

private fun CFGNodeWithSubgraphs<*>.resumptionOf(subGraph: ControlFlowGraph): CFGNode<*>? = followingNodes
    .firstOrNull { it is PostponedLambdaExitNode && it.fir.anonymousFunction === subGraph.declaration }

private fun CFGNodeWithSubgraphs<*>.callEmissionKind(session: FirSession): EmissionKind? =
    ((this as? SplitPostponedLambdasNode)?.fir as? FirFunctionCall)?.emissionKind(session)

private fun CFGNode<*>.emitsNode(session: FirSession): Boolean =
    this is FunctionCallExitNode && fir.emissionKind(session) == EmissionKind.Node

private fun FirFunctionCall.emissionKind(session: FirSession): EmissionKind =
    if (invokesComposableValue(session)) {
        kindByName(invokesValueNamedAsEffect())
    } else {
        session.composableEmissionKinds.kindOf(this)
    }

object RootEmissionErrors : KtDiagnosticsContainer() {
    val COMPOSABLE_EMITS_FLAT_SIBLINGS by error0<PsiElement>(SourceElementPositioningStrategies.DECLARATION_NAME)

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = RootEmissionErrorMessages
}

object RootEmissionErrorMessages : BaseDiagnosticRendererFactory() {
    @Suppress("ktlint:standard:property-naming")
    override val MAP by KtDiagnosticFactoryToRendererMap("RootEmission") { map ->
        map.put(
            RootEmissionErrors.COMPOSABLE_EMITS_FLAT_SIBLINGS,
            "This composable emits more than one node at its root, so the caller decides how they " +
                "are laid out. Wrap them in a layout, or declare a layout scope — ColumnScope, " +
                "BoxScope, and the like, as a receiver or a context parameter — to hand placement " +
                "to the caller.",
        )
    }
}

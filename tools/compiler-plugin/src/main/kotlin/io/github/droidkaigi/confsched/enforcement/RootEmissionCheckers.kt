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
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirSimpleFunctionChecker
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirLoop
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.FirThrowExpression
import org.jetbrains.kotlin.fir.expressions.FirTryExpression
import org.jetbrains.kotlin.fir.expressions.FirWhenExpression
import org.jetbrains.kotlin.fir.expressions.impl.FirElseIfTrueCondition
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.isUnit
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.util.OperatorNameConventions
import kotlin.math.max

private val LAYOUT_SCOPE_IDS = setOf(
    ClassId(FqName("androidx.compose.foundation.layout"), Name.identifier("BoxScope")),
    ClassId(FqName("androidx.compose.foundation.layout"), Name.identifier("RowScope")),
    ClassId(FqName("androidx.compose.foundation.layout"), Name.identifier("ColumnScope")),
    ClassId(FqName("androidx.compose.foundation.lazy"), Name.identifier("LazyItemScope")),
)

// A composable named `…Effect` runs work rather than emitting a node, the convention every effect
// in this repository and in the Compose runtime follows. A `fun interface` effect carries the name
// on the interface, so its `invoke` is read through that.
private const val EFFECT_NAME_SUFFIX = "Effect"

private const val EMISSIONS_PER_ITERATION = 2

internal object SingleRootEmissionChecker : FirSimpleFunctionChecker(MppCheckerKind.Platform) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirNamedFunction) {
        val session = context.session
        if (!declaration.symbol.hasAnnotation(COMPOSABLE_ANNOTATION_ID, session)) return
        if (declaration.declaresLayoutScopeReceiver(session)) return
        val body = declaration.body ?: return

        if (body.emissions(session).worstPath < 2) return
        reporter.reportOn(declaration.source, RootEmissionErrors.COMPOSABLE_EMITS_FLAT_SIBLINGS, context)
    }
}

private fun FirNamedFunction.declaresLayoutScopeReceiver(session: FirSession): Boolean {
    val receiver = receiverParameter?.typeRef?.toRegularClassSymbol(session) ?: return false
    if (receiver.classId in LAYOUT_SCOPE_IDS) return true
    return lookupSuperTypes(receiver, lookupInterfaces = true, deep = true, useSiteSession = session)
        .any { it.classId in LAYOUT_SCOPE_IDS }
}

// Emissions counted along control flow. `fallthrough` is the largest count a path carries into
// whatever follows, and is null when every path leaves first; `exited` covers the paths that left.
private data class Emissions(val fallthrough: Int?, val exited: Int) {
    val worstPath: Int get() = maxOf(fallthrough ?: 0, exited)
}

private val EMITS_NOTHING = Emissions(fallthrough = 0, exited = 0)

private fun FirStatement.emissions(session: FirSession): Emissions = when (this) {
    is FirBlock -> statements.foldEmissions(session)

    is FirWhenExpression -> branchEmissions(session)

    is FirTryExpression -> (listOf(tryBlock) + catches.map { it.block }).branchEmissions(session)

    is FirLoop -> block.emissions(session).let { body ->
        Emissions(
            fallthrough = if (body.worstPath > 0) EMISSIONS_PER_ITERATION else 0,
            exited = body.exited,
        )
    }

    is FirReturnExpression -> Emissions(fallthrough = null, exited = result.emissions(session).worstPath)

    is FirThrowExpression -> Emissions(fallthrough = null, exited = 0)

    is FirFunctionCall -> if (emitsNode(session)) Emissions(fallthrough = 1, exited = 0) else EMITS_NOTHING

    else -> EMITS_NOTHING
}

private fun FirWhenExpression.branchEmissions(session: FirSession): Emissions {
    val paths = branches.map { it.result.emissions(session) }
    // A `when` with no else has a path that runs none of the branches.
    val missingElse = branches.none { it.condition is FirElseIfTrueCondition }
    return (if (missingElse) paths + EMITS_NOTHING else paths).merge()
}

private fun List<FirStatement>.branchEmissions(session: FirSession): Emissions =
    map { it.emissions(session) }.merge()

private fun List<Emissions>.merge(): Emissions = Emissions(
    fallthrough = mapNotNull { it.fallthrough }.maxOrNull(),
    exited = maxOfOrNull { it.exited } ?: 0,
)

private fun List<FirStatement>.foldEmissions(session: FirSession): Emissions {
    var reached = 0
    var left = 0

    for (element in this) {
        val emissions = element.emissions(session)
        left = max(left, reached + emissions.exited)

        if (emissions.fallthrough == null) {
            return Emissions(null, left)
        }
        reached += emissions.fallthrough
    }

    return Emissions(reached, left)
}

private fun FirFunctionCall.emitsNode(session: FirSession): Boolean {
    if (!resolvedType.isUnit) return false
    val symbol = calleeReference.toResolvedCallableSymbol() ?: return false
    if (declaredName().endsWith(EFFECT_NAME_SUFFIX)) return false
    if (symbol.hasAnnotation(COMPOSABLE_ANNOTATION_ID, session)) return true
    val invoked = invokedValue ?: return false
    return invoked.resolvedType.isComposableFunctionType(session)
}

// An `invoke` call is named by the type holding it, which is what a `fun interface` effect declares
// itself with; every other call is named by its own callee.
private fun FirFunctionCall.declaredName(): String {
    val symbol = calleeReference.toResolvedCallableSymbol() ?: return ""
    val name = symbol.name
    if (name != OperatorNameConventions.INVOKE) return name.asString()
    val holder = invokedValue?.resolvedType?.classId ?: return ""
    return holder.shortClassName.asString()
}

private val FirFunctionCall.invokedValue: FirExpression?
    get() = explicitReceiver ?: dispatchReceiver

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
                "are laid out. Wrap them in a layout, or declare a layout scope receiver — BoxScope, " +
                "RowScope, ColumnScope, LazyItemScope, or a subtype — to hand placement to the caller.",
        )
    }
}

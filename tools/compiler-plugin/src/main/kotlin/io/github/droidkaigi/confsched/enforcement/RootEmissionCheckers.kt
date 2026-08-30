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
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.expressions.ExhaustivenessStatus
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirBreakExpression
import org.jetbrains.kotlin.fir.expressions.FirCatch
import org.jetbrains.kotlin.fir.expressions.FirContinueExpression
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirLoop
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.FirThrowExpression
import org.jetbrains.kotlin.fir.expressions.FirTryExpression
import org.jetbrains.kotlin.fir.expressions.FirWhenExpression
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.expressions.unwrapArgument
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeTypeParameterType
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneTypeOrNull
import org.jetbrains.kotlin.fir.types.isUnit
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.util.OperatorNameConventions
import kotlin.math.max

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

private const val EFFECT_NAME_SUFFIX = "Effect"

// A repeated emitter saturates here rather than being counted: the rule only asks whether a path
// carries two, and an iteration count is not known statically.
private const val SATURATED_EMISSION_COUNT = 2

internal object SingleRootEmissionChecker : FirSimpleFunctionChecker(MppCheckerKind.Platform) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirNamedFunction) {
        val session = context.session
        if (!declaration.symbol.hasAnnotation(COMPOSABLE_ANNOTATION_ID, session)) return
        if (declaration.declaresLayoutScope(session)) return
        val body = declaration.body ?: return

        if (body.emissions(session, declaration).worstPath < 2) return
        reporter.reportOn(declaration.source, RootEmissionErrors.COMPOSABLE_EMITS_FLAT_SIBLINGS, context)
    }
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

// Emissions counted along control flow, each field the largest count its kind of path carries.
// `fallthrough` continues into whatever follows and is null when no path does; `exited` leaves the
// checked function; `left` completes the nearest enclosing lambda, which resumes the caller; `broke`
// leaves the loop it names, and `continued` returns to that loop's head. Null means no path of that
// kind ends here.
private data class Emissions(
    val fallthrough: Int?,
    val exited: Int? = null,
    val left: Int? = null,
    val broke: Map<FirLoop, Int> = emptyMap(),
    val continued: Map<FirLoop, Int> = emptyMap(),
) {
    val worstPath: Int
        get() = maxOf(
            fallthrough ?: 0,
            exited ?: 0,
            left ?: 0,
            broke.values.maxOrNull() ?: 0,
            continued.values.maxOrNull() ?: 0,
        )
}

private val EMITS_NOTHING = Emissions(fallthrough = 0)

private fun FirStatement.emissions(session: FirSession, root: FirNamedFunction): Emissions = when (this) {
    is FirBlock -> statements.map { it.emissions(session, root) }.sequence()

    is FirWhenExpression -> branchEmissions(session, root)

    is FirTryExpression ->
        (listOf(tryBlock) + catches.map(FirCatch::block)).map { it.emissions(session, root) }.merge()

    is FirLoop -> block.emissions(session, root).loopEmissions(this)

    // A lambda body ends in a return targeting the lambda, so the target separates leaving the
    // function from completing the lambda.
    is FirReturnExpression -> result.emissions(session, root).worstPath.let { carried ->
        if (target.labeledElement === root) {
            Emissions(fallthrough = null, exited = carried)
        } else {
            Emissions(fallthrough = null, left = carried)
        }
    }

    is FirThrowExpression -> Emissions(fallthrough = null, exited = 0)

    is FirBreakExpression -> Emissions(fallthrough = null, broke = mapOf(target.labeledElement to 0))

    is FirContinueExpression -> Emissions(fallthrough = null, continued = mapOf(target.labeledElement to 0))

    is FirProperty -> initializer?.emissions(session, root) ?: EMITS_NOTHING

    is FirFunctionCall -> callEmissions(session, root)

    else -> EMITS_NOTHING
}

// A call that emits owns whatever its lambdas emit; one that does not passes them through, which is
// how an inline non-composable lambda such as `run` carries emissions to the call site. Completing
// a lambda resumes the caller, so `left` turns back into a fallthrough here.
private fun FirFunctionCall.callEmissions(session: FirSession, root: FirNamedFunction): Emissions {
    if (emitsNode(session)) return Emissions(fallthrough = 1)
    val inside = arguments
        .mapNotNull { (it.unwrapArgument() as? FirAnonymousFunctionExpression)?.anonymousFunction?.body }
        .map { it.emissions(session, root) }
        .ifEmpty { return EMITS_NOTHING }
        .merge()
    return Emissions(
        fallthrough = maxOf(inside.fallthrough ?: 0, inside.left ?: 0),
        exited = inside.exited,
        broke = inside.broke,
        continued = inside.continued,
    )
}

// A path reaches the loop head by falling off the body's end or by `continue`; either way, what it
// carries repeats.
private fun Emissions.loopEmissions(loop: FirLoop): Emissions = Emissions(
    fallthrough = maxOf(
        if (maxOf(fallthrough ?: 0, continued[loop] ?: 0) > 0) SATURATED_EMISSION_COUNT else 0,
        broke[loop] ?: 0,
    ),
    exited = exited,
    left = left,
    broke = broke - loop,
    continued = continued - loop,
)

private fun FirWhenExpression.branchEmissions(session: FirSession, root: FirNamedFunction): Emissions {
    val paths = branches.map { it.result.emissions(session, root) }
    // A `when` that does not cover its subject has a path that runs none of the branches.
    val uncovered = exhaustivenessStatus is ExhaustivenessStatus.NotExhaustive
    return (if (uncovered) paths + EMITS_NOTHING else paths).merge()
}

// Alternatives: the paths never meet, so each way of ending takes the worst of its own kind.
private fun List<Emissions>.merge(): Emissions = Emissions(
    fallthrough = mapNotNull(Emissions::fallthrough).maxOrNull(),
    exited = mapNotNull(Emissions::exited).maxOrNull(),
    left = mapNotNull(Emissions::left).maxOrNull(),
    broke = flatMap { it.broke.entries }
        .groupBy({ it.key }, { it.value })
        .mapValues { (_, counts) -> counts.max() },
    continued = flatMap { it.continued.entries }
        .groupBy({ it.key }, { it.value })
        .mapValues { (_, counts) -> counts.max() },
)

// One after another: what the earlier ones carried offsets every way the later ones can end.
private fun List<Emissions>.sequence(): Emissions {
    var reached = 0
    var exited: Int? = null
    var left: Int? = null
    val broke = mutableMapOf<FirLoop, Int>()
    val continued = mutableMapOf<FirLoop, Int>()

    for (emissions in this) {
        emissions.exited?.let { exited = max(exited ?: 0, reached + it) }
        emissions.left?.let { left = max(left ?: 0, reached + it) }
        for ((loop, count) in emissions.broke) broke[loop] = max(broke[loop] ?: 0, reached + count)
        for ((loop, count) in emissions.continued) continued[loop] = max(continued[loop] ?: 0, reached + count)

        val fallthrough = emissions.fallthrough ?: return Emissions(null, exited, left, broke, continued)
        reached += fallthrough
    }
    return Emissions(reached, exited, left, broke, continued)
}

private fun FirFunctionCall.emitsNode(session: FirSession): Boolean {
    if (!resolvedType.isUnit) return false
    val symbol = calleeReference.toResolvedCallableSymbol() ?: return false
    if (isEffectCall(symbol)) return false
    if (symbol.hasAnnotation(COMPOSABLE_ANNOTATION_ID, session)) return true
    val invoked = invokedValue ?: return false
    return invoked.resolvedType.isComposableFunctionType(session)
}

// An `invoke` call carries no name of its own, so the effect naming is read from the declarations
// around it: the type holding the `invoke` for a `fun interface`, and the invoked value itself for
// a plain function type, whose holder is only ever `FunctionN`.
private fun FirFunctionCall.isEffectCall(symbol: FirCallableSymbol<*>): Boolean {
    if (symbol.name != OperatorNameConventions.INVOKE) {
        return symbol.name.asString().endsWith(EFFECT_NAME_SUFFIX)
    }
    val invoked = invokedValue ?: return false
    if (invoked.resolvedType.classId?.shortClassName?.asString()?.endsWith(EFFECT_NAME_SUFFIX) == true) return true
    val valueName = (invoked as? FirQualifiedAccessExpression)?.calleeReference?.toResolvedCallableSymbol()?.name
    return valueName?.asString()?.endsWith(EFFECT_NAME_SUFFIX) == true
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
                "are laid out. Wrap them in a layout, or declare a layout scope — ColumnScope, " +
                "BoxScope, and the like, as a receiver or a context parameter — to hand placement " +
                "to the caller. A composable that only runs work is named with the Effect suffix.",
        )
    }
}

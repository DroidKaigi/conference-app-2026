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
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirBreakExpression
import org.jetbrains.kotlin.fir.expressions.FirCatch
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirLoop
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.FirThrowExpression
import org.jetbrains.kotlin.fir.expressions.FirTryExpression
import org.jetbrains.kotlin.fir.expressions.FirWhenExpression
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.expressions.impl.FirElseIfTrueCondition
import org.jetbrains.kotlin.fir.expressions.unwrapArgument
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.classId
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

        if (body.emissions(session).worstPath < 2) return
        reporter.reportOn(declaration.source, RootEmissionErrors.COMPOSABLE_EMITS_FLAT_SIBLINGS, context)
    }
}

private fun FirNamedFunction.declaresLayoutScope(session: FirSession): Boolean {
    val declared = listOfNotNull(receiverParameter?.typeRef) + contextParameters.map(FirValueParameter::returnTypeRef)
    return declared.any { it.toRegularClassSymbol(session)?.isLayoutScope(session) == true }
}

private fun FirClassSymbol<*>.isLayoutScope(session: FirSession): Boolean {
    if (classId in LAYOUT_SCOPE_IDS) return true
    return lookupSuperTypes(this, lookupInterfaces = true, deep = true, useSiteSession = session)
        .any { it.classId in LAYOUT_SCOPE_IDS }
}

// Emissions counted along control flow. `fallthrough` is the largest count a path carries into
// whatever follows and is null when every path leaves first; `exited` covers the paths that left the
// function; `broke` covers the paths that left the innermost loop, which the loop turns back into a
// fallthrough.
private data class Emissions(val fallthrough: Int?, val exited: Int, val broke: Int? = null) {
    val worstPath: Int get() = maxOf(fallthrough ?: 0, exited)
}

private val EMITS_NOTHING = Emissions(fallthrough = 0, exited = 0)

private fun FirStatement.emissions(session: FirSession): Emissions = when (this) {
    is FirBlock -> statements.map { it.emissions(session) }.sequence()

    is FirWhenExpression -> branchEmissions(session)

    is FirTryExpression -> (listOf(tryBlock) + catches.map(FirCatch::block)).map { it.emissions(session) }.merge()

    is FirLoop -> block.emissions(session).loopEmissions()

    is FirReturnExpression -> Emissions(fallthrough = null, exited = result.emissions(session).worstPath)

    is FirThrowExpression -> Emissions(fallthrough = null, exited = 0)

    // `continue` reaches the loop head just as the end of the body does, so it stays a fallthrough.
    is FirBreakExpression -> Emissions(fallthrough = null, exited = 0, broke = 0)

    is FirProperty -> initializer?.emissions(session) ?: EMITS_NOTHING

    is FirFunctionCall -> callEmissions(session)

    else -> EMITS_NOTHING
}

// A call that emits owns whatever its lambdas emit; one that does not passes them through, which is
// how an inline non-composable lambda such as `run` carries emissions to the call site.
private fun FirFunctionCall.callEmissions(session: FirSession): Emissions {
    if (emitsNode(session)) return Emissions(fallthrough = 1, exited = 0)
    return arguments
        .mapNotNull { (it.unwrapArgument() as? FirAnonymousFunctionExpression)?.anonymousFunction?.body }
        .map { it.emissions(session) }
        .ifEmpty { return EMITS_NOTHING }
        .merge()
}

private fun Emissions.loopEmissions(): Emissions = Emissions(
    fallthrough = maxOf(
        if ((fallthrough ?: 0) > 0) SATURATED_EMISSION_COUNT else 0,
        broke ?: 0,
    ),
    exited = exited,
)

private fun FirWhenExpression.branchEmissions(session: FirSession): Emissions {
    val paths = branches.map { it.result.emissions(session) }
    // A `when` with no else has a path that runs none of the branches.
    val missingElse = branches.none { it.condition is FirElseIfTrueCondition }
    return (if (missingElse) paths + EMITS_NOTHING else paths).merge()
}

// Alternatives: the paths never meet, so each way of ending takes the worst of its own kind.
private fun List<Emissions>.merge(): Emissions = Emissions(
    fallthrough = mapNotNull(Emissions::fallthrough).maxOrNull(),
    exited = maxOfOrNull { it.exited } ?: 0,
    broke = mapNotNull(Emissions::broke).maxOrNull(),
)

// One after another: what the earlier ones carried offsets every way the later ones can end.
private fun List<Emissions>.sequence(): Emissions {
    var reached = 0
    var left = 0
    var broken: Int? = null

    for (emissions in this) {
        left = max(left, reached + emissions.exited)
        emissions.broke?.let { broken = max(broken ?: 0, reached + it) }

        val fallthrough = emissions.fallthrough ?: return Emissions(null, left, broken)
        reached += fallthrough
    }
    return Emissions(reached, left, broken)
}

private fun FirFunctionCall.emitsNode(session: FirSession): Boolean {
    if (!resolvedType.isUnit) return false
    val symbol = calleeReference.toResolvedCallableSymbol() ?: return false
    if (declaredName(symbol).endsWith(EFFECT_NAME_SUFFIX)) return false
    if (symbol.hasAnnotation(COMPOSABLE_ANNOTATION_ID, session)) return true
    val invoked = invokedValue ?: return false
    return invoked.resolvedType.isComposableFunctionType(session)
}

// An `invoke` call is named by the type holding it, which is where a `fun interface` effect declares
// its name; every other call is named by its own callee.
private fun FirFunctionCall.declaredName(symbol: FirCallableSymbol<*>): String {
    if (symbol.name != OperatorNameConventions.INVOKE) return symbol.name.asString()
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

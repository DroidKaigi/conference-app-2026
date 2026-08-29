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
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirLoop
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.FirTryExpression
import org.jetbrains.kotlin.fir.expressions.FirWhenExpression
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.isUnit
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

private val LAYOUT_SCOPE_IDS = setOf(
    ClassId(FqName("androidx.compose.foundation.layout"), Name.identifier("BoxScope")),
    ClassId(FqName("androidx.compose.foundation.layout"), Name.identifier("RowScope")),
    ClassId(FqName("androidx.compose.foundation.layout"), Name.identifier("ColumnScope")),
    ClassId(FqName("androidx.compose.foundation.lazy"), Name.identifier("LazyItemScope")),
)

// A composable named `…Effect` runs work rather than emitting a node, the convention every effect
// in this repository and in the Compose runtime follows.
private const val EFFECT_NAME_SUFFIX = "Effect"

private const val EMISSIONS_PER_ITERATION = 2

internal object SingleRootEmissionChecker : FirSimpleFunctionChecker(MppCheckerKind.Platform) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirNamedFunction) {
        val session = context.session
        if (!declaration.symbol.hasAnnotation(COMPOSABLE_ANNOTATION_ID, session)) return
        if (declaration.declaresLayoutScopeReceiver(session)) return
        val body = declaration.body ?: return

        if (body.rootEmissionCount(session) < 2) return
        reporter.reportOn(declaration.source, RootEmissionErrors.COMPOSABLE_EMITS_FLAT_SIBLINGS, context)
    }
}

private fun FirNamedFunction.declaresLayoutScopeReceiver(session: FirSession): Boolean {
    val receiver = receiverParameter?.typeRef?.toRegularClassSymbol(session) ?: return false
    if (receiver.classId in LAYOUT_SCOPE_IDS) return true
    return lookupSuperTypes(receiver, lookupInterfaces = true, deep = true, useSiteSession = session)
        .any { it.classId in LAYOUT_SCOPE_IDS }
}

private fun FirStatement.rootEmissionCount(session: FirSession): Int = when (this) {
    is FirBlock -> statements.sumOf { it.rootEmissionCount(session) }
    is FirWhenExpression -> branches.maxOfOrNull { it.result.rootEmissionCount(session) } ?: 0
    is FirLoop -> if (block.rootEmissionCount(session) > 0) EMISSIONS_PER_ITERATION else 0
    is FirTryExpression -> maxOf(
        tryBlock.rootEmissionCount(session),
        catches.maxOfOrNull { it.block.rootEmissionCount(session) } ?: 0,
    )
    is FirReturnExpression -> result.rootEmissionCount(session)
    is FirFunctionCall -> if (emitsNode(session)) 1 else 0
    else -> 0
}

private fun FirFunctionCall.emitsNode(session: FirSession): Boolean {
    if (!resolvedType.isUnit) return false
    val symbol = calleeReference.toResolvedCallableSymbol() ?: return false
    if (symbol.name.asString().endsWith(EFFECT_NAME_SUFFIX)) return false
    if (symbol.hasAnnotation(COMPOSABLE_ANNOTATION_ID, session)) return true
    val invoked = explicitReceiver ?: dispatchReceiver ?: return false
    return invoked.resolvedType.isComposableFunctionType(session)
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
                "are laid out. Wrap them in a layout, or declare a layout scope receiver — BoxScope, " +
                "RowScope, ColumnScope, LazyItemScope, or a subtype — to hand placement to the caller.",
        )
    }
}

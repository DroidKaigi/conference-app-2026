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
import org.jetbrains.kotlin.fir.analysis.checkers.directOverriddenFunctionsSafe
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirBasicExpressionChecker
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.declaredFunctions
import org.jetbrains.kotlin.fir.declarations.getSingleMatchedExpectForActualOrNull
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.utils.isAbstract
import org.jetbrains.kotlin.fir.declarations.utils.isActual
import org.jetbrains.kotlin.fir.declarations.utils.isOverride
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionConversionKind
import org.jetbrains.kotlin.fir.expressions.FirFunctionTypeConversionExpression
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.lazyResolveToPhase
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.resolvedType

private const val PROJECT_PACKAGE_PREFIX = "io.github.droidkaigi.confsched"

internal object AbstractComposableApplierChecker : FirSimpleFunctionChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirNamedFunction) {
        val symbol = declaration.symbol
        if (!symbol.isOverride && !symbol.isActual) return
        val body = declaration.body ?: return
        val session = context.session
        val implemented = buildList {
            symbol.getSingleMatchedExpectForActualOrNull()?.let(::add)
            addAll(symbol.directOverriddenFunctionsSafe())
        }
        if (implemented.none { it.leavesApplierUndeclared(session) }) return
        if (session.composableEmissionKinds.kindOf(body) != EmissionKind.Node) return
        reporter.reportOn(declaration.source, DeclaredApplierErrors.ABSTRACT_COMPOSABLE_EMITS_WITHOUT_APPLIER)
    }
}

internal object SamConversionApplierChecker : FirBasicExpressionChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirStatement) {
        if (expression !is FirFunctionTypeConversionExpression) return
        if (expression.kind != FirFunctionConversionKind.Sam) return
        val lambda = (expression.expression as? FirAnonymousFunctionExpression)?.anonymousFunction ?: return
        val body = lambda.body ?: return
        val session = context.session
        val abstractFunction = expression.resolvedType.singleAbstractFunction(session) ?: return
        if (!abstractFunction.leavesApplierUndeclared(session)) return
        if (session.composableEmissionKinds.kindOf(body) != EmissionKind.Node) return
        reporter.reportOn(
            lambda.source,
            DeclaredApplierErrors.ABSTRACT_COMPOSABLE_EMITS_WITHOUT_APPLIER,
            positioningStrategy = SourceElementPositioningStrategies.DEFAULT,
        )
    }
}

private fun ConeKotlinType.singleAbstractFunction(session: FirSession): FirNamedFunctionSymbol? =
    toRegularClassSymbol(session)?.declaredFunctions(session)?.singleOrNull { it.isAbstract }

// The diagnostic asks for an annotation on the declaration, which only a declaration this
// repository owns can carry.
@OptIn(SymbolInternals::class)
private fun FirFunctionSymbol<*>.leavesApplierUndeclared(session: FirSession): Boolean {
    if (!callableId.packageName.asString().startsWith(PROJECT_PACKAGE_PREFIX)) return false
    if (!hasAnnotation(COMPOSABLE_ANNOTATION_ID, session)) return false
    lazyResolveToPhase(FirResolvePhase.BODY_RESOLVE)
    if (fir.body != null) return false
    return session.composableEmissionKinds.declaredKindOf(this) == null
}

object DeclaredApplierErrors : KtDiagnosticsContainer() {
    val ABSTRACT_COMPOSABLE_EMITS_WITHOUT_APPLIER by error0<PsiElement>(
        SourceElementPositioningStrategies.DECLARATION_NAME,
    )

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = DeclaredApplierErrorMessages
}

object DeclaredApplierErrorMessages : BaseDiagnosticRendererFactory() {
    @Suppress("ktlint:standard:property-naming")
    override val MAP by KtDiagnosticFactoryToRendererMap("DeclaredApplier") { map ->
        map.put(
            DeclaredApplierErrors.ABSTRACT_COMPOSABLE_EMITS_WITHOUT_APPLIER,
            "This implementation places a node, but the declaration it implements does not say so. " +
                "Annotate the expect declaration or the abstract member with `@UiComposable` so that " +
                "every caller counts it.",
        )
    }
}

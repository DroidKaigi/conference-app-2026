package io.github.droidkaigi.confsched.enforcement

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtRealSourceElementKind
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.declarations.utils.isInfix
import org.jetbrains.kotlin.fir.declarations.utils.isOperator
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.resolvedArgumentMapping
import org.jetbrains.kotlin.fir.expressions.unwrapArgument
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.coneType

// Named-argument form is the only option for every parameter but the last, so the rule reaches only
// the last one. Its source order among the other arguments does not matter: named arguments may be
// written in any order, so moving the literal out of the parentheses always leaves a legal call.
internal object ComposableLambdaMustBeTrailingChecker : FirFunctionCallChecker(MppCheckerKind.Platform) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirFunctionCall) {
        val callee = expression.calleeReference.toResolvedCallableSymbol() as? FirFunctionSymbol<*> ?: return
        if (callee is FirNamedFunctionSymbol && (callee.isInfix || callee.isOperator)) return

        val lastParameter = callee.valueParameterSymbols.lastOrNull() ?: return
        if (lastParameter.isVararg) return

        val mapping = expression.resolvedArgumentMapping ?: return
        for ((argument, parameter) in mapping) {
            if (parameter.name != lastParameter.name) continue
            if (!parameter.returnTypeRef.coneType.isComposableFunctionType(context.session)) continue

            val lambda = argument.unwrapArgument() as? FirAnonymousFunctionExpression ?: continue
            if (lambda.isTrailingLambda) continue
            if (!lambda.anonymousFunction.isLambda) continue
            val source = lambda.source ?: continue
            if (source.kind !is KtRealSourceElementKind) continue

            reporter.reportOn(
                source,
                ComposableTrailingLambdaErrors.COMPOSABLE_LAMBDA_MUST_BE_TRAILING,
                context,
            )
        }
    }
}

object ComposableTrailingLambdaErrors : KtDiagnosticsContainer() {
    val COMPOSABLE_LAMBDA_MUST_BE_TRAILING by error0<PsiElement>(SourceElementPositioningStrategies.DEFAULT)

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = ComposableTrailingLambdaErrorMessages
}

object ComposableTrailingLambdaErrorMessages : BaseDiagnosticRendererFactory() {
    @Suppress("ktlint:standard:property-naming")
    override val MAP by KtDiagnosticFactoryToRendererMap("ComposableTrailingLambda") { map ->
        map.put(
            ComposableTrailingLambdaErrors.COMPOSABLE_LAMBDA_MUST_BE_TRAILING,
            "A @Composable lambda literal bound to the last parameter must use trailing-lambda " +
                "syntax. Move it out of the parentheses ('Wrapper(…) { … }'). A value or " +
                "reference the caller already holds is passed by name as before " +
                "('content = content').",
        )
    }
}

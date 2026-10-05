package io.github.droidkaigi.confsched.enforcement

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirNamedFunctionChecker
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.isUnit
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

private const val PROJECT_PACKAGE_PREFIX = "io.github.droidkaigi.confsched"
private const val KOTLIN_FUNCTION_PREFIX = "kotlin/Function"
private const val INVOKE = "invoke"

internal object NoCallerSuppliedCallbackArgumentChecker : FirNamedFunctionChecker(MppCheckerKind.Platform) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirNamedFunction) {
        val symbol = declaration.symbol
        if (!symbol.hasAnnotation(PreviewWrapperNames.COMPOSABLE_ID, context.session)) return
        if (!symbol.callableId.packageName.asString().startsWith(PROJECT_PACKAGE_PREFIX)) return
        val body = declaration.body ?: return

        val callbacks = declaration.valueParameters.filter { it.isCallback() }
        if (callbacks.isEmpty()) return
        val callbackSymbols = callbacks.mapTo(mutableSetOf()) { it.symbol }
        val ownParameters = declaration.valueParameters.mapTo(mutableSetOf()) { it.symbol }

        val reported = mutableMapOf<FirValueParameterSymbol, String>()
        body.accept(
            object : FirVisitorVoid() {
                override fun visitElement(element: FirElement) {
                    val callback = (element as? FirFunctionCall)?.invokedCallback(callbackSymbols)
                    if (callback != null) {
                        for (argument in element.argumentList.arguments) {
                            val read = argument.callerSuppliedRead(ownParameters) ?: continue
                            reported.putIfAbsent(callback, read)
                        }
                    }
                    element.acceptChildren(this)
                }
            },
        )

        for (callback in callbacks) {
            val read = reported[callback.symbol] ?: continue
            reporter.reportOn(
                callback.source,
                CallbackArgumentErrors.NO_CALLER_SUPPLIED_CALLBACK_ARGUMENT,
                read,
                context,
            )
        }
    }
}

/**
 * A `Unit`-returning plain function type. `ComposableFunctionN` and `SuspendFunctionN` carry their own
 * class ids, so content slots and suspending handlers stay out of the check.
 */
private fun FirValueParameter.isCallback(): Boolean {
    val type = returnTypeRef.coneType
    if (type.classId?.asString()?.startsWith(KOTLIN_FUNCTION_PREFIX) != true) return false
    return type.typeArguments.lastOrNull()?.type?.isUnit == true
}

private fun FirFunctionCall.invokedCallback(callbacks: Set<FirValueParameterSymbol>): FirValueParameterSymbol? {
    if (calleeReference.toResolvedCallableSymbol()?.name?.asString() != INVOKE) return null
    val target = (explicitReceiver ?: dispatchReceiver) as? FirPropertyAccessExpression ?: return null
    if (target.explicitReceiver != null) return null
    val symbol = target.calleeReference.toResolvedCallableSymbol() as? FirValueParameterSymbol ?: return null
    return symbol.takeIf { it in callbacks }
}

/**
 * How the expression reads one of [parameters] — the parameter name, or a property-selection chain
 * rooted at it — or null when it is anything else. Anything else is a value the caller does not hold.
 */
private fun FirExpression.callerSuppliedRead(parameters: Set<FirValueParameterSymbol>): String? {
    val access = this as? FirPropertyAccessExpression ?: return null
    val callee = access.calleeReference.toResolvedCallableSymbol() ?: return null
    val receiver = access.explicitReceiver
        ?: return (callee as? FirValueParameterSymbol)?.takeIf { it in parameters }?.name?.asString()
    if (callee !is FirPropertySymbol) return null
    val root = receiver.callerSuppliedRead(parameters) ?: return null
    return "$root.${callee.name.asString()}"
}

object CallbackArgumentErrors : KtDiagnosticsContainer() {
    val NO_CALLER_SUPPLIED_CALLBACK_ARGUMENT by error1<PsiElement, String>(
        SourceElementPositioningStrategies.NAME_IDENTIFIER,
    )

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = CallbackArgumentErrorMessages
}

object CallbackArgumentErrorMessages : BaseDiagnosticRendererFactory() {
    @Suppress("ktlint:standard:property-naming")
    override val MAP by KtDiagnosticFactoryToRendererMap("CallbackArgument") { map ->
        map.put(
            CallbackArgumentErrors.NO_CALLER_SUPPLIED_CALLBACK_ARGUMENT,
            "This callback reports back ''{0}'', which the caller supplied to this composable. Drop " +
                "that parameter from the callback type and let the caller close over the value it " +
                "already holds.",
            CommonRenderers.STRING,
        )
    }
}

package io.github.droidkaigi.confsched.enforcement

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirSimpleFunctionChecker
import org.jetbrains.kotlin.fir.declarations.FirAnonymousFunction
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.visitors.FirVisitor
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

private const val MAX_COMPOSABLE_NESTING_DEPTH = 4

internal object ComposableNestingDepthChecker : FirSimpleFunctionChecker(MppCheckerKind.Platform) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirNamedFunction) {
        val session = context.session
        if (!declaration.symbol.hasAnnotation(COMPOSABLE_ANNOTATION_ID, session)) return
        val body = declaration.body ?: return

        val violations = mutableListOf<KtSourceElement>()
        body.acceptChildren(NestingVisitor(session, violations::add), NestingContext(depth = 0, enclosingCall = null))

        for (violation in violations) {
            reporter.reportOn(violation, ComposableNestingErrors.COMPOSABLE_NESTING_TOO_DEEP, context)
        }
    }
}

private data class NestingContext(val depth: Int, val enclosingCall: FirFunctionCall?)

private class NestingVisitor(
    private val session: FirSession,
    private val onViolation: (KtSourceElement) -> Unit,
) : FirVisitor<Unit, NestingContext>() {
    override fun visitElement(element: FirElement, data: NestingContext) {
        // A local function is reported against its own declaration, so its body restarts at zero.
        if (element is FirNamedFunction) return

        if (element is FirAnonymousFunction && element.emitsUi(session)) {
            val depth = data.depth + 1
            if (depth > MAX_COMPOSABLE_NESTING_DEPTH) {
                val source = data.enclosingCall?.calleeReference?.source ?: element.source
                if (source != null) onViolation(source)
                return
            }
            element.acceptChildren(this, NestingContext(depth, enclosingCall = null))
            return
        }

        val enclosingCall = element as? FirFunctionCall ?: data.enclosingCall
        element.acceptChildren(this, data.copy(enclosingCall = enclosingCall))
    }
}

private fun FirElement.emitsUi(session: FirSession): Boolean {
    var found = false
    val visitor = object : FirVisitorVoid() {
        override fun visitElement(element: FirElement) {
            if (found || element is FirNamedFunction) return
            if (element is FirFunctionCall && element.isComposableCall(session)) {
                found = true
                return
            }
            element.acceptChildren(this)
        }
    }
    acceptChildren(visitor)
    return found
}

// The compiler adds @Composable to the `invoke` of a composable function type, so invoking a
// `@Composable () -> Unit` parameter resolves to an annotated symbol like any other composable call.
private fun FirFunctionCall.isComposableCall(session: FirSession): Boolean {
    val symbol = calleeReference.toResolvedCallableSymbol() ?: return false
    return symbol.hasAnnotation(COMPOSABLE_ANNOTATION_ID, session)
}

object ComposableNestingErrors : KtDiagnosticsContainer() {
    val COMPOSABLE_NESTING_TOO_DEEP by error0<PsiElement>(SourceElementPositioningStrategies.DEFAULT)

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = ComposableNestingErrorMessages
}

object ComposableNestingErrorMessages : BaseDiagnosticRendererFactory() {
    @Suppress("ktlint:standard:property-naming")
    override val MAP by KtDiagnosticFactoryToRendererMap("ComposableNesting") { map ->
        map.put(
            ComposableNestingErrors.COMPOSABLE_NESTING_TOO_DEEP,
            "This content lambda nests UI more than $MAX_COMPOSABLE_NESTING_DEPTH levels deep. " +
                "Extract the content into its own @Composable function. Lambdas that emit no UI — " +
                "onClick, remember, coroutine bodies — are not counted.",
        )
    }
}

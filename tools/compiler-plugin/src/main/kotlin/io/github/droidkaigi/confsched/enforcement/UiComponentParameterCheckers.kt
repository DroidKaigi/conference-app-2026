package io.github.droidkaigi.confsched.enforcement

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirNamedFunctionChecker
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.declaredProperties
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.primaryConstructorIfAny
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.isUnit
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid
import org.jetbrains.kotlin.name.Name

private const val PROJECT_PACKAGE_PREFIX = "io.github.droidkaigi.confsched"

internal object UiComponentTakesWhatItReadsChecker : FirNamedFunctionChecker(MppCheckerKind.Platform) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirNamedFunction) {
        val session = context.session
        val symbol = declaration.symbol
        if (!symbol.hasAnnotation(PreviewWrapperNames.COMPOSABLE_ID, session)) return
        if (!symbol.resolvedReturnTypeRef.coneType.isUnit) return
        if (symbol.previewAnnotations(session).isPreview) return

        val packageName = symbol.callableId?.packageName?.asString() ?: return
        if (!packageName.startsWith(SoilReadConfinementNames.FEATURE_PACKAGE_PREFIX)) return
        val body = declaration.body ?: return

        for (parameter in declaration.valueParameters) {
            val classSymbol = parameter.returnTypeRef.toRegularClassSymbol(session) ?: continue
            val properties = classSymbol.stateProperties(session) ?: continue

            val visitor = ParameterUseVisitor(parameter.symbol, properties.toSet())
            body.accept(visitor)
            if (visitor.usedAsWhole) continue

            val unread = properties - visitor.readProperties
            if (unread.isEmpty()) continue
            reporter.reportOn(
                parameter.source,
                UiComponentParameterErrors.UI_COMPONENT_IGNORES_PARAMETER_PROPERTIES,
                classSymbol.name.asString(),
                unread.joinToString { it.asString() },
                context,
            )
        }
    }
}

/**
 * The properties a project-owned aggregate declares in its primary constructor, or null when the
 * type is not such an aggregate. A constructor parameter that declares no property is left out, and
 * a computed property is not state the caller supplies.
 */
private fun FirRegularClassSymbol.stateProperties(session: FirSession): List<Name>? {
    if (classKind != ClassKind.CLASS) return null
    if (!classId.packageFqName.asString().startsWith(PROJECT_PACKAGE_PREFIX)) return null
    val constructor = primaryConstructorIfAny(session) ?: return null
    val declared = declaredProperties(session).mapTo(mutableSetOf()) { it.name }
    return constructor.valueParameterSymbols.map { it.name }.filter { it in declared }
}

/**
 * Which properties the body selects off [parameter]. Any other use — passing the value on, calling a
 * member function, comparing it — makes the parameter's own shape load-bearing, and [usedAsWhole]
 * then takes it out of the check.
 */
private class ParameterUseVisitor(
    private val parameter: FirValueParameterSymbol,
    private val properties: Set<Name>,
) : FirVisitorVoid() {
    val readProperties = mutableSetOf<Name>()
    var usedAsWhole = false
        private set

    override fun visitElement(element: FirElement) {
        if (usedAsWhole) return
        if (element is FirPropertyAccessExpression) {
            if (element.explicitReceiver.isParameterReference()) {
                val read = (element.calleeReference.toResolvedCallableSymbol() as? FirPropertySymbol)?.name
                // An extension property on the type reads fields this walk cannot see.
                if (read == null || read !in properties) usedAsWhole = true else readProperties += read
                return
            }
            if (element.isParameterReference()) {
                usedAsWhole = true
                return
            }
        }
        element.acceptChildren(this)
    }

    private fun FirElement?.isParameterReference(): Boolean =
        this is FirPropertyAccessExpression &&
            explicitReceiver == null &&
            calleeReference.toResolvedCallableSymbol() == parameter
}

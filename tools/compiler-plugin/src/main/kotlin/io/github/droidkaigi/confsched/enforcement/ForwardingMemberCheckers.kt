package io.github.droidkaigi.confsched.enforcement

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error2
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.collectSupertypesWithDelegates
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.directOverriddenSymbolsSafe
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.utils.isOverride
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.expressions.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.isSubtypeOf
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
import org.jetbrains.kotlin.name.ClassId

// Interface delegation generates no member for equals, hashCode and toString unless the delegated
// interface declares one abstract, so a hand-written forward of them is not replaceable by `by`
// (FirDelegatedMemberScope.collectFunctionsFromSpecificField skips `isPublicInAny()` members).
private val ANY_MEMBER_NAMES = setOf("equals", "hashCode", "toString")

private class DelegatableSupertype(
    val type: ConeKotlinType,
    val symbol: FirRegularClassSymbol,
    val ancestry: Set<ClassId>,
)

private class Forward(val holder: FirPropertySymbol, val callee: FirCallableSymbol<*>)

internal object ForwardingMemberMustDelegateChecker : FirClassChecker(MppCheckerKind.Platform) {

    @OptIn(DirectDeclarationsAccess::class)
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirClass) {
        val session = context.session
        val supertypes = declaration.delegatableSupertypes(session)
        if (supertypes.isEmpty()) return

        val holders = declaration.declarations
            .filterIsInstance<FirProperty>()
            .filter { it.holdsConstructorArgument() }
            .mapTo(mutableSetOf()) { it.symbol }
        if (holders.isEmpty()) return

        for (member in declaration.declarations) {
            if (member !is FirCallableDeclaration || !member.isOverride) continue
            val forward = when (member) {
                is FirProperty -> member.forward()
                is FirNamedFunction -> member.forward()
                else -> null
            } ?: continue
            if (forward.holder !in holders) continue
            val forwardedId = forward.callee.callableId ?: continue
            if (member.symbol.directOverriddenSymbolsSafe().none { it.callableId == forwardedId }) continue

            val declaringInterface = forwardedId.classId ?: continue
            val target = supertypes.firstOrNull { supertype ->
                declaringInterface in supertype.ancestry &&
                    forward.holder.resolvedReturnType.isSubtypeOf(supertype.type, session)
            } ?: continue

            val source = member.source ?: continue
            reporter.reportOn(
                source,
                ForwardingMemberErrors.FORWARDING_MEMBER_MUST_DELEGATE,
                target.symbol.name.asString(),
                forward.holder.name.asString(),
                context,
            )
        }
    }
}

private fun FirClass.delegatableSupertypes(session: FirSession): List<DelegatableSupertype> =
    collectSupertypesWithDelegates()
        .filterValues { delegateField -> delegateField == null }
        .keys
        .mapNotNull { typeRef ->
            val symbol = typeRef.toRegularClassSymbol(session) ?: return@mapNotNull null
            if (symbol.classKind != ClassKind.INTERFACE) return@mapNotNull null
            DelegatableSupertype(typeRef.coneType, symbol, symbol.ancestry(session))
        }

private fun FirRegularClassSymbol.ancestry(session: FirSession): Set<ClassId> =
    lookupSuperTypes(this, lookupInterfaces = true, deep = true, useSiteSession = session)
        .mapNotNullTo(mutableSetOf(classId)) { it.classId }

// `by` evaluates its expression once at construction while a hand-written forward reads its source
// at every call, so only a source the constructor fixed makes the two equivalent.
private fun FirProperty.holdsConstructorArgument(): Boolean {
    if (isVar || delegate != null || getter?.body != null) return false
    return (initializer as? FirPropertyAccessExpression)?.toResolvedCallableSymbol() is FirValueParameterSymbol
}

private fun FirProperty.forward(): Forward? {
    if (isVar || receiverParameter != null) return null
    val access = getter?.body.singleExpression() as? FirPropertyAccessExpression ?: return null
    val holder = access.explicitReceiver.holderRead() ?: return null
    return Forward(holder, access.toResolvedCallableSymbol() ?: return null)
}

private fun FirNamedFunction.forward(): Forward? {
    if (receiverParameter != null || name.asString() in ANY_MEMBER_NAMES) return null
    val call = body.singleExpression() as? FirFunctionCall ?: return null
    val holder = call.explicitReceiver.holderRead() ?: return null
    if (!call.passesOnlyParametersOf(this)) return null
    return Forward(holder, call.toResolvedCallableSymbol() ?: return null)
}

private fun FirBlock?.singleExpression(): FirExpression? =
    when (val statement = this?.statements?.singleOrNull()) {
        is FirReturnExpression -> statement.result
        is FirExpression -> statement
        else -> null
    }

private fun FirExpression?.holderRead(): FirPropertySymbol? {
    val access = this as? FirPropertyAccessExpression ?: return null
    if (access.explicitReceiver != null) return null
    return access.toResolvedCallableSymbol() as? FirPropertySymbol
}

private fun FirFunctionCall.passesOnlyParametersOf(member: FirNamedFunction): Boolean {
    if (arguments.size != member.valueParameters.size) return false
    return arguments.zip(member.valueParameters).all { (argument, parameter) ->
        (argument as? FirPropertyAccessExpression)?.toResolvedCallableSymbol() == parameter.symbol
    }
}

object ForwardingMemberErrors : KtDiagnosticsContainer() {
    val FORWARDING_MEMBER_MUST_DELEGATE by error2<PsiElement, String, String>(
        SourceElementPositioningStrategies.DECLARATION_NAME,
    )

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = ForwardingMemberErrorMessages
}

object ForwardingMemberErrorMessages : BaseDiagnosticRendererFactory() {
    @Suppress("ktlint:standard:property-naming")
    override val MAP by KtDiagnosticFactoryToRendererMap("ForwardingMember") { map ->
        map.put(
            ForwardingMemberErrors.FORWARDING_MEMBER_MUST_DELEGATE,
            "This member only forwards to ''{1}''. Declare the supertype as ''{0} by {1}'' and " +
                "delete this member.",
            CommonRenderers.STRING,
            CommonRenderers.STRING,
        )
    }
}

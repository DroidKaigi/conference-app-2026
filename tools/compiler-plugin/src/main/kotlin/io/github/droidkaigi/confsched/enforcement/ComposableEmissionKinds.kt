package io.github.droidkaigi.confsched.enforcement

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.caches.FirCache
import org.jetbrains.kotlin.fir.caches.firCachesFactory
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId
import org.jetbrains.kotlin.fir.declarations.getStringArgument
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassLikeSymbol
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.expressions.unwrapArgument
import org.jetbrains.kotlin.fir.extensions.FirExtensionSessionComponent
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.lazyResolveToPhase
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

private val COMPOSE_RUNTIME_PACKAGE = FqName("androidx.compose.runtime")

private val COMPOSABLE_TARGET_ID = ClassId(COMPOSE_RUNTIME_PACKAGE, Name.identifier("ComposableTarget"))
private val COMPOSABLE_INFERRED_TARGET_ID =
    ClassId(COMPOSE_RUNTIME_PACKAGE, Name.identifier("ComposableInferredTarget"))
private val COMPOSABLE_OPEN_TARGET_ID = ClassId(COMPOSE_RUNTIME_PACKAGE, Name.identifier("ComposableOpenTarget"))
private val COMPOSABLE_TARGET_MARKER_ID = ClassId(COMPOSE_RUNTIME_PACKAGE, Name.identifier("ComposableTargetMarker"))

private val SCHEME_ARGUMENT = Name.identifier("scheme")

// A scheme opens with `[`, then its target item, then the parameter schemes, the result, or the close.
private const val SCHEME_TARGET_TERMINATORS = "[]:*"
private const val SCHEME_ANONYMOUS_OPEN = "_"

/**
 * What a call does to the composition it runs in.
 */
internal enum class EmissionKind {
    /** Bound to an applier: the call places one node, and its content lambdas belong to that node. */
    Node,

    /** Open: the call places nothing, and its content lambdas emit into the caller's applier. */
    Transparent,

    /** Neither: the call runs work only. */
    None,
}

/**
 * Reads the applier a composable is bound to, which the Compose compiler records as an annotation on
 * every composable it compiles. A callee in the module being compiled has no such annotation yet —
 * the inference runs in the IR backend — so its body is read with the same rules instead.
 */
internal class ComposableEmissionKinds(session: FirSession) : FirExtensionSessionComponent(session) {
    private val kinds: FirCache<FirCallableSymbol<*>, EmissionKind, Nothing?> =
        session.firCachesFactory.createCache { symbol, _ -> symbol.computeKind() }

    private val underComputation = ThreadLocal.withInitial { mutableSetOf<FirCallableSymbol<*>>() }

    fun kindOf(body: FirBlock): EmissionKind {
        val scan = EmissionKindScan(this, session)
        body.accept(scan)
        return scan.kind
    }

    fun declaredKindOf(symbol: FirCallableSymbol<*>): EmissionKind? = symbol.declaredKind()

    fun kindOf(call: FirFunctionCall): EmissionKind {
        val symbol = call.calleeReference.toResolvedCallableSymbol() ?: return EmissionKind.None
        val active = underComputation.get()
        // Mutual recursion has no answer to reach; None leaves the count to the calls around it.
        if (!active.add(symbol)) return EmissionKind.None
        return try {
            kinds.getValue(symbol, null)
        } finally {
            active.remove(symbol)
        }
    }

    @OptIn(SymbolInternals::class)
    private fun FirCallableSymbol<*>.computeKind(): EmissionKind {
        declaredKind()?.let { return it }
        if (!hasAnnotation(COMPOSABLE_ANNOTATION_ID, session)) return EmissionKind.None
        if (!origin.fromSource) return EmissionKind.None
        lazyResolveToPhase(FirResolvePhase.BODY_RESOLVE)
        val body = (fir as? FirFunction)?.body ?: return EmissionKind.None
        return kindOf(body)
    }

    private fun FirCallableSymbol<*>.declaredKind(): EmissionKind? {
        val annotations = resolvedAnnotationsWithClassIds
        if (annotations.getAnnotationByClassId(COMPOSABLE_TARGET_ID, session) != null) return EmissionKind.Node
        if (annotations.any { it.marksAnApplier() }) return EmissionKind.Node
        annotations.getAnnotationByClassId(COMPOSABLE_INFERRED_TARGET_ID, session)
            ?.getStringArgument(SCHEME_ARGUMENT)
            ?.let { return if (it.namesAnApplier()) EmissionKind.Node else EmissionKind.Transparent }
        if (annotations.getAnnotationByClassId(COMPOSABLE_OPEN_TARGET_ID, session) != null) {
            return EmissionKind.Transparent
        }
        return null
    }

    private fun FirAnnotation.marksAnApplier(): Boolean =
        toAnnotationClassLikeSymbol(session)?.hasAnnotation(COMPOSABLE_TARGET_MARKER_ID, session) == true
}

internal val FirSession.composableEmissionKinds: ComposableEmissionKinds by FirSession.sessionComponentAccessor()

// A scheme names an applier where it is bound and carries an index where it stays open.
private fun String.namesAnApplier(): Boolean {
    val target = removePrefix("[").takeWhile { it !in SCHEME_TARGET_TERMINATORS }.trim()
    return target.isNotEmpty() && target != SCHEME_ANONYMOUS_OPEN && target.toIntOrNull() == null
}

// Reaching a node makes the whole function one; short of that, handing content on to a call that
// emits into the caller's applier — or invoking content the caller supplied — makes it transparent.
private class EmissionKindScan(
    private val kinds: ComposableEmissionKinds,
    private val session: FirSession,
) : FirVisitorVoid() {
    private var reachesNode = false
    private var passesContentOn = false

    val kind: EmissionKind
        get() = when {
            reachesNode -> EmissionKind.Node
            passesContentOn -> EmissionKind.Transparent
            else -> EmissionKind.None
        }

    override fun visitElement(element: FirElement) {
        if (reachesNode) return
        element.acceptChildren(this)
    }

    override fun visitFunctionCall(functionCall: FirFunctionCall) {
        if (reachesNode) return
        if (functionCall.invokesComposableValue(session)) {
            passesContentOn = true
        } else {
            when (kinds.kindOf(functionCall)) {
                EmissionKind.Node -> {
                    reachesNode = true
                    return
                }

                EmissionKind.Transparent -> if (functionCall.hasLambdaArgument()) passesContentOn = true

                EmissionKind.None -> Unit
            }
        }
        functionCall.acceptChildren(this)
    }
}

private fun FirFunctionCall.hasLambdaArgument(): Boolean =
    arguments.any { it.unwrapArgument() is FirAnonymousFunctionExpression }

// A value of composable function type states no content of its own, so whoever supplies it decides.
internal fun FirFunctionCall.invokesComposableValue(session: FirSession): Boolean =
    invokedValue?.resolvedType?.isComposableFunctionType(session) == true

// A composable parameter has no declaration to annotate, so its name is what states that it places
// nothing: the `Effect` suffix on the value that is invoked.
internal fun FirFunctionCall.invokesValueNamedAsEffect(): Boolean {
    val valueName = (invokedValue as? FirQualifiedAccessExpression)?.calleeReference?.toResolvedCallableSymbol()?.name
    return valueName?.endsWithEffect() == true
}

private fun Name.endsWithEffect(): Boolean = asString().endsWith(EFFECT_NAME_SUFFIX)

private const val EFFECT_NAME_SUFFIX = "Effect"

private val FirFunctionCall.invokedValue: FirExpression?
    get() = explicitReceiver ?: dispatchReceiver

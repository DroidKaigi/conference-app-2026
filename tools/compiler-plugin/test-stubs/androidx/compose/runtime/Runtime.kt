package androidx.compose.runtime

import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KProperty

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.VALUE_PARAMETER,
)
@Retention(AnnotationRetention.BINARY)
annotation class Composable

@Composable
fun LaunchedEffect(key: Any?, block: suspend () -> Unit) {
}

@Composable
fun <T> remember(calculation: () -> T): T = calculation()

@Composable
fun <T> remember(key1: Any?, calculation: () -> T): T = calculation()

@Composable
fun rememberCoroutineScope(): CoroutineScope = throw UnsupportedOperationException()

@Composable
fun <T> rememberUpdatedState(newValue: T): State<T> = mutableStateOf(newValue)

interface State<out T> {
    val value: T
}

interface MutableState<T> : State<T> {
    override var value: T

    operator fun component1(): T

    operator fun component2(): (T) -> Unit
}

operator fun <T> State<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value

operator fun <T> MutableState<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
}

fun <T> mutableStateOf(value: T): MutableState<T> = throw UnsupportedOperationException()

fun <T> derivedStateOf(calculation: () -> T): State<T> = throw UnsupportedOperationException()

class CompositionLocal<T>

class ProvidedValue<T>

infix fun <T> CompositionLocal<T>.provides(value: T): ProvidedValue<T> = ProvidedValue()

@Composable
@ComposableInferredTarget(scheme = "[0[0]]")
fun CompositionLocalProvider(vararg values: ProvidedValue<*>, content: @Composable () -> Unit) {
}

@Composable
fun SideEffect(effect: () -> Unit) {
}

class DisposableEffectResult

class DisposableEffectScope {
    fun onDispose(onDisposeEffect: () -> Unit): DisposableEffectResult = DisposableEffectResult()
}

@Composable
fun DisposableEffect(key1: Any?, effect: DisposableEffectScope.() -> DisposableEffectResult) {
}

@Composable
@ComposableInferredTarget(scheme = "[0[0]]")
fun <T> key(vararg keys: Any?, block: @Composable () -> T): T = throw UnsupportedOperationException()

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
annotation class ComposableTarget(val applier: String)

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class ComposableTargetMarker(val description: String = "")

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.BINARY)
annotation class ComposableInferredTarget(val scheme: String)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
annotation class ComposableOpenTarget(val index: Int)

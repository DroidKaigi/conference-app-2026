package io.github.droidkaigi.confsched.core.testing

import kotlinx.coroutines.CompletableDeferred

// A key fake delegates to buildQueryKey / buildSubscriptionKey, whose expression cannot reference
// the class under construction — the data a test sets therefore lives in a constructor parameter.
class FakeFixture<T>(initial: T) {
    private var gate: CompletableDeferred<Unit>? = null
    private var failure: Throwable? = null

    var value: T = initial
        private set

    fun set(value: T) {
        this.value = value
    }

    fun hold() {
        gate = CompletableDeferred()
    }

    fun release() {
        gate?.complete(Unit)
        gate = null
    }

    fun failWith(throwable: Throwable) {
        failure = throwable
    }

    suspend fun await(): T {
        gate?.await()
        failure?.let { throw it }
        return value
    }
}

// The surface a Robot drives: `hold` keeps the boundary on its loading fallback until `release`,
// `failWith` sends it to the error fallback.
abstract class FakeKeyControl<T>(private val fixture: FakeFixture<T>) {
    fun set(value: T) = fixture.set(value)

    fun hold() = fixture.hold()

    fun release() = fixture.release()

    fun failWith(throwable: Throwable) = fixture.failWith(throwable)
}

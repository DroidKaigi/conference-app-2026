package io.github.droidkaigi.confsched.core.testing

import kotlinx.coroutines.channels.Channel

// A MutationKey fake delegates to buildMutationKey, whose expression cannot reference the class
// under construction — the mutable behaviour therefore lives in a constructor parameter.
class FakeMutationState<S, R>(initialResult: R) {
    val invocations = Channel<S>(Channel.UNLIMITED)
    private var failure: Throwable? = null
    private var nextResult: R = initialResult

    suspend fun record(value: S): R {
        invocations.send(value)
        failure?.let { throw it }
        return nextResult
    }

    fun complete(result: R) {
        nextResult = result
    }

    fun failWith(throwable: Throwable) {
        failure = throwable
    }
}

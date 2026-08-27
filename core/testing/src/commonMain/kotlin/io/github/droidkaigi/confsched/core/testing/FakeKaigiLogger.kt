package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import kotlinx.coroutines.channels.Channel

@Inject
@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class)
class FakeKaigiLogger : KaigiLogger {
    val debugMessages = Channel<String>(Channel.UNLIMITED)
    val errors = Channel<Throwable?>(Channel.UNLIMITED)

    override fun debug(message: () -> String) {
        debugMessages.trySend(message())
    }

    override fun info(message: () -> String) = Unit

    override fun warn(message: () -> String) = Unit

    override fun error(throwable: Throwable?, message: () -> String) {
        errors.trySend(throwable)
    }
}

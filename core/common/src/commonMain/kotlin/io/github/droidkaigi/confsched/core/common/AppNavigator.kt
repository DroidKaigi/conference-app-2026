package io.github.droidkaigi.confsched.core.common

import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow

sealed interface NavCommand {
    data class Push(val key: NavKey) : NavCommand

    data class Pop(val origin: NavKey?) : NavCommand

    data class MoveToTop(val key: NavKey) : NavCommand

    data class ReplaceTop(val key: NavKey) : NavCommand
}

@Inject
@SingleIn(UiScope::class)
class AppNavigator(private val logger: KaigiLogger) : Navigator {
    private val commandChannel = Channel<NavCommand>(Channel.BUFFERED)
    val commands: Flow<NavCommand> = commandChannel.receiveAsFlow()

    // No replay: a screen that starts collecting after a tap must not scroll for a stale event.
    val reselections: Flow<NavKey>
        field = MutableSharedFlow<NavKey>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    fun goTo(key: NavKey) {
        logger.debug { "goTo $key" }
        commandChannel.trySend(NavCommand.Push(key))
    }

    override fun back(origin: NavKey?) {
        logger.debug { "back from $origin" }
        commandChannel.trySend(NavCommand.Pop(origin = origin))
    }

    fun moveToTop(key: NavKey) {
        logger.debug { "moveToTop $key" }
        commandChannel.trySend(NavCommand.MoveToTop(key))
    }

    fun replaceTop(key: NavKey) {
        logger.debug { "replaceTop $key" }
        commandChannel.trySend(NavCommand.ReplaceTop(key))
    }

    internal fun reselect(key: NavKey) {
        logger.debug { "reselect $key" }
        reselections.tryEmit(key)
    }
}

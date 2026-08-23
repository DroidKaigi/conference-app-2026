package io.github.droidkaigi.confsched.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.retain.retain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class ScreenChannel<Action, ActionResult>(
    internal val actions: Channel<Action> = Channel(Channel.BUFFERED),
    internal val results: Channel<ActionResult> = Channel(Channel.BUFFERED),
) {
    context(_: ScreenContext)
    fun send(action: Action) {
        actions.trySend(action)
    }

    context(_: PresenterContext)
    suspend fun emit(result: ActionResult) {
        results.send(result)
    }
}

// Retained (not remembered) so buffered, not-yet-consumed actions/results survive transient
// destruction of the entry instead of being dropped with a recreated channel.
@Composable
fun <A, R> retainScreenChannel(): ScreenChannel<A, R> = retain { ScreenChannel() }

@Composable
context(presenterContext: PresenterContext)
fun <A> ActionEffect(channel: ScreenChannel<A, *>, block: suspend (A) -> Unit) {
    // The effect outlives the composition that launched it, so it reads the block through a
    // state: the one it was launched with holds the UiState of that first composition.
    val currentBlock by rememberUpdatedState(block)
    val logger = presenterContext.logger
    LaunchedEffect(channel) {
        for (action in channel.actions) {
            consume(logger, action) { currentBlock(action) }
        }
    }
}

@Composable
context(screenContext: ScreenContext)
fun <R> ActionResultEffect(channel: ScreenChannel<*, R>, block: suspend (R) -> Unit) {
    val currentBlock by rememberUpdatedState(block)
    val logger = screenContext.logger
    LaunchedEffect(channel) {
        for (result in channel.results) {
            consume(logger, result) { currentBlock(result) }
        }
    }
}

/**
 * Handles one event in a child of the effect's scope.
 *
 * A handler suspends for as long as the work it drives takes — a mutation in flight, a snackbar
 * still on screen — so handling in the receiving loop would hold every later event behind it.
 * A handler that throws is a defect in the screen rather than a condition the user can act on:
 * reporting it keeps the loop, and the rest of the screen, alive.
 *
 * The child starts undispatched so a handler runs up to its first suspension before the next
 * event is taken: handlers that never suspend complete in the order their events were sent,
 * whatever the dispatcher.
 */
private fun CoroutineScope.consume(logger: KaigiLogger, event: Any?, block: suspend () -> Unit) {
    launch(start = CoroutineStart.UNDISPATCHED) {
        try {
            block()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (failure: Throwable) {
            // The type alone, so a payload never reaches the crash report.
            logger.error(failure) { "Unhandled failure while consuming ${event?.let { it::class.simpleName }}" }
        }
    }
}

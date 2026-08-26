package io.github.droidkaigi.confsched.core.testing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.currentComposer
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.common.context
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import soil.query.SwrCachePlus
import soil.query.compose.LocalMutationClient
import soil.query.compose.LocalQueryClient
import soil.query.compose.LocalSubscriptionClient
import soil.query.compose.LocalSwrClient

class PresenterTestScope<A, R, S>(
    private val screenContext: ScreenContext,
    private val screenChannel: ScreenChannel<A, R>,
    val uiStates: ReceiveTurbine<S>,
    val results: ReceiveTurbine<R>,
) {
    fun send(action: A) = context(screenContext) { screenChannel.send(action) }
}

fun <C : PresenterContext, A, R, S> runPresenterTest(
    presenterContext: C,
    presenter: @Composable context(C) (ScreenChannel<A, R>) -> S,
    validate: suspend PresenterTestScope<A, R, S>.() -> Unit,
): TestResult = runTest {
    val screenContext = object : ScreenContext {}
    val screenChannel = ScreenChannel<A, R>()
    val results = Channel<R>(Channel.BUFFERED)
    val uiStateFlow = moleculeFlow(RecompositionMode.Immediate) {
        val client = SwrCachePlus(backgroundScope)
        compositionLocalProviderWithReturnValue(
            LocalSwrClient provides client,
            LocalQueryClient provides client,
            LocalMutationClient provides client,
            LocalSubscriptionClient provides client,
        ) {
            context(screenContext) {
                ActionResultEffect(screenChannel, results::send)
            }
            context(presenterContext) {
                presenter(screenChannel)
            }
        }
    }
    turbineScope {
        val uiStates = uiStateFlow.distinctUntilChanged().testIn(backgroundScope)
        val resultsTurbine = results.receiveAsFlow().testIn(backgroundScope)
        PresenterTestScope(screenContext, screenChannel, uiStates, resultsTurbine).validate()
        uiStates.cancelAndIgnoreRemainingEvents()
        resultsTurbine.cancelAndIgnoreRemainingEvents()
    }
}

@OptIn(InternalComposeApi::class)
@Composable
private fun <T> compositionLocalProviderWithReturnValue(
    vararg values: ProvidedValue<*>,
    content: @Composable () -> T,
): T {
    currentComposer.startProviders(values)
    val result = content()
    currentComposer.endProviders()
    return result
}

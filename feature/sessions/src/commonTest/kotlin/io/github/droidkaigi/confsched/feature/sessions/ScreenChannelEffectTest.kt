package io.github.droidkaigi.confsched.feature.sessions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.testing.FakeKaigiLogger
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ScreenChannelEffectTest {

    private val logger = FakeKaigiLogger()
    private val presenterContext = object : PresenterContext {
        override val logger: KaigiLogger = this@ScreenChannelEffectTest.logger
    }
    private val screenContext = object : ScreenContext {
        override val logger: KaigiLogger = this@ScreenChannelEffectTest.logger
    }

    @Test
    fun an_action_still_being_handled_does_not_hold_up_the_next_one() = runTest {
        val channel = ScreenChannel<String, Nothing>()
        val handled = Channel<String>(Channel.UNLIMITED)

        backgroundScope.launch {
            moleculeFlow(RecompositionMode.Immediate) {
                context(presenterContext) {
                    ActionEffect(channel) { action ->
                        if (action == "never returns") awaitCancellation() else handled.send(action)
                    }
                }
            }.collect {}
        }
        runCurrent()

        context(screenContext) {
            channel.send("never returns")
            channel.send("the next one")
        }
        runCurrent()

        assertEquals("the next one", handled.receive())
    }

    @Test
    fun actions_whose_handlers_never_suspend_complete_in_the_order_sent() = runTest {
        val channel = ScreenChannel<String, Nothing>()
        val handled = mutableListOf<String>()

        backgroundScope.launch {
            moleculeFlow(RecompositionMode.Immediate) {
                context(presenterContext) {
                    ActionEffect(channel) { action -> handled.add(action) }
                }
            }.collect {}
        }
        runCurrent()

        context(screenContext) {
            channel.send("first")
            channel.send("second")
            channel.send("third")
        }
        runCurrent()

        assertEquals(listOf("first", "second", "third"), handled)
    }

    @Test
    fun an_action_that_throws_is_reported_and_leaves_the_screen_running() = runTest {
        val channel = ScreenChannel<String, Nothing>()
        val handled = Channel<String>(Channel.UNLIMITED)

        backgroundScope.launch {
            moleculeFlow(RecompositionMode.Immediate) {
                context(presenterContext) {
                    ActionEffect(channel) { action ->
                        if (action == "throws") error("handler defect") else handled.send(action)
                    }
                }
            }.collect {}
        }
        runCurrent()

        context(screenContext) {
            channel.send("throws")
            channel.send("the next one")
        }
        runCurrent()

        assertIs<IllegalStateException>(logger.errors.receive())
        assertEquals("the next one", handled.receive())
    }

    @Test
    fun an_action_is_handled_against_the_latest_composition() = runTest {
        val channel = ScreenChannel<String, Nothing>()
        val handled = Channel<Int>(Channel.UNLIMITED)
        var revision by mutableStateOf(1)

        backgroundScope.launch {
            moleculeFlow(RecompositionMode.Immediate) {
                val current = revision
                context(presenterContext) {
                    ActionEffect(channel) { handled.send(current) }
                }
            }.collect {}
        }
        runCurrent()

        revision = 2
        runCurrent()
        context(screenContext) { channel.send("read the revision") }
        runCurrent()

        assertEquals(2, handled.receive())
    }
}

package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Prizes
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.testing.Robot
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class StampCollectingScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<StampCollectingScreenTestGraph>()
    private var backCount = 0

    fun setupPrizes(prizes: Prizes) {
        graph.prizesQueryKey.set(prizes)
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                StampCollectingScreenRoot(onNavigateBack = { backCount++ })
            }
        }
    }

    fun scrollToText(text: String) {
        composeUiTest.onNode(hasScrollAction()).performScrollToNode(hasText(text))
        composeUiTest.waitForIdle()
    }

    fun clickBack() {
        composeUiTest.onNodeWithContentDescription("Back").performClick()
        composeUiTest.waitForIdle()
    }

    fun checkTextDisplayed(text: String) {
        composeUiTest.onNodeWithText(text).assertIsDisplayed()
    }

    fun checkBackInvoked(times: Int) {
        assertEquals(times, backCount)
    }
}

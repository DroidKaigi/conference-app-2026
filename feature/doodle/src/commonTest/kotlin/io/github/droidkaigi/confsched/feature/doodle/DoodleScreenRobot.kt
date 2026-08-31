package io.github.droidkaigi.confsched.feature.doodle

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import dev.zacsweers.metro.createGraphFactory
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleEdit
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.testing.Robot
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DoodleScreenRobot(composeUiTest: ComposeUiTest, private val target: DoodleTarget) : Robot(composeUiTest) {

    private val graph = createGraphFactory<DoodleScreenTestGraph.Factory>().create(target)
    private var backCount = 0

    fun setupSavedDoodle(doodle: Doodle) {
        graph.doodlesSubscriptionKey.set(persistentMapOf(target to doodle))
    }

    fun setupCard(card: ProfileCard?) {
        graph.profileCardSubscriptionKey.set(card)
    }

    fun setupFailingSave() {
        graph.doodleMutationKey.failWith(IllegalStateException("boom"))
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                DoodleScreenRoot(onNavigateBack = { backCount++ })
            }
        }
    }

    // The canvas carries no semantics of its own, so the stroke is drawn through the window: the
    // gesture stays in the upper half, which the canvas fills below the app bar.
    fun drawStroke() {
        composeUiTest.onRoot().performTouchInput {
            val y = centerY * 0.8f
            down(Offset(centerX - width * 0.2f, y))
            moveTo(Offset(centerX, y + height * 0.05f))
            moveTo(Offset(centerX + width * 0.2f, y))
            up()
        }
        composeUiTest.waitForIdle()
    }

    fun clickButton(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).performClick()
        composeUiTest.waitForIdle()
    }

    fun clickBack() {
        composeUiTest.onNodeWithContentDescription("Back").performClick()
        composeUiTest.waitForIdle()
    }

    fun checkButtonDisplayed(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).assertIsDisplayed()
    }

    fun checkButtonEnabled(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).assertIsEnabled()
    }

    fun checkButtonDisabled(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).assertIsNotEnabled()
    }

    fun checkSavedDoodle(doodle: Doodle) {
        assertEquals(
            DoodleEdit(target = target, doodle = doodle),
            graph.doodleMutationKey.invocations.tryReceive().getOrThrow(),
        )
    }

    fun checkNothingSaved() {
        assertTrue(graph.doodleMutationKey.invocations.tryReceive().isFailure, "a save reached the data layer")
    }

    fun checkBackInvoked(times: Int) {
        assertEquals(times, backCount)
    }

    private fun text(label: StringResource): String = runBlocking { getString(label) }
}

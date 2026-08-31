package io.github.droidkaigi.confsched.feature.about

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleEdit
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.core.ui.DOODLE_CANVAS_FRAME_TEST_TAG
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class AboutScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<AboutScreenTestGraph>()
    private val invocations = mutableMapOf<String, Int>()

    private fun record(name: String): () -> Unit = {
        invocations[name] = (invocations[name] ?: 0) + 1
    }

    fun setupSavedWallDoodle(doodle: Doodle) {
        graph.doodlesSubscriptionKey.set(persistentMapOf(DoodleTarget.AboutWall to doodle))
    }

    fun setupFailingSave() {
        graph.doodleMutationKey.failWith(IllegalStateException("boom"))
    }

    fun setupContent(isDebugMenuAvailable: Boolean) {
        setScreenContent {
            context(graph.screenContext) {
                AboutScreenRoot(
                    onOpenVenueWithMap = record(VENUE),
                    onNavigateToSponsors = record(SPONSORS),
                    onNavigateToContributors = record(CONTRIBUTORS),
                    onNavigateToStaff = record(STAFF),
                    onNavigateToLicenses = record(LICENSES),
                    onOpenCodeOfConduct = record(CODE_OF_CONDUCT),
                    onOpenPrivacyPolicy = record(PRIVACY),
                    onNavigateToSettings = record(SETTINGS),
                    onOpenYoutube = record(YOUTUBE),
                    onOpenX = record(X),
                    onOpenMedium = record(MEDIUM),
                    isDebugMenuAvailable = isDebugMenuAvailable,
                    onNavigateToDebug = record(DEBUG),
                )
            }
        }
    }

    fun clickRow(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).performScrollTo().performClick()
        composeUiTest.waitForIdle()
    }

    fun clickSign(description: StringResource) {
        composeUiTest.onNodeWithContentDescription(text(description)).performScrollTo().performClick()
        composeUiTest.waitForIdle()
    }

    fun clickSocial(description: String) {
        composeUiTest.onNodeWithContentDescription(description).performScrollTo().performClick()
        composeUiTest.waitForIdle()
    }

    /** Draws a horizontal stroke through the centre of the wall canvas. */
    fun drawStroke() {
        composeUiTest.onAllNodesWithTag(DOODLE_CANVAS_FRAME_TEST_TAG)[0].performTouchInput {
            down(Offset(centerX - width * STROKE_HALF_SPAN_FRACTION, centerY))
            moveTo(Offset(centerX, centerY))
            moveTo(Offset(centerX + width * STROKE_HALF_SPAN_FRACTION, centerY))
            up()
        }
        composeUiTest.waitForIdle()
    }

    fun clickButton(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkRowDisplayed(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).performScrollTo().assertIsDisplayed()
    }

    fun checkRowDoesNotExist(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).assertDoesNotExist()
    }

    fun checkButtonDisplayed(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).assertIsDisplayed()
    }

    fun checkTextDoesNotExist(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).assertDoesNotExist()
    }

    fun checkInvokedOnce(name: String) {
        assertEquals(1, invocations[name] ?: 0, "$name should be invoked exactly once")
    }

    fun checkSavedWallDoodle(doodle: Doodle) {
        assertEquals(
            listOf(DoodleEdit(target = DoodleTarget.AboutWall, doodle = doodle)),
            graph.doodleMutationKey.invocations.tryReceive().getOrThrow(),
        )
    }

    fun checkSavedWallStrokeCount(count: Int) {
        val edits = graph.doodleMutationKey.invocations.tryReceive().getOrThrow()
        assertEquals(count, edits.single().doodle.strokes.size)
    }

    fun checkNothingSaved() {
        assertTrue(graph.doodleMutationKey.invocations.tryReceive().isFailure, "a save reached the data layer")
    }

    // The test environment picks its own locale, so labels are resolved from the resources the UI
    // draws rather than hard-coded, and the assertions hold whichever locale runs.
    private fun text(label: StringResource): String = runBlocking { getString(label) }

    companion object {
        const val VENUE = "venue"
        const val SPONSORS = "sponsors"
        const val CONTRIBUTORS = "contributors"
        const val STAFF = "staff"
        const val LICENSES = "licenses"
        const val CODE_OF_CONDUCT = "codeOfConduct"
        const val PRIVACY = "privacy"
        const val SETTINGS = "settings"
        const val YOUTUBE = "youtube"
        const val X = "x"
        const val MEDIUM = "medium"
        const val DEBUG = "debug"
    }
}

private const val STROKE_HALF_SPAN_FRACTION = 0.2f

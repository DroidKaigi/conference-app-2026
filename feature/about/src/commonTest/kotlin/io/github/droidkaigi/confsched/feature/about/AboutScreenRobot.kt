package io.github.droidkaigi.confsched.feature.about

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleEdit
import io.github.droidkaigi.confsched.core.model.DoodleInk
import io.github.droidkaigi.confsched.core.model.DoodleStroke
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.core.ui.DOODLE_CANVAS_FRAME_TEST_TAG
import io.github.droidkaigi.confsched.core.ui.DOODLE_OUTLINE_TOGGLE_TEST_TAG
import io.github.droidkaigi.confsched.core.ui.doodleInkSwatchTestTag
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

    fun clickBandInk() = clickInk(DoodleInk.Band)

    fun clickPaperInk() = clickInk(DoodleInk.Paper)

    private fun clickInk(ink: DoodleInk) {
        composeUiTest.onNodeWithTag(doodleInkSwatchTestTag(ink)).performClick()
        composeUiTest.waitForIdle()
    }

    fun clickOutlineToggle() {
        composeUiTest.onNodeWithTag(DOODLE_OUTLINE_TOGGLE_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    /** Draws a horizontal stroke through the centre of the wall canvas. */
    fun drawStroke() {
        wallCanvas().performTouchInput {
            down(Offset(centerX - width * STROKE_HALF_SPAN_FRACTION, centerY))
            moveTo(Offset(centerX, centerY))
            moveTo(Offset(centerX + width * STROKE_HALF_SPAN_FRACTION, centerY))
            up()
        }
        composeUiTest.waitForIdle()
    }

    /** Presses a pointer down on the wall canvas and drags it past the touch slop, leaving it down. */
    fun startStroke() {
        wallCanvas().performTouchInput {
            down(Offset(centerX - width * STROKE_HALF_SPAN_FRACTION, centerY))
            moveTo(Offset(centerX, centerY))
        }
        composeUiTest.waitForIdle()
    }

    /** Lifts the pointer [startStroke] left down, which ends the stroke it was drawing. */
    fun finishStroke() {
        wallCanvas().performTouchInput { up() }
        composeUiTest.waitForIdle()
    }

    private fun wallCanvas(): SemanticsNodeInteraction =
        composeUiTest.onAllNodesWithTag(DOODLE_CANVAS_FRAME_TEST_TAG)[0]

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

    fun checkButtonEnabled(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).assertIsEnabled()
    }

    fun checkButtonDisabled(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).assertIsNotEnabled()
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

    fun checkSavedWallStrokeInks(inks: List<DoodleInk>) {
        val edits = graph.doodleMutationKey.invocations.tryReceive().getOrThrow()
        assertEquals(inks, edits.single().doodle.strokes.map(DoodleStroke::ink))
    }

    fun checkSavedWallStrokeOutlines(outlines: List<Boolean>) {
        val edits = graph.doodleMutationKey.invocations.tryReceive().getOrThrow()
        assertEquals(outlines, edits.single().doodle.strokes.map(DoodleStroke::outlined))
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

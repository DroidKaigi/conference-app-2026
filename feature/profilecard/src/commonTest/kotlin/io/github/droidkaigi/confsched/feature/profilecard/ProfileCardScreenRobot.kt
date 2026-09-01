package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleInk
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.core.ui.DOODLE_CANVAS_FRAME_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_FORM_ADD_IMAGE_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_FORM_AVATAR_IMAGE_ERROR_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_FORM_LINK_FIELD_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_FORM_NICK_NAME_FIELD_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_FORM_OCCUPATION_FIELD_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_FORM_SUBMIT_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_VIEW_DOODLE_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_VIEW_EDIT_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_VIEW_SHARE_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.mascotOptionTestTag
import io.github.droidkaigi.confsched.feature.profilecard.component.sketchinessOptionTestTag
import kotlinx.collections.immutable.PersistentMap
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ProfileCardScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<ProfileCardScreenTestGraph>()

    fun setupStoredCard(card: ProfileCard?) {
        graph.profileCardSubscriptionKey.set(card)
    }

    fun setupDoodles(doodles: PersistentMap<DoodleTarget, Doodle>) {
        graph.doodlesSubscriptionKey.set(doodles)
    }

    fun setupFailingDoodleSave() {
        graph.doodleMutationKey.failWith(IllegalStateException("boom"))
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                ProfileCardScreenRoot()
            }
        }
    }

    fun clickDoodle() = clickTag(PROFILE_CARD_VIEW_DOODLE_BUTTON_TEST_TAG)

    fun clickDone() = clickText("Done")

    fun clickBandInk() = clickDescription("Band color")

    fun clickBannerInk() = clickDescription("Banner color")

    fun clickOutlineToggle() = clickText("Outline")

    fun clickFlipToBack() = clickDescription("Switch to the back")

    fun clickFlipToFront() = clickDescription("Switch to the front")

    /** Draws a horizontal stroke through the centre of the canvas the visible face is drawn on. */
    fun drawStroke() {
        visibleFaceCanvas().performTouchInput {
            down(Offset(centerX - width * STROKE_HALF_SPAN_FRACTION, centerY))
            moveTo(Offset(centerX, centerY))
            moveTo(Offset(centerX + width * STROKE_HALF_SPAN_FRACTION, centerY))
            up()
        }
        composeUiTest.waitForIdle()
    }

    /** Presses a pointer down on that canvas and drags it past the touch slop, leaving it down. */
    fun startStroke() {
        visibleFaceCanvas().performTouchInput {
            down(Offset(centerX - width * STROKE_HALF_SPAN_FRACTION, centerY))
            moveTo(Offset(centerX, centerY))
        }
        composeUiTest.waitForIdle()
    }

    /** Lifts the pointer [startStroke] left down, which ends the stroke it was drawing. */
    fun finishStroke() {
        visibleFaceCanvas().performTouchInput { up() }
        composeUiTest.waitForIdle()
    }

    private fun visibleFaceCanvas(): SemanticsNodeInteraction =
        composeUiTest.onAllNodesWithTag(DOODLE_CANVAS_FRAME_TEST_TAG)[0]

    fun inputNickName(text: String) = inputField(PROFILE_CARD_FORM_NICK_NAME_FIELD_TEST_TAG, text)

    fun inputOccupation(text: String) = inputField(PROFILE_CARD_FORM_OCCUPATION_FIELD_TEST_TAG, text)

    fun inputLink(text: String) = inputField(PROFILE_CARD_FORM_LINK_FIELD_TEST_TAG, text)

    fun clickMascot(mascot: Mascot) = clickTag(mascotOptionTestTag(mascot))

    fun clickSketchiness(sketchiness: Sketchiness) = clickTag(sketchinessOptionTestTag(sketchiness))

    fun clickCreate() = clickTag(PROFILE_CARD_FORM_SUBMIT_BUTTON_TEST_TAG)

    fun clickEdit() = clickTag(PROFILE_CARD_VIEW_EDIT_BUTTON_TEST_TAG)

    fun checkFormDisplayed() {
        editableFieldIn(PROFILE_CARD_FORM_NICK_NAME_FIELD_TEST_TAG).assertIsDisplayed()
        composeUiTest.onNodeWithTag(PROFILE_CARD_FORM_SUBMIT_BUTTON_TEST_TAG).assertIsDisplayed()
    }

    fun checkAddImageButtonDisplayed() {
        composeUiTest.onNodeWithTag(PROFILE_CARD_FORM_ADD_IMAGE_BUTTON_TEST_TAG).assertIsDisplayed()
    }

    fun checkFormDoesNotExist() {
        composeUiTest.onNodeWithTag(PROFILE_CARD_FORM_SUBMIT_BUTTON_TEST_TAG).assertDoesNotExist()
    }

    fun checkNickNameShows(nickName: String) {
        editableFieldIn(PROFILE_CARD_FORM_NICK_NAME_FIELD_TEST_TAG).assertTextEquals(nickName)
    }

    fun checkAvatarImageErrorDisplayed() {
        composeUiTest.onNodeWithTag(PROFILE_CARD_FORM_AVATAR_IMAGE_ERROR_TEST_TAG).assertIsDisplayed()
    }

    fun checkCardDisplayed() {
        composeUiTest.onNodeWithTag(PROFILE_CARD_VIEW_SHARE_BUTTON_TEST_TAG).assertIsDisplayed()
        composeUiTest.onNodeWithTag(PROFILE_CARD_VIEW_EDIT_BUTTON_TEST_TAG).assertIsDisplayed()
    }

    fun checkCardDoesNotExist() {
        composeUiTest.onNodeWithTag(PROFILE_CARD_VIEW_SHARE_BUTTON_TEST_TAG).assertDoesNotExist()
    }

    fun checkTextDisplayed(text: String) {
        composeUiTest.onNodeWithText(text).assertIsDisplayed()
    }

    fun checkDoneEnabled() {
        composeUiTest.onNodeWithText("Done").assertIsEnabled()
    }

    fun checkDoneDisabled() {
        composeUiTest.onNodeWithText("Done").assertIsNotEnabled()
    }

    fun checkFlipToBackDisabled() {
        composeUiTest.onNodeWithContentDescription("Switch to the back").assertIsNotEnabled()
    }

    fun checkCardWritten(card: ProfileCard) {
        assertEquals(card, graph.profileCardMutationKey.invocations.tryReceive().getOrNull())
    }

    fun checkNoCardWritten() {
        assertNull(graph.profileCardMutationKey.invocations.tryReceive().getOrNull())
    }

    /** Asserts one save carried both faces, each with the given number of strokes. */
    fun checkSavedFaceStrokeCounts(front: Int, back: Int) {
        val edits = graph.doodleMutationKey.invocations.tryReceive().getOrThrow()
        assertEquals(front, edits.single { it.target == DoodleTarget.ProfileCardFront }.doodle.strokes.size)
        assertEquals(back, edits.single { it.target == DoodleTarget.ProfileCardBack }.doodle.strokes.size)
    }

    /** Asserts one save carried the given ink on the stroke drawn last on each face. */
    fun checkLastSavedStrokeInks(front: DoodleInk, back: DoodleInk) {
        val edits = graph.doodleMutationKey.invocations.tryReceive().getOrThrow()
        assertEquals(front, edits.single { it.target == DoodleTarget.ProfileCardFront }.doodle.strokes.last().ink)
        assertEquals(back, edits.single { it.target == DoodleTarget.ProfileCardBack }.doodle.strokes.last().ink)
    }

    /** Asserts one save carried the given outline on the stroke drawn last on each face. */
    fun checkLastSavedStrokeOutlines(front: Boolean, back: Boolean) {
        val edits = graph.doodleMutationKey.invocations.tryReceive().getOrThrow()
        assertEquals(front, edits.single { it.target == DoodleTarget.ProfileCardFront }.doodle.strokes.last().outlined)
        assertEquals(back, edits.single { it.target == DoodleTarget.ProfileCardBack }.doodle.strokes.last().outlined)
    }

    fun checkNoDoodleSaved() {
        assertTrue(graph.doodleMutationKey.invocations.tryReceive().isFailure, "a doodle save reached the data layer")
    }

    private fun inputField(testTag: String, text: String) {
        val field = editableFieldIn(testTag)
        field.performTextClearance()
        field.performTextInput(text)
        composeUiTest.waitForIdle()
    }

    // The text sits on the editable node inside the tagged frame, not on the frame itself.
    private fun editableFieldIn(testTag: String): SemanticsNodeInteraction {
        return composeUiTest.onNode(hasSetTextAction() and hasAnyAncestor(hasTestTag(testTag)))
    }

    private fun clickTag(testTag: String) {
        composeUiTest.onNodeWithTag(testTag).performClick()
        composeUiTest.waitForIdle()
    }

    private fun clickText(text: String) {
        composeUiTest.onNodeWithText(text).performClick()
        composeUiTest.waitForIdle()
    }

    private fun clickDescription(description: String) {
        composeUiTest.onNodeWithContentDescription(description).performClick()
        composeUiTest.waitForIdle()
    }
}

private const val STROKE_HALF_SPAN_FRACTION = 0.2f

package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_FORM_ADD_IMAGE_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_FORM_AVATAR_IMAGE_ERROR_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_FORM_LINK_FIELD_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_FORM_NICK_NAME_FIELD_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_FORM_OCCUPATION_FIELD_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_FORM_SUBMIT_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_VIEW_EDIT_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_VIEW_SHARE_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.mascotOptionTestTag
import io.github.droidkaigi.confsched.feature.profilecard.component.sketchinessOptionTestTag
import kotlinx.collections.immutable.PersistentMap
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalTestApi::class)
class ProfileCardScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<ProfileCardScreenTestGraph>()
    private val doodleTargets = mutableListOf<DoodleTarget>()

    fun setupStoredCard(card: ProfileCard?) {
        graph.profileCardSubscriptionKey.set(card)
    }

    fun setupDoodles(doodles: PersistentMap<DoodleTarget, Doodle>) {
        graph.doodlesSubscriptionKey.set(doodles)
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                ProfileCardScreenRoot(onNavigateToDoodle = doodleTargets::add)
            }
        }
    }

    // The card itself carries no label; it is the first clickable the finished card view lays out.
    fun clickCard() {
        composeUiTest.onAllNodes(hasClickAction())[0].performClick()
        composeUiTest.waitForIdle()
    }

    fun clickDoodle() {
        composeUiTest.onNodeWithContentDescription("Draw on the card").performClick()
        composeUiTest.waitForIdle()
    }

    fun checkDoodleTargets(vararg targets: DoodleTarget) {
        assertEquals(targets.toList(), doodleTargets.toList())
    }

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

    fun checkCardWritten(card: ProfileCard) {
        assertEquals(card, graph.profileCardMutationKey.invocations.tryReceive().getOrNull())
    }

    fun checkNoCardWritten() {
        assertNull(graph.profileCardMutationKey.invocations.tryReceive().getOrNull())
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
}

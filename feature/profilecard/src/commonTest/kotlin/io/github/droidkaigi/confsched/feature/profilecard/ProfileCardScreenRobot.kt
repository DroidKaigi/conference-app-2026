package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.testing.Robot
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalTestApi::class)
class ProfileCardScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<ProfileCardScreenTestGraph>()

    fun setupStoredCard(card: ProfileCard?) {
        graph.profileCardSubscriptionKey.set(card)
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                ProfileCardScreenRoot()
            }
        }
    }

    fun inputNickName(text: String) = inputField(NICK_NAME_FIELD_INDEX, text)

    fun inputOccupation(text: String) = inputField(OCCUPATION_FIELD_INDEX, text)

    fun inputLink(text: String) = inputField(LINK_FIELD_INDEX, text)

    fun clickMascot(mascotName: String) {
        composeUiTest.onNodeWithContentDescription(mascotName).performClick()
        composeUiTest.waitForIdle()
    }

    fun clickSketchiness(label: String) = clickText(label)

    fun clickCreate() = clickText("Create Card")

    fun clickEdit() = clickText("Edit")

    fun checkTextDisplayed(text: String) {
        composeUiTest.onNodeWithText(text).assertIsDisplayed()
    }

    fun checkTextDoesNotExist(text: String) {
        composeUiTest.onNodeWithText(text).assertDoesNotExist()
    }

    fun checkCardWritten(card: ProfileCard) {
        assertEquals(card, graph.profileCardMutationKey.invocations.tryReceive().getOrNull())
    }

    fun checkNoCardWritten() {
        assertNull(graph.profileCardMutationKey.invocations.tryReceive().getOrNull())
    }

    private fun inputField(index: Int, text: String) {
        val field = composeUiTest.onAllNodes(hasSetTextAction())[index]
        field.performTextClearance()
        field.performTextInput(text)
        composeUiTest.waitForIdle()
    }

    private fun clickText(text: String) {
        composeUiTest.onNodeWithText(text).performClick()
        composeUiTest.waitForIdle()
    }

    private companion object {
        const val NICK_NAME_FIELD_INDEX = 0
        const val OCCUPATION_FIELD_INDEX = 1
        const val LINK_FIELD_INDEX = 2
    }
}

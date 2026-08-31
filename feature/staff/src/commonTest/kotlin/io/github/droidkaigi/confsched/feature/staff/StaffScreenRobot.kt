package io.github.droidkaigi.confsched.feature.staff

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Staff
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.core.ui.KAIGI_TOP_APP_BAR_BACK_BUTTON_TEST_TAG
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class StaffScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<StaffScreenTestGraph>()
    private val openedProfiles = mutableListOf<String>()
    private var backCount = 0

    fun setupStaff(staff: Staff) {
        graph.staffQueryKey.set(staff)
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                StaffScreenRoot(
                    onNavigateBack = { backCount++ },
                    onStaffClick = openedProfiles::add,
                )
            }
        }
    }

    fun clickStaff(username: String) {
        composeUiTest.onNodeWithText(username).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkStaffDisplayed(username: String) {
        composeUiTest.onNodeWithText(username).assertIsDisplayed()
    }

    fun checkOpenedProfiles(vararg urls: String) {
        assertEquals(urls.toList(), openedProfiles)
    }

    fun clickBack() {
        composeUiTest.onNodeWithTag(KAIGI_TOP_APP_BAR_BACK_BUTTON_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkBackInvoked(times: Int) {
        assertEquals(times, backCount)
    }
}

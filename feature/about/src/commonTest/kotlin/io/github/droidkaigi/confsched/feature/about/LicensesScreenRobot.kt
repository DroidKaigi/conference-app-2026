package io.github.droidkaigi.confsched.feature.about

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mikepenz.aboutlibraries.Libs
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.core.ui.DEFAULT_ERROR_FALLBACK_CONTENT_TEST_TAG
import io.github.droidkaigi.confsched.core.ui.KAIGI_TOP_APP_BAR_BACK_BUTTON_TEST_TAG
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class LicensesScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<LicensesScreenTestGraph>()
    private var backCount = 0

    fun setupLibs(libs: Libs) {
        graph.licensesQueryKey.set(libs)
    }

    fun setupPendingLibs() {
        graph.licensesQueryKey.hold()
    }

    fun setupFailingLibs() {
        graph.licensesQueryKey.failWith(IllegalStateException("boom"))
    }

    fun releaseLibs(libs: Libs) {
        graph.licensesQueryKey.set(libs)
        graph.licensesQueryKey.release()
        composeUiTest.waitForIdle()
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                LicensesScreenRoot(onNavigateBack = { backCount++ })
            }
        }
    }

    fun clickLibrary(name: String) {
        composeUiTest.onNodeWithText(name).performClick()
        composeUiTest.waitForIdle()
    }

    fun clickBack() {
        composeUiTest.onNodeWithTag(KAIGI_TOP_APP_BAR_BACK_BUTTON_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkTextDisplayed(text: String) {
        composeUiTest.onNodeWithText(text).assertIsDisplayed()
    }

    fun checkTextDoesNotExist(text: String) {
        composeUiTest.onNodeWithText(text).assertDoesNotExist()
    }

    fun checkBackInvoked(times: Int) {
        assertEquals(times, backCount)
    }

    fun checkLoadingDisplayed() {
        composeUiTest.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }

    fun checkErrorDisplayed() {
        composeUiTest.onNodeWithTag(DEFAULT_ERROR_FALLBACK_CONTENT_TEST_TAG).assertIsDisplayed()
    }
}

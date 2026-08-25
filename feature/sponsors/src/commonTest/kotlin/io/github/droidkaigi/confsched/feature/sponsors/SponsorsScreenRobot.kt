package io.github.droidkaigi.confsched.feature.sponsors

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Sponsors
import io.github.droidkaigi.confsched.core.testing.Robot
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class SponsorsScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<SponsorsScreenTestGraph>()
    private val openedSites = mutableListOf<String>()
    private var backCount = 0

    fun setupSponsors(sponsors: Sponsors) {
        graph.sponsorsQueryKey.set(sponsors)
    }

    fun setupPendingSponsors() {
        graph.sponsorsQueryKey.hold()
    }

    fun setupFailingSponsors() {
        graph.sponsorsQueryKey.failWith(IllegalStateException("boom"))
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                SponsorsScreenRoot(
                    onNavigateBack = { backCount++ },
                    onNavigateToSponsorSite = openedSites::add,
                )
            }
        }
    }

    fun releaseSponsors(sponsors: Sponsors) {
        graph.sponsorsQueryKey.set(sponsors)
        graph.sponsorsQueryKey.release()
        composeUiTest.waitForIdle()
    }

    fun clickSponsor(name: String) {
        composeUiTest.onNodeWithContentDescription(name).performClick()
        composeUiTest.waitForIdle()
    }

    fun clickBack() {
        composeUiTest.onNodeWithContentDescription("Back").performClick()
        composeUiTest.waitForIdle()
    }

    fun checkPlanSectionDisplayed(title: String) {
        composeUiTest.onNodeWithContentDescription(title).assertIsDisplayed()
    }

    fun checkSponsorDisplayed(name: String) {
        composeUiTest.onNodeWithContentDescription(name).assertIsDisplayed()
    }

    fun checkSponsorCount(name: String, expected: Int) {
        assertEquals(expected, composeUiTest.onAllNodesWithContentDescription(name).fetchSemanticsNodes().size)
    }

    fun checkPlanSectionDoesNotExist(title: String) {
        composeUiTest.onNodeWithContentDescription(title).assertDoesNotExist()
    }

    fun checkOpenedSites(vararg links: String) {
        assertEquals(links.toList(), openedSites)
    }

    fun checkBackInvoked(times: Int) {
        assertEquals(times, backCount)
    }

    fun checkLoadingDisplayed() {
        composeUiTest.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }

    fun checkErrorDisplayed() {
        composeUiTest.onNodeWithText("Failed to load").assertIsDisplayed()
    }

    fun checkEmptyStateDisplayed() {
        composeUiTest.onNodeWithText("Sponsors have not been announced yet.").assertIsDisplayed()
    }
}

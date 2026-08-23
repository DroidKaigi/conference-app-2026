package io.github.droidkaigi.confsched.feature.contributors

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Contributors
import io.github.droidkaigi.confsched.core.testing.Robot
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ContributorsScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<ContributorsScreenTestGraph>()
    private val openedProfiles = mutableListOf<String>()
    private var backCount = 0

    fun setupContributors(contributors: Contributors) {
        graph.contributorsQueryKey.set(contributors)
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                ContributorsScreenRoot(
                    onNavigateBack = { backCount++ },
                    onNavigateToContributorProfile = openedProfiles::add,
                )
            }
        }
    }

    fun clickContributor(username: String) {
        composeUiTest.onNodeWithText(username).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkContributorDisplayed(username: String) {
        composeUiTest.onNodeWithText(username).assertIsDisplayed()
    }

    fun checkCountDisplayed(count: Int) {
        composeUiTest.onNodeWithText("TOTAL").assertIsDisplayed()
        composeUiTest.onNodeWithText("$count").assertIsDisplayed()
        composeUiTest.onNodeWithText("persons").assertIsDisplayed()
    }

    fun checkOpenedProfiles(vararg urls: String) {
        assertEquals(urls.toList(), openedProfiles)
    }

    fun clickBack() {
        composeUiTest.onNodeWithContentDescription("Back").performClick()
        composeUiTest.waitForIdle()
    }

    fun checkBackInvoked(times: Int) {
        assertEquals(times, backCount)
    }

    fun checkEmptyStateDisplayed() {
        composeUiTest.onNodeWithText("No contributors to show yet.").assertIsDisplayed()
    }
}

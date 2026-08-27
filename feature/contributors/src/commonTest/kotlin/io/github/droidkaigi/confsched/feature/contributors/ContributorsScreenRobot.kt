package io.github.droidkaigi.confsched.feature.contributors

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Contributors
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.core.ui.KAIGI_TOP_APP_BAR_BACK_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.contributors.component.CONTRIBUTORS_COUNT_TEXT_COUNT_TEST_TAG
import io.github.droidkaigi.confsched.feature.contributors.component.CONTRIBUTORS_COUNT_TEXT_LABEL_TEST_TAG
import io.github.droidkaigi.confsched.feature.contributors.component.CONTRIBUTORS_COUNT_TEXT_UNIT_TEST_TAG
import io.github.droidkaigi.confsched.feature.contributors.component.CONTRIBUTORS_EMPTY_VIEW_TEST_TAG
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
        composeUiTest.onNodeWithTag(CONTRIBUTORS_COUNT_TEXT_LABEL_TEST_TAG).assertIsDisplayed()
        composeUiTest.onNodeWithTag(CONTRIBUTORS_COUNT_TEXT_COUNT_TEST_TAG).assertTextEquals("$count")
        composeUiTest.onNodeWithTag(CONTRIBUTORS_COUNT_TEXT_UNIT_TEST_TAG).assertIsDisplayed()
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

    fun checkEmptyStateDisplayed() {
        composeUiTest.onNodeWithTag(CONTRIBUTORS_EMPTY_VIEW_TEST_TAG).assertIsDisplayed()
    }
}

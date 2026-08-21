package io.github.droidkaigi.confsched.feature.search

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.testing.Robot

@OptIn(ExperimentalTestApi::class)
class SearchScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<SearchScreenTestGraph>()

    fun setupTimetable(timetable: Timetable) {
        graph.timetableQueryKey.set(timetable)
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                SearchScreenRoot(onNavigateBack = {}, onNavigateToDetail = {})
            }
        }
    }

    fun typeQuery(text: String) {
        // The query field is the screen's only editable node, and its hint reads the same as the
        // opening state's headline, so the semantics find it where the text would not.
        composeUiTest.onNode(hasSetTextAction()).performTextInput(text)
        composeUiTest.waitForIdle()
    }

    fun clearQuery() {
        composeUiTest.onNodeWithContentDescription(CLEAR_DESCRIPTION).performClick()
        composeUiTest.waitForIdle()
    }

    fun openDayFilter() {
        composeUiTest.onNodeWithText(DATE_LABEL).performClick()
        composeUiTest.waitForIdle()
    }

    fun pickFilterOption(label: String) {
        composeUiTest.onNodeWithText(label).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkSessionDisplayed(title: String) {
        composeUiTest.onNodeWithText(title).assertIsDisplayed()
    }

    fun checkSessionDoesNotExist(title: String) {
        composeUiTest.onNodeWithText(title).assertDoesNotExist()
    }

    fun checkInitialStateDisplayed() {
        composeUiTest.onNodeWithText(INITIAL_DESCRIPTION).assertIsDisplayed()
    }

    fun checkNoMatchStateDisplayed() {
        composeUiTest.onNodeWithText(NO_MATCH_TITLE).assertIsDisplayed()
    }

    fun checkDayFilterShows(label: String) {
        composeUiTest.onNodeWithText(label).assertIsDisplayed()
    }

    private companion object {
        // The screen runs in the default locale, so its English strings are what find its nodes.
        const val CLEAR_DESCRIPTION = "Clear"
        const val DATE_LABEL = "Date"

        // The opening state's headline reads the same as the field's hint, so its supporting line
        // is what tells the state apart.
        const val INITIAL_DESCRIPTION = "Find a session by its title or speaker"
        const val NO_MATCH_TITLE = "No sessions found"
    }
}

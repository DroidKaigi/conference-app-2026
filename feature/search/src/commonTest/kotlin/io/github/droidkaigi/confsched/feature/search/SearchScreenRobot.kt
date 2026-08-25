package io.github.droidkaigi.confsched.feature.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.feature.search.component.SearchTopBar
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SearchScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<SearchScreenTestGraph>()
    private var backClicked = false
    private var openedSessionId: TimetableItemId? = null

    fun setupTimetable(timetable: Timetable) {
        graph.timetableQueryKey.set(timetable)
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                SearchScreenRoot(
                    onNavigateBack = { backClicked = true },
                    onNavigateToDetail = { openedSessionId = it },
                )
            }
        }
    }

    fun setupQueryField(queryText: String) {
        setScreenContent {
            var query by remember { mutableStateOf(queryText) }
            SearchTopBar(
                queryText = query,
                onQueryTextChange = { query = it },
                onBackClick = {},
            )
        }
    }

    fun typeQuery(text: String) {
        // The field is the screen's only editable node, which finds it without depending on its hint.
        composeUiTest.onNode(hasSetTextAction()).performTextInput(text)
        composeUiTest.waitForIdle()
    }

    fun checkQueryText(queryText: String) {
        composeUiTest.onNode(hasSetTextAction()).assertTextEquals(queryText)
    }

    fun clearQuery() {
        composeUiTest.onNodeWithContentDescription(CLEAR_DESCRIPTION).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkSearchFieldHeight() {
        composeUiTest.onNodeWithContentDescription(SEARCH_DESCRIPTION).assertHeightIsEqualTo(40.dp)
    }

    fun openDayFilter() {
        composeUiTest.onNodeWithText(DATE_LABEL).performClick()
        composeUiTest.waitForIdle()
    }

    fun openCategoryFilter() {
        composeUiTest.onNodeWithText(CATEGORY_LABEL).performClick()
        composeUiTest.waitForIdle()
    }

    fun openSessionTypeFilter() {
        composeUiTest.onNodeWithText(SESSION_TYPE_LABEL).performClick()
        composeUiTest.waitForIdle()
    }

    fun openLanguageFilter() {
        composeUiTest.onNodeWithText(LANGUAGE_LABEL).performScrollTo().performClick()
        composeUiTest.waitForIdle()
    }

    fun checkFilterOptionDisplayed(label: String) {
        composeUiTest.onNodeWithText(label).assertIsDisplayed()
    }

    fun checkFilterOptionDoesNotExist(label: String) {
        composeUiTest.onNodeWithText(label).assertDoesNotExist()
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
        composeUiTest.onNodeWithText(INITIAL_TITLE).assertIsDisplayed()
    }

    fun checkNoMatchStateDisplayed() {
        composeUiTest.onNodeWithText(NO_MATCH_TITLE).assertIsDisplayed()
    }

    fun checkQueryNoMatchDescriptionDisplayed() {
        composeUiTest.onNodeWithText(QUERY_NO_MATCH_DESCRIPTION).assertIsDisplayed()
    }

    fun checkFilteredNoMatchDescriptionDisplayed() {
        composeUiTest.onNodeWithText(FILTERED_NO_MATCH_DESCRIPTION).assertIsDisplayed()
    }

    fun clearFilters() {
        composeUiTest.onNodeWithText(CLEAR_FILTERS).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkClearFiltersDisplayed() {
        composeUiTest.onNodeWithText(CLEAR_FILTERS).assertIsDisplayed()
    }

    fun checkClearFiltersDoesNotExist() {
        composeUiTest.onNodeWithText(CLEAR_FILTERS).assertDoesNotExist()
    }

    fun checkResultCountShows(count: Int) {
        if (count == 1) {
            // Native robots use base strings without an English `one` rule.
            composeUiTest.onNode(
                hasTextExactly("1 session") or hasTextExactly("1 sessions"),
            ).assertIsDisplayed()
        } else {
            composeUiTest.onNodeWithText("$count sessions").assertIsDisplayed()
        }
    }

    fun checkDayFilterShows(label: String) {
        composeUiTest.onNodeWithText(label).assertIsDisplayed()
    }

    fun checkDayHeaderDisplayed(day: DroidKaigi2026Day) {
        composeUiTest.onNodeWithText(day.label).assertIsDisplayed()
    }

    fun clickBack() {
        composeUiTest.onNodeWithContentDescription(BACK_DESCRIPTION).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkBackClicked() {
        assertTrue(backClicked)
    }

    fun clickSession(title: String) {
        composeUiTest.onNodeWithText(title).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkOpenedSession(id: String) {
        assertEquals(TimetableItemId(id), openedSessionId)
    }

    private companion object {
        const val SEARCH_DESCRIPTION = "Search"
        const val BACK_DESCRIPTION = "Back"
        const val CLEAR_DESCRIPTION = "Clear"
        const val DATE_LABEL = "Date"
        const val CATEGORY_LABEL = "Category"
        const val SESSION_TYPE_LABEL = "Session type"
        const val LANGUAGE_LABEL = "Language"

        const val INITIAL_TITLE = "Take a look around"
        const val NO_MATCH_TITLE = "Nothing found."
        const val QUERY_NO_MATCH_DESCRIPTION = "Try a different search term"
        const val FILTERED_NO_MATCH_DESCRIPTION = "Removing a filter may turn something up"
        const val CLEAR_FILTERS = "Clear filters"
    }
}

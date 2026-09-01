package io.github.droidkaigi.confsched.feature.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.SessionType
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.feature.search.component.SEARCH_FILTER_CATEGORY_CHIP_TEST_TAG
import io.github.droidkaigi.confsched.feature.search.component.SEARCH_FILTER_DAY_CHIP_TEST_TAG
import io.github.droidkaigi.confsched.feature.search.component.SEARCH_FILTER_LANGUAGE_CHIP_TEST_TAG
import io.github.droidkaigi.confsched.feature.search.component.SEARCH_FILTER_SESSION_TYPE_CHIP_TEST_TAG
import io.github.droidkaigi.confsched.feature.search.component.SEARCH_RESULT_SECTION_COUNT_TEST_TAG
import io.github.droidkaigi.confsched.feature.search.component.SEARCH_STATE_VIEW_CLEAR_FILTERS_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.search.component.SEARCH_STATE_VIEW_INITIAL_TEST_TAG
import io.github.droidkaigi.confsched.feature.search.component.SEARCH_STATE_VIEW_NO_MATCH_DESCRIPTION_TEST_TAG
import io.github.droidkaigi.confsched.feature.search.component.SEARCH_STATE_VIEW_NO_MATCH_TEST_TAG
import io.github.droidkaigi.confsched.feature.search.component.SEARCH_TOP_BAR_BACK_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.search.component.SEARCH_TOP_BAR_CLEAR_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.search.component.SEARCH_TOP_BAR_QUERY_FIELD_TEST_TAG
import io.github.droidkaigi.confsched.feature.search.component.SearchTopBar
import io.github.droidkaigi.confsched.feature.search.component.searchFilterCategoryOptionTestTag
import io.github.droidkaigi.confsched.feature.search.component.searchFilterDayOptionTestTag
import io.github.droidkaigi.confsched.feature.search.component.searchFilterLanguageOptionTestTag
import io.github.droidkaigi.confsched.feature.search.component.searchFilterSessionTypeOptionTestTag
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
                    onFavoriteAdded = {},
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
        composeUiTest.onNodeWithTag(SEARCH_TOP_BAR_CLEAR_BUTTON_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkSearchFieldHeight() {
        composeUiTest.onNodeWithTag(SEARCH_TOP_BAR_QUERY_FIELD_TEST_TAG).assertHeightIsEqualTo(40.dp)
    }

    fun openDayFilter() {
        composeUiTest.onNodeWithTag(SEARCH_FILTER_DAY_CHIP_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    fun openCategoryFilter() {
        composeUiTest.onNodeWithTag(SEARCH_FILTER_CATEGORY_CHIP_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    fun openSessionTypeFilter() {
        composeUiTest.onNodeWithTag(SEARCH_FILTER_SESSION_TYPE_CHIP_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    fun openLanguageFilter() {
        composeUiTest.onNodeWithTag(SEARCH_FILTER_LANGUAGE_CHIP_TEST_TAG).performScrollTo().performClick()
        composeUiTest.waitForIdle()
    }

    fun checkFilterOptionDisplayed(day: DroidKaigi2026Day) {
        composeUiTest.onNodeWithTag(searchFilterDayOptionTestTag(day)).assertIsDisplayed()
    }

    fun checkFilterOptionDoesNotExist(day: DroidKaigi2026Day) {
        composeUiTest.onNodeWithTag(searchFilterDayOptionTestTag(day)).assertDoesNotExist()
    }

    fun pickDayFilterOption(day: DroidKaigi2026Day) {
        composeUiTest.onNodeWithTag(searchFilterDayOptionTestTag(day)).performClick()
        composeUiTest.waitForIdle()
    }

    fun pickCategoryFilterOption(categoryId: Long) {
        composeUiTest.onNodeWithTag(searchFilterCategoryOptionTestTag(categoryId)).performClick()
        composeUiTest.waitForIdle()
    }

    fun pickSessionTypeFilterOption(sessionType: SessionType) {
        composeUiTest.onNodeWithTag(searchFilterSessionTypeOptionTestTag(sessionType)).performClick()
        composeUiTest.waitForIdle()
    }

    fun pickLanguageFilterOption(language: Language) {
        composeUiTest.onNodeWithTag(searchFilterLanguageOptionTestTag(language)).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkSessionDisplayed(title: String) {
        composeUiTest.onNodeWithText(title).assertIsDisplayed()
    }

    fun checkSessionDoesNotExist(title: String) {
        composeUiTest.onNodeWithText(title).assertDoesNotExist()
    }

    fun checkInitialStateDisplayed() {
        composeUiTest.onNodeWithTag(SEARCH_STATE_VIEW_INITIAL_TEST_TAG).assertIsDisplayed()
    }

    fun checkNoMatchStateDisplayed() {
        composeUiTest.onNodeWithTag(SEARCH_STATE_VIEW_NO_MATCH_TEST_TAG).assertIsDisplayed()
    }

    fun checkNoMatchDescriptionDisplayed() {
        composeUiTest.onNodeWithTag(SEARCH_STATE_VIEW_NO_MATCH_DESCRIPTION_TEST_TAG).assertIsDisplayed()
    }

    fun clearFilters() {
        composeUiTest.onNodeWithTag(SEARCH_STATE_VIEW_CLEAR_FILTERS_BUTTON_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkClearFiltersDisplayed() {
        composeUiTest.onNodeWithTag(SEARCH_STATE_VIEW_CLEAR_FILTERS_BUTTON_TEST_TAG).assertIsDisplayed()
    }

    fun checkClearFiltersDoesNotExist() {
        composeUiTest.onNodeWithTag(SEARCH_STATE_VIEW_CLEAR_FILTERS_BUTTON_TEST_TAG).assertDoesNotExist()
    }

    fun checkResultCountShows(count: Int) {
        composeUiTest.onNodeWithTag(SEARCH_RESULT_SECTION_COUNT_TEST_TAG)
            .assertIsDisplayed()
            // The count reads the same in every locale, while the words around it do not.
            .assertTextContains("$count", substring = true)
    }

    fun checkDayFilterShows(label: String) {
        composeUiTest.onNodeWithText(label).assertIsDisplayed()
    }

    fun checkDayHeaderDisplayed(day: DroidKaigi2026Day) {
        composeUiTest.onNodeWithText(day.label).assertIsDisplayed()
    }

    fun clickBack() {
        composeUiTest.onNodeWithTag(SEARCH_TOP_BAR_BACK_BUTTON_TEST_TAG).performClick()
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
}

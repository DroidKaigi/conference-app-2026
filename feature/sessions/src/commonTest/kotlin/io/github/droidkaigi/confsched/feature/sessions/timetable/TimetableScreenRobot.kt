package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.core.ui.TIMETABLE_LIVE_BADGE_TEST_TAG
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TIMETABLE_HEADER_GRID_VIEW_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TIMETABLE_HEADER_LIST_VIEW_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TIMETABLE_HEADER_SEARCH_BUTTON_TEST_TAG
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TimetableScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<TimetableScreenTestGraph>()

    private var restingDayTabsTop: Dp = Dp.Unspecified
    private var searchOpened = false

    fun setupTimetable(timetable: Timetable) {
        graph.timetableQueryKey.set(timetable)
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                TimetableScreenRoot(
                    onNavigateToDetail = {},
                    onNavigateToSearch = { searchOpened = true },
                    onOfferFirstFavoriteGuidance = {},
                )
            }
        }
    }

    fun clickSearch() {
        composeUiTest.onNodeWithTag(TIMETABLE_HEADER_SEARCH_BUTTON_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkSearchOpened() {
        assertTrue(searchOpened)
    }

    fun clickDayTab(day: DroidKaigi2026Day) {
        composeUiTest.onNodeWithText(day.label).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkSessionDisplayed(title: String) {
        composeUiTest.onNodeWithText(title).assertIsDisplayed()
    }

    fun checkSessionDoesNotExist(title: String) {
        composeUiTest.onNodeWithText(title).assertDoesNotExist()
    }

    fun checkTimeSlotDisplayed(startsAt: String, endsAt: String) {
        composeUiTest.onNodeWithText(startsAt).assertIsDisplayed()
        composeUiTest.onNodeWithText(endsAt).assertIsDisplayed()
    }

    fun checkLiveBadgeDisplayed() {
        composeUiTest.onNodeWithTag(TIMETABLE_LIVE_BADGE_TEST_TAG).assertIsDisplayed()
    }

    fun checkLiveBadgeDoesNotExist() {
        composeUiTest.onNodeWithTag(TIMETABLE_LIVE_BADGE_TEST_TAG).assertDoesNotExist()
    }

    fun checkTopBarActionsDisplayed() {
        composeUiTest.onNodeWithTag(TIMETABLE_HEADER_SEARCH_BUTTON_TEST_TAG).assertIsDisplayed()
        composeUiTest.onNodeWithTag(TIMETABLE_HEADER_GRID_VIEW_BUTTON_TEST_TAG).assertIsDisplayed()
    }

    fun clickSwitchToGridView() {
        composeUiTest.onNodeWithTag(TIMETABLE_HEADER_GRID_VIEW_BUTTON_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkSwitchToListViewActionDisplayed() {
        composeUiTest.onNodeWithTag(TIMETABLE_HEADER_LIST_VIEW_BUTTON_TEST_TAG).assertIsDisplayed()
    }

    fun recordDayTabsPosition() {
        restingDayTabsTop = dayTabsBounds().top
    }

    // Both swipes stay in the lower half of the window: a gesture starting at the top edge would
    // begin on the app bar, which does not scroll, and never reach the list.
    fun scrollDown() {
        composeUiTest.onRoot().performTouchInput { swipeUp(startY = bottom, endY = centerY) }
        composeUiTest.waitForIdle()
    }

    fun scrollUp() {
        composeUiTest.onRoot().performTouchInput { swipeDown(startY = centerY, endY = bottom) }
        composeUiTest.waitForIdle()
    }

    fun checkDayTabsFoldedAway() {
        val currentBounds = dayTabsBounds()
        assertTrue(
            actual = currentBounds.bottom <= restingDayTabsTop,
            message = "Day tabs did not scroll away; still visible at $currentBounds against initial top $restingDayTabsTop",
        )
    }

    fun checkDayTabsAtFullHeight() {
        assertEquals(
            expected = restingDayTabsTop,
            actual = dayTabsBounds().top,
            message = "Day tabs did not return to initial top position after scroll up",
        )
    }

    private fun dayTabsBounds(): DpRect =
        composeUiTest.onNodeWithText(DroidKaigi2026Day.Day1.label).getUnclippedBoundsInRoot()
}

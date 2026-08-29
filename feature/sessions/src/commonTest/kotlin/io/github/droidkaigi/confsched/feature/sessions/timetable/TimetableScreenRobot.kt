package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithContentDescription
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
                )
            }
        }
    }

    fun clickSearch() {
        composeUiTest.onNodeWithContentDescription("Search").performClick()
        composeUiTest.waitForIdle()
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
        composeUiTest.onNodeWithText("LIVE").assertIsDisplayed()
    }

    fun checkLiveBadgeDoesNotExist() {
        composeUiTest.onNodeWithText("LIVE").assertDoesNotExist()
    }

    fun checkTopBarActionsDisplayed() {
        composeUiTest.onNodeWithContentDescription("Search").assertIsDisplayed()
        composeUiTest.onNodeWithContentDescription("Switch to grid view").assertIsDisplayed()
    }

    fun clickSwitchToGridView() {
        composeUiTest.onNodeWithContentDescription("Switch to grid view").performClick()
        composeUiTest.waitForIdle()
    }

    fun checkSwitchToListViewActionDisplayed() {
        composeUiTest.onNodeWithContentDescription("Switch to list view").assertIsDisplayed()
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
        assertTrue(dayTabsBounds().bottom <= restingDayTabsTop)
    }

    fun checkDayTabsAtFullHeight() {
        assertEquals(restingDayTabsTop, dayTabsBounds().top)
    }

    private fun dayTabsBounds(): DpRect =
        composeUiTest.onNodeWithText(DroidKaigi2026Day.Day1.label).getUnclippedBoundsInRoot()

    fun checkSearchOpened() {
        assertTrue(searchOpened)
    }
}

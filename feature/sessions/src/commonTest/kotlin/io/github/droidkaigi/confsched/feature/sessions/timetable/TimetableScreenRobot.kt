package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.testing.Robot
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TimetableScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<TimetableScreenTestGraph>()

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

    fun checkTopBarActionsDisplayed() {
        composeUiTest.onNodeWithContentDescription("Search").assertIsDisplayed()
        composeUiTest.onNodeWithContentDescription("Switch to grid view").assertIsDisplayed()
    }

    fun checkSearchOpened() {
        assertTrue(searchOpened)
    }
}

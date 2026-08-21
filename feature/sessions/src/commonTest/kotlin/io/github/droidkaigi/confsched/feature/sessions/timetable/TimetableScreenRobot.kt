package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.testing.Robot

@OptIn(ExperimentalTestApi::class)
class TimetableScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<TimetableScreenTestGraph>()

    fun setupTimetable(timetable: Timetable) {
        graph.timetableQueryKey.set(timetable)
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                TimetableScreenRoot(onNavigateToDetail = {}, onNavigateToSearch = {})
            }
        }
    }

    fun clickDayTab(day: DroidKaigi2026Day) {
        composeUiTest.onNodeWithText(day.label).performClick()
        composeUiTest.waitForIdle()
    }

    fun clickRoomChip(room: Room) {
        composeUiTest.onNode(roomChip(room)).performClick()
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

    fun checkRoomChipOffersNoMap(room: Room) {
        composeUiTest.onNodeWithText(room.name, substring = true).assertIsDisplayed()
        composeUiTest.onAllNodes(roomChip(room)).assertCountEquals(0)
    }

    fun checkEventMapDisplayed(floor: Floor) {
        composeUiTest.onNodeWithContentDescription("Map of ${floor.label}").assertIsDisplayed()
    }

    fun checkEventMapDoesNotExist() {
        composeUiTest.onAllNodesWithContentDescription("Map of", substring = true).assertCountEquals(0)
    }

    fun checkTopBarActionsDisplayed() {
        composeUiTest.onNodeWithContentDescription("Search").assertIsDisplayed()
        composeUiTest.onNodeWithContentDescription("Switch to grid view").assertIsDisplayed()
    }

    // Only a chip that opens the map is a target of its own; otherwise its name merges into the card.
    private fun roomChip(room: Room): SemanticsMatcher = hasTextExactly(room.name) and hasClickAction()
}

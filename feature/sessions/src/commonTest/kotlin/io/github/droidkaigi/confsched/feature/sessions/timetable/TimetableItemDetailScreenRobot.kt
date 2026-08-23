package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import dev.zacsweers.metro.createGraphFactory
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.testing.Robot

@OptIn(ExperimentalTestApi::class)
class TimetableItemDetailScreenRobot(
    composeUiTest: ComposeUiTest,
    timetableItemId: TimetableItemId,
) : Robot(composeUiTest) {

    private val graph = createGraphFactory<TimetableItemDetailScreenTestGraph.Factory>()
        .create(timetableItemId)

    fun setupTimetable(timetable: Timetable) {
        graph.timetableQueryKey.set(timetable)
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                TimetableItemDetailScreenRoot(
                    onNavigateBack = {},
                    onNavigateToSession = {},
                    onOpenUrl = {},
                    onShareText = {},
                )
            }
        }
    }

    fun clickOpenEventMap() {
        composeUiTest.onNodeWithContentDescription(OPEN_EVENT_MAP).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkLocationOffersMap(room: Room) {
        checkLocationNames(room)
        composeUiTest.onNodeWithContentDescription(OPEN_EVENT_MAP).assertIsDisplayed()
    }

    fun checkLocationOffersNoMap(room: Room) {
        checkLocationNames(room)
        composeUiTest.onAllNodesWithContentDescription(OPEN_EVENT_MAP).assertCountEquals(0)
    }

    fun checkEventMapDisplayed(floor: Floor) {
        composeUiTest.onNodeWithContentDescription("Map of ${floor.label}").assertIsDisplayed()
    }

    fun checkEventMapDoesNotExist() {
        composeUiTest.onAllNodesWithContentDescription("Map of", substring = true).assertCountEquals(0)
    }

    // The header names the room as well, so a room the app knows no floor for leaves the location
    // row sharing its text with that chip.
    private fun checkLocationNames(room: Room) {
        composeUiTest.onAllNodesWithText(locationText(room)).onFirst().assertIsDisplayed()
    }

    // Repeats the format SessionInfoCard draws, which is the text its row carries.
    private fun locationText(room: Room): String =
        room.floor?.let { "${room.name} (${it.label})" } ?: room.name

    private companion object {
        // The label SessionInfoCard gives the icon that opens the map.
        const val OPEN_EVENT_MAP = "Open event map"
    }
}

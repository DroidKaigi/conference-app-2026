package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithContentDescription
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

    fun clickLocation(room: Room) {
        composeUiTest.onNode(locationRow(room)).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkLocationOffersMap(room: Room) {
        composeUiTest.onNode(locationRow(room)).assertIsDisplayed()
    }

    fun checkLocationOffersNoMap(room: Room) {
        composeUiTest.onAllNodes(locationRow(room)).assertCountEquals(0)
    }

    fun checkEventMapDisplayed(floor: Floor) {
        composeUiTest.onNodeWithContentDescription("Map of ${floor.label}").assertIsDisplayed()
    }

    fun checkEventMapDoesNotExist() {
        composeUiTest.onAllNodesWithContentDescription("Map of", substring = true).assertCountEquals(0)
    }

    // Only a row that opens the map is clickable, and the header names the room without one.
    private fun locationRow(room: Room): SemanticsMatcher = hasText(locationText(room)) and hasClickAction()

    // Repeats the format SessionInfoCard draws, which is the text its row carries.
    private fun locationText(room: Room): String =
        room.floor?.let { "${room.name} (${it.label})" } ?: room.name
}

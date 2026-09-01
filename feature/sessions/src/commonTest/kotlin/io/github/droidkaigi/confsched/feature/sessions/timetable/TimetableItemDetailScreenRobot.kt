package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dev.zacsweers.metro.createGraphFactory
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.locationText
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.SESSION_INFO_CARD_OPEN_EVENT_MAP_TEST_TAG
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.sessionEventMapImageTestTag

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
                    onAddCalendarEvent = {},
                    onShareText = {},
                    onFavoriteAdded = {},
                )
            }
        }
    }

    fun clickOpenEventMap() {
        composeUiTest.onNodeWithTag(SESSION_INFO_CARD_OPEN_EVENT_MAP_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkLocationOffersMap(room: Room) {
        checkLocationNames(room)
        composeUiTest.onNodeWithTag(SESSION_INFO_CARD_OPEN_EVENT_MAP_TEST_TAG).assertIsDisplayed()
    }

    fun checkLocationOffersNoMap(room: Room) {
        checkLocationNames(room)
        composeUiTest.onAllNodesWithTag(SESSION_INFO_CARD_OPEN_EVENT_MAP_TEST_TAG).assertCountEquals(0)
    }

    fun checkEventMapDisplayed(floor: Floor) {
        composeUiTest.onNodeWithTag(sessionEventMapImageTestTag(floor)).assertIsDisplayed()
    }

    fun checkEventMapDoesNotExist() {
        Floor.entries.forEach { floor ->
            composeUiTest.onAllNodesWithTag(sessionEventMapImageTestTag(floor)).assertCountEquals(0)
        }
    }

    // The header names the room as well, so a room the app knows no floor for leaves the location
    // row sharing its text with that chip.
    private fun checkLocationNames(room: Room) {
        composeUiTest.onAllNodesWithText(room.locationText).onFirst().assertIsDisplayed()
    }
}

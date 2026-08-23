package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import io.github.droidkaigi.confsched.core.testing.testTimetableItem
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class TimetableItemDetailScreenRobotTest : RobotTest() {

    private val sampleTimetable = Timetable(
        items = persistentListOf(
            testTimetableItem(id = "d1a", title = "Day1 A", room = Room.OTTER, speaker = "Sp1", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "10:00", endsAt = "10:40"),
            testTimetableItem(id = "d1b", title = "Day1 B", room = Room.UNKNOWN, speaker = "Sp2", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "11:00", endsAt = "11:40"),
        ),
        bookmarks = persistentSetOf(),
    )

    @Test
    fun session_detail_event_map_behaviour() = runRobotTest(
        robotFactory = { TimetableItemDetailScreenRobot(this, TimetableItemId("d1a")) },
    ) {
        describe("when the session detail has loaded") {
            doIt {
                setupTimetable(sampleTimetable)
                setupContent()
            }
            itShould("offer the map from the room it names, with the map still closed") {
                checkLocationOffersMap(Room.OTTER)
                checkEventMapDoesNotExist()
            }
            describe("and the map action is tapped") {
                doIt {
                    clickOpenEventMap()
                }
                itShould("show the map of the floor that room is on") {
                    checkEventMapDisplayed(Floor.Basement)
                }
            }
        }
    }

    @Test
    fun session_detail_event_map_behaviour_without_a_known_floor() = runRobotTest(
        robotFactory = { TimetableItemDetailScreenRobot(this, TimetableItemId("d1b")) },
    ) {
        describe("when the session detail of a room the app knows no floor for has loaded") {
            doIt {
                setupTimetable(sampleTimetable)
                setupContent()
            }
            itShould("name the room without offering a map") {
                checkLocationOffersNoMap(Room.UNKNOWN)
                checkEventMapDoesNotExist()
            }
        }
    }
}

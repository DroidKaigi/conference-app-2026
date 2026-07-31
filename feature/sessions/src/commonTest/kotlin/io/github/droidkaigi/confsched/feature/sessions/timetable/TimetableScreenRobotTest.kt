package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class TimetableScreenRobotTest {

    private val rawResponse = """{ "sessions": [ { "id": "d1a" } ] }"""

    private val sampleTimetable = Timetable(
        items = persistentListOf(
            TimetableItem(TimetableItemId("d1a"), "Day1 A", "Room1", "Sp1", DroidKaigi2026Day.Day1, "10:00", "10:40"),
            TimetableItem(TimetableItemId("d2a"), "Day2 A", "Room1", "Sp3", DroidKaigi2026Day.Day2, "10:00", "10:40"),
        ),
        bookmarks = persistentSetOf(),
        rawResponse = rawResponse,
    )

    @Test
    fun timetable_screen_behaviour() = runRobotTest(
        robotFactory = { TimetableScreenRobot(this) },
    ) {
        describe("when the timetable has loaded") {
            doIt {
                setupContent(sampleTimetable)
            }
            itShould("show Day1 sessions") {
                checkSessionDisplayed("Day1 A")
                checkSessionDoesNotExist("Day2 A")
            }
            itShould("keep the raw response collapsed") {
                checkRawResponseDoesNotExist(rawResponse)
            }
            describe("and the Day2 tab is tapped") {
                doIt {
                    clickDayTab(DroidKaigi2026Day.Day2)
                }
                itShould("swap the list to Day2 sessions") {
                    checkSessionDisplayed("Day2 A")
                    checkSessionDoesNotExist("Day1 A")
                }
            }
            describe("and the raw response header is tapped") {
                doIt {
                    clickRawResponseHeader()
                }
                itShould("reveal the payload") {
                    checkRawResponseDisplayed(rawResponse)
                }
            }
        }
    }
}

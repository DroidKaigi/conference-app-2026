package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.SessionType
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class TimetableScreenRobotTest : RobotTest() {

    private val sampleTimetable = Timetable(
        items = persistentListOf(
            TimetableItem(TimetableItemId("d1a"), MultiLangText(ja = "Day1 A", en = "Day1 A"), Room.NARWHAL, "Sp1", Language.ENGLISH, DroidKaigi2026Day.Day1, "10:00", "10:40", SessionType.NORMAL),
            TimetableItem(TimetableItemId("d2a"), MultiLangText(ja = "Day2 A", en = "Day2 A"), Room.NARWHAL, "Sp3", Language.ENGLISH, DroidKaigi2026Day.Day2, "10:00", "10:40", SessionType.NORMAL),
        ),
        bookmarks = persistentSetOf(),
    )

    @Test
    fun timetable_screen_behaviour() = runRobotTest(
        robotFactory = { TimetableScreenRobot(this) },
    ) {
        describe("when the timetable has loaded") {
            doIt {
                setupTimetable(sampleTimetable)
                setupContent()
            }
            itShould("show Day1 sessions under their time slot") {
                checkSessionDisplayed("Day1 A")
                checkSessionDoesNotExist("Day2 A")
                checkTimeSlotDisplayed("10:00", "10:40")
            }
            itShould("offer the search and grid view actions") {
                checkTopBarActionsDisplayed()
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
        }
    }
}

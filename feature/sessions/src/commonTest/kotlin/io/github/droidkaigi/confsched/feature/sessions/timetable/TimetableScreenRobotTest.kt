package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import io.github.droidkaigi.confsched.core.testing.testTimetableItem
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class TimetableScreenRobotTest : RobotTest() {

    private val sampleTimetable = Timetable(
        items = persistentListOf(
            testTimetableItem(id = "d1a", title = "Day1 A", room = Room.NARWHAL, speaker = "Sp1", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "10:00", endsAt = "10:40"),
            testTimetableItem(id = "d1b", title = "Day1 B", room = Room.NARWHAL, speaker = "Sp2", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "11:00", endsAt = "11:40"),
            testTimetableItem(id = "d1c", title = "Day1 C", room = Room.NARWHAL, speaker = "Sp4", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "12:00", endsAt = "12:40"),
            testTimetableItem(id = "d1d", title = "Day1 D", room = Room.NARWHAL, speaker = "Sp5", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "13:00", endsAt = "13:40"),
            testTimetableItem(id = "d1e", title = "Day1 E", room = Room.NARWHAL, speaker = "Sp6", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "14:00", endsAt = "14:40"),
            testTimetableItem(id = "d2a", title = "Day2 A", room = Room.NARWHAL, speaker = "Sp3", language = Language.ENGLISH, day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40"),
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
                checkLiveBadgeDisplayed()
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
                    checkLiveBadgeDoesNotExist()
                }
            }
            describe("and the list is scrolled down") {
                doIt {
                    recordDayTabsPosition()
                    scrollDown()
                }
                itShould("fold the day tabs away, leaving the app bar in place") {
                    checkDayTabsFoldedAway()
                    checkTopBarActionsDisplayed()
                }
                describe("and then scrolled back up") {
                    doIt {
                        scrollUp()
                    }
                    itShould("return the day tabs to full height") {
                        checkDayTabsAtFullHeight()
                    }
                }
            }
        }
    }
}

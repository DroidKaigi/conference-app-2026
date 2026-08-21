package io.github.droidkaigi.confsched.feature.search

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
class SearchScreenRobotTest : RobotTest() {

    private val sampleTimetable = Timetable(
        items = persistentListOf(
            item("d1a", "Day1 A", DroidKaigi2026Day.Day1),
            item("d2a", "Day2 A", DroidKaigi2026Day.Day2),
        ),
        bookmarks = persistentSetOf(),
    )

    @Test
    fun search_screen_behaviour() = runRobotTest(
        robotFactory = { SearchScreenRobot(this) },
    ) {
        describe("when the search screen opens") {
            doIt {
                setupTimetable(sampleTimetable)
                setupContent()
            }
            itShould("stand the opening state in for a list") {
                checkInitialStateDisplayed()
            }
            describe("and a word one session matches is typed") {
                doIt {
                    typeQuery("Day1")
                }
                itShould("list only the sessions that match") {
                    checkSessionDisplayed("Day1 A")
                    checkSessionDoesNotExist("Day2 A")
                }
                describe("and the query is cleared") {
                    doIt {
                        clearQuery()
                    }
                    itShould("return to the opening state") {
                        checkInitialStateDisplayed()
                    }
                }
            }
            describe("and a word no session matches is typed") {
                doIt {
                    typeQuery("zzzz")
                }
                itShould("show the no-match state") {
                    checkNoMatchStateDisplayed()
                }
            }
            describe("and a day is picked with nothing typed") {
                doIt {
                    openDayFilter()
                    pickFilterOption(DroidKaigi2026Day.Day2.label)
                }
                itShould("narrow the list to that day") {
                    checkSessionDisplayed("Day2 A")
                    checkSessionDoesNotExist("Day1 A")
                }
                itShould("show the picked day in place of the chip's label") {
                    checkDayFilterShows(DroidKaigi2026Day.Day2.label)
                }
            }
        }
    }

    private fun item(id: String, title: String, day: DroidKaigi2026Day) = TimetableItem(
        id = TimetableItemId(id),
        title = MultiLangText(ja = title, en = title),
        room = Room.NARWHAL,
        speaker = "Speaker A",
        language = Language.ENGLISH,
        day = day,
        startsAt = "10:00",
        endsAt = "10:40",
        sessionType = SessionType.NORMAL,
    )
}

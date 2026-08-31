package io.github.droidkaigi.confsched.feature.search

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.SessionCategory
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.SessionType
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import io.github.droidkaigi.confsched.core.testing.testTimetableItem
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SearchScreenRobotTest : RobotTest() {

    private val categoryA = SessionCategory(11L, MultiLangText(ja = "カテゴリ A", en = "Category A"))
    private val categoryB = SessionCategory(12L, MultiLangText(ja = "カテゴリ B", en = "Category B"))
    private val sampleTimetable = Timetable(
        items = persistentListOf(
            item("d1a", "Day1 A", DroidKaigi2026Day.Day1, categoryA),
            item(
                id = "d2a",
                title = "Day2 A",
                day = DroidKaigi2026Day.Day2,
                category = categoryB,
                language = Language.JAPANESE,
                sessionType = SessionType.CODELABS,
            ),
        ),
        bookmarks = persistentSetOf(),
        categories = persistentListOf(categoryA, categoryB),
    )

    @Test
    fun restored_query_places_cursor_at_the_end() = runRobotTest(
        robotFactory = { SearchScreenRobot(this) },
    ) {
        doIt {
            setupQueryField("Day")
            typeQuery("1")
        }
        itShould("append input to the restored query") {
            checkQueryText("Day1")
        }
    }

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
                itShould("count what it found") {
                    checkResultCountShows(1)
                }
                itShould("keep the search field at its designed height") {
                    checkSearchFieldHeight()
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
                    checkNoMatchDescriptionDisplayed()
                }
                itShould("not offer a filter action when no filter is selected") {
                    checkClearFiltersDoesNotExist()
                }
            }
            describe("and a filter leaves nothing to show") {
                doIt {
                    typeQuery("Day1")
                    openDayFilter()
                    pickDayFilterOption(DroidKaigi2026Day.Day2)
                }
                itShould("show the no-match state") {
                    checkNoMatchStateDisplayed()
                    checkNoMatchDescriptionDisplayed()
                }
                itShould("offer to clear the selected filter") {
                    checkClearFiltersDisplayed()
                }
                describe("and the filters are cleared from it") {
                    doIt {
                        clearFilters()
                    }
                    itShould("keep the word typed and bring the match back") {
                        checkSessionDisplayed("Day1 A")
                    }
                }
            }
            describe("and a day is picked with nothing typed") {
                doIt {
                    openDayFilter()
                    pickDayFilterOption(DroidKaigi2026Day.Day2)
                }
                itShould("narrow the list to that day") {
                    checkSessionDisplayed("Day2 A")
                    checkSessionDoesNotExist("Day1 A")
                }
                itShould("show the picked day in place of the chip's label") {
                    checkDayFilterShows(DroidKaigi2026Day.Day2.label)
                }
            }
            describe("and the date chip is tapped twice") {
                doIt {
                    openDayFilter()
                }
                itShould("open its menu") {
                    checkFilterOptionDisplayed(DroidKaigi2026Day.Day1)
                }
                describe("and the chip is tapped again") {
                    doIt {
                        openDayFilter()
                    }
                    itShould("close its menu") {
                        checkFilterOptionDoesNotExist(DroidKaigi2026Day.Day1)
                    }
                }
            }
            describe("and a word from both conference days is typed") {
                doIt {
                    typeQuery("Day")
                }
                itShould("separate the results with day headers") {
                    checkDayHeaderDisplayed(DroidKaigi2026Day.Day1)
                    checkDayHeaderDisplayed(DroidKaigi2026Day.Day2)
                    checkResultCountShows(2)
                }
            }
        }
    }

    @Test
    fun category_filter_menu_forwards_its_selection() = runRobotTest(
        robotFactory = { SearchScreenRobot(this) },
    ) {
        describe("when the search screen opens") {
            doIt {
                setupTimetable(sampleTimetable)
                setupContent()
            }
            describe("and a category is picked") {
                doIt {
                    openCategoryFilter()
                    pickCategoryFilterOption(categoryB.id)
                }
                itShould("filter by that category") {
                    checkSessionDisplayed("Day2 A")
                    checkSessionDoesNotExist("Day1 A")
                }
            }
        }
    }

    @Test
    fun session_type_filter_menu_forwards_its_selection() = runRobotTest(
        robotFactory = { SearchScreenRobot(this) },
    ) {
        describe("when the search screen opens") {
            doIt {
                setupTimetable(sampleTimetable)
                setupContent()
            }
            describe("and a session type is picked") {
                doIt {
                    openSessionTypeFilter()
                    pickSessionTypeFilterOption(SessionType.CODELABS)
                }
                itShould("filter by that session type") {
                    checkSessionDisplayed("Day2 A")
                    checkSessionDoesNotExist("Day1 A")
                }
            }
        }
    }

    @Test
    fun language_filter_menu_forwards_its_selection() = runRobotTest(
        robotFactory = { SearchScreenRobot(this) },
    ) {
        describe("when the search screen opens") {
            doIt {
                setupTimetable(sampleTimetable)
                setupContent()
            }
            describe("and a language is picked") {
                doIt {
                    openLanguageFilter()
                    pickLanguageFilterOption(Language.JAPANESE)
                }
                itShould("filter by that language") {
                    checkSessionDisplayed("Day2 A")
                    checkSessionDoesNotExist("Day1 A")
                }
            }
        }
    }

    @Test
    fun navigation_callbacks_are_forwarded() = runRobotTest(
        robotFactory = { SearchScreenRobot(this) },
    ) {
        describe("when the search screen opens") {
            doIt {
                setupTimetable(sampleTimetable)
                setupContent()
            }
            describe("and back is tapped") {
                doIt { clickBack() }
                itShould("forward the back callback") { checkBackClicked() }
            }
            describe("and a result is tapped") {
                doIt {
                    typeQuery("Day1")
                    clickSession("Day1 A")
                }
                itShould("forward that session id") { checkOpenedSession("d1a") }
            }
        }
    }

    private fun item(
        id: String,
        title: String,
        day: DroidKaigi2026Day,
        category: SessionCategory,
        language: Language = Language.ENGLISH,
        sessionType: SessionType = SessionType.NORMAL,
    ) = testTimetableItem(
        id = id,
        title = title,
        room = SessionRoom.NARWHAL,
        speaker = "Speaker A",
        language = language,
        day = day,
        startsAt = "10:00",
        endsAt = "10:40",
        sessionType = sessionType,
        category = category,
    )
}

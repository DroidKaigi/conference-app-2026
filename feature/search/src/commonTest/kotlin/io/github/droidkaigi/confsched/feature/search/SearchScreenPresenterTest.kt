package io.github.droidkaigi.confsched.feature.search

import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.AppError
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.SessionCategory
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.SessionType
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableSpeaker
import io.github.droidkaigi.confsched.core.model.TimetableSpeakerId
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import io.github.droidkaigi.confsched.core.testing.testTimetableItem
import io.github.droidkaigi.confsched.feature.search.component.SearchResultUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SearchScreenPresenterTest {

    private val graph = createGraph<SearchScreenTestGraph>()

    private val designCategory = SessionCategory(id = 11L, name = MultiLangText(ja = "設計", en = "Design"))
    private val toolingCategory = SessionCategory(id = 12L, name = MultiLangText(ja = "ツール", en = "Tooling"))

    private val sampleTimetable = Timetable(
        items = persistentListOf(
            item("compose", MultiLangText(ja = "Compose 入門", en = "Getting started with Compose"), "Speaker A"),
            item(
                "kmp",
                MultiLangText(ja = "KMP の実践", en = "Kotlin Multiplatform in practice"),
                "Speaker B",
                day = DroidKaigi2026Day.Day2,
                language = Language.ENGLISH,
                sessionType = SessionType.CODELABS,
                category = toolingCategory,
            ),
        ),
        bookmarks = persistentSetOf(),
        categories = persistentListOf(designCategory, toolingCategory),
    )

    @Test
    fun opens_with_nothing_searched_on() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            val initial = uiStates.awaitItem()
            assertEquals("", initial.queryText)
            assertEquals(SearchResultUiState.Empty.Initial, initial.result)
        }
    }

    @Test
    fun a_typed_word_keeps_only_the_sessions_it_matches() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("Compose"))
            val found = assertIs<SearchResultUiState.Found>(uiStates.awaitItem().result)
            assertEquals(listOf("compose"), found.timeSlots.flatMap { slot -> slot.items.map { it.id.value } })
        }
    }

    @Test
    fun surrounding_spaces_do_not_change_the_word_being_searched_or_marked() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("  Compose  "))
            val state = uiStates.awaitItem()
            val found = assertIs<SearchResultUiState.Found>(state.result)
            assertEquals("  Compose  ", state.queryText)
            assertEquals("Compose", found.titleMark)
            assertEquals(listOf("compose"), found.timeSlots.flatMap { slot -> slot.items.map { it.id.value } })
        }
    }

    @Test
    fun a_speaker_name_matches_as_well_as_a_title() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("Speaker B"))
            val found = assertIs<SearchResultUiState.Found>(uiStates.awaitItem().result)
            assertEquals(listOf("kmp"), found.timeSlots.flatMap { slot -> slot.items.map { it.id.value } })
        }
    }

    @Test
    fun a_query_cannot_match_across_two_speaker_names() {
        val item = item(
            id = "speakers",
            title = MultiLangText(ja = "セッション", en = "Session"),
            speaker = "Alice",
        ).copy(
            speakers = persistentListOf(
                speaker("Alice"),
                speaker("Bob"),
            ),
        )
        val timetable = Timetable(
            items = persistentListOf(item),
            bookmarks = persistentSetOf(),
            categories = persistentListOf(designCategory),
        )

        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, timetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("e, B"))
            assertEquals(SearchResultUiState.Empty.NoMatch, uiStates.awaitItem().result)
        }
    }

    @Test
    fun a_word_typed_in_japanese_matches_whichever_language_the_app_runs_in() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("実践"))
            val found = assertIs<SearchResultUiState.Found>(uiStates.awaitItem().result)
            assertEquals(listOf("kmp"), found.timeSlots.flatMap { slot -> slot.items.map { it.id.value } })
        }
    }

    @Test
    fun a_word_no_session_matches_reports_no_match() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("zzzz"))
            assertEquals(SearchResultUiState.Empty.NoMatch, uiStates.awaitItem().result)
        }
    }

    @Test
    fun clearing_the_query_returns_to_the_opening_state() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("Compose"))
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText(""))
            assertEquals(SearchResultUiState.Empty.Initial, uiStates.awaitItem().result)
        }
    }

    @Test
    fun toggle_bookmark_action_forwards_the_id_to_the_mutation() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ToggleBookmark(TimetableItemId("compose")))
            assertEquals(TimetableItemId("compose"), graph.favoriteMutationKey.invocations.receive())
        }
    }

    @Test
    fun mutation_failure_surfaces_ShowMessage_on_channel() {
        graph.favoriteMutationKey.failWith(IllegalStateException("boom"))
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()
            send(SearchScreenAction.ToggleBookmark(TimetableItemId("compose")))

            val result = results.awaitItem()
            assertIs<SearchScreenActionResult.ShowMessage>(result)
            assertIs<AppError.UnknownException>(result.message.error)
        }
    }

    @Test
    fun bookmark_addition_emits_FavoriteAdded_on_channel() {
        graph.favoriteMutationKey.complete(true)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()
            send(SearchScreenAction.ToggleBookmark(TimetableItemId("kmp")))

            val result = results.awaitItem()
            assertEquals(SearchScreenActionResult.FavoriteAdded, result)
        }
    }

    @Test
    fun bookmark_removal_does_not_emit_FavoriteAdded_on_channel() {
        graph.favoriteMutationKey.complete(false)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()
            send(SearchScreenAction.ToggleBookmark(TimetableItemId("compose")))

            results.expectNoEvents()
        }
    }

    @Test
    fun picking_a_day_narrows_the_result_to_it() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ToggleDay(DroidKaigi2026Day.Day2))
            val state = uiStates.awaitItem()
            assertEquals(DroidKaigi2026Day.Day2, state.filterRow.selectedDay)
            assertEquals(listOf("kmp"), assertIs<SearchResultUiState.Found>(state.result).timeSlots.flatMap { slot -> slot.items.map { it.id.value } })
        }
    }

    @Test
    fun picking_the_same_day_twice_clears_it() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ToggleDay(DroidKaigi2026Day.Day2))
            uiStates.awaitItem()

            send(SearchScreenAction.ToggleDay(DroidKaigi2026Day.Day2))
            val cleared = uiStates.awaitItem()
            assertEquals(null, cleared.filterRow.selectedDay)
            assertEquals(SearchResultUiState.Empty.Initial, cleared.result)
        }
    }

    @Test
    fun a_filter_alone_searches_without_a_word_typed() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ToggleLanguage(Language.ENGLISH))
            val state = uiStates.awaitItem()
            assertEquals(listOf("kmp"), assertIs<SearchResultUiState.Found>(state.result).timeSlots.flatMap { slot -> slot.items.map { it.id.value } })
        }
    }

    @Test
    fun picking_a_filter_twice_clears_it() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ToggleCategory(toolingCategory.id))
            assertEquals(setOf(toolingCategory.id), uiStates.awaitItem().filterRow.selectedCategoryIds)

            send(SearchScreenAction.ToggleCategory(toolingCategory.id))
            val cleared = uiStates.awaitItem()
            assertEquals(emptySet(), cleared.filterRow.selectedCategoryIds)
            assertEquals(SearchResultUiState.Empty.Initial, cleared.result)
        }
    }

    @Test
    fun a_word_and_a_filter_both_have_to_match() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("Compose"))
            uiStates.awaitItem()

            send(SearchScreenAction.ToggleSessionType(SessionType.CODELABS))
            assertEquals(SearchResultUiState.Empty.NoMatch, uiStates.awaitItem().result)
        }
    }

    @Test
    fun the_row_offers_only_the_session_types_the_timetable_holds() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            assertEquals(
                listOf(SessionType.NORMAL, SessionType.CODELABS),
                uiStates.awaitItem().filterRow.sessionTypes,
            )
        }
    }

    @Test
    fun a_room_name_matches_as_well_as_a_title() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("NARWHAL"))
            val found = assertIs<SearchResultUiState.Found>(uiStates.awaitItem().result)
            assertEquals(2, found.matchCount)
        }
    }

    @Test
    fun clearing_the_filters_keeps_the_word_typed() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("Compose"))
            uiStates.awaitItem()
            send(SearchScreenAction.ToggleSessionType(SessionType.CODELABS))
            assertEquals(SearchResultUiState.Empty.NoMatch, uiStates.awaitItem().result)

            send(SearchScreenAction.ClearFilters)
            val cleared = uiStates.awaitItem()
            assertEquals("Compose", cleared.queryText)
            assertEquals(emptySet(), cleared.filterRow.selectedSessionTypes)
            assertEquals(
                listOf("compose"),
                assertIs<SearchResultUiState.Found>(cleared.result)
                    .timeSlots.flatMap { slot -> slot.items.map { it.id.value } },
            )
        }
    }

    private fun item(
        id: String,
        title: MultiLangText,
        speaker: String,
        day: DroidKaigi2026Day = DroidKaigi2026Day.Day1,
        language: Language = Language.MIXED,
        sessionType: SessionType = SessionType.NORMAL,
        category: SessionCategory = designCategory,
    ) = testTimetableItem(
        id = id,
        title = title.en,
        room = SessionRoom.NARWHAL,
        speaker = speaker,
        language = language,
        day = day,
        startsAt = "10:00",
        endsAt = "10:40",
        sessionType = sessionType,
        category = category,
    ).copy(title = title)

    private fun speaker(name: String) = TimetableSpeaker(
        id = TimetableSpeakerId(name),
        name = name,
        tagLine = "",
        iconUrl = null,
    )

    @Test
    fun bookmark_addition_offers_the_first_favorite_guidance_while_it_is_pending() {
        graph.favoriteMutationKey.complete(true)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = true) },
        ) {
            uiStates.awaitItem()
            send(SearchScreenAction.ToggleBookmark(TimetableItemId("kmp")))

            assertEquals(SearchScreenActionResult.FavoriteAdded, results.awaitItem())
            assertEquals(
                SearchScreenActionResult.OfferFirstFavoriteGuidance(SessionRoom.NARWHAL),
                results.awaitItem(),
            )
        }
    }

    @Test
    fun bookmark_addition_does_not_offer_the_first_favorite_guidance_once_it_was_answered() {
        graph.favoriteMutationKey.complete(true)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()
            send(SearchScreenAction.ToggleBookmark(TimetableItemId("kmp")))

            assertEquals(SearchScreenActionResult.FavoriteAdded, results.awaitItem())
            results.expectNoEvents()
        }
    }
}

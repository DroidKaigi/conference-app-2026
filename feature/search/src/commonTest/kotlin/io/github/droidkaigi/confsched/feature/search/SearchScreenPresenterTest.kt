package io.github.droidkaigi.confsched.feature.search

import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.SessionType
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import io.github.droidkaigi.confsched.feature.search.component.SearchResultUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SearchScreenPresenterTest {

    private val graph = createGraph<SearchScreenTestGraph>()

    private val sampleTimetable = Timetable(
        items = persistentListOf(
            item("compose", MultiLangText(ja = "Compose 入門", en = "Getting started with Compose"), "Speaker A"),
            item("kmp", MultiLangText(ja = "KMP の実践", en = "Kotlin Multiplatform in practice"), "Speaker B"),
        ),
        bookmarks = persistentSetOf(),
    )

    @Test
    fun opens_with_nothing_searched_on() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable) },
        ) {
            val initial = uiStates.awaitItem()
            assertEquals("", initial.query)
            assertEquals(SearchResultUiState.Empty.Initial, initial.result)
        }
    }

    @Test
    fun a_typed_word_keeps_only_the_sessions_it_matches() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("Compose"))
            val found = assertIs<SearchResultUiState.Found>(uiStates.awaitItem().result)
            assertEquals(listOf("compose"), found.items.map { it.id.value })
        }
    }

    @Test
    fun a_speaker_name_matches_as_well_as_a_title() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("Speaker B"))
            val found = assertIs<SearchResultUiState.Found>(uiStates.awaitItem().result)
            assertEquals(listOf("kmp"), found.items.map { it.id.value })
        }
    }

    @Test
    fun a_word_typed_in_japanese_matches_whichever_language_the_app_runs_in() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("実践"))
            val found = assertIs<SearchResultUiState.Found>(uiStates.awaitItem().result)
            assertEquals(listOf("kmp"), found.items.map { it.id.value })
        }
    }

    @Test
    fun a_word_no_session_matches_reports_no_match() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable) },
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
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText("Compose"))
            uiStates.awaitItem()

            send(SearchScreenAction.ChangeQueryText(""))
            assertEquals(SearchResultUiState.Empty.Initial, uiStates.awaitItem().result)
        }
    }

    @Test
    fun bookmark_action_forwards_the_id_to_the_mutation() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable) },
        ) {
            uiStates.awaitItem()

            send(SearchScreenAction.Bookmark(TimetableItemId("compose")))
            assertEquals(TimetableItemId("compose"), graph.favoriteMutationKey.invocations.receive())
        }
    }

    @Test
    fun mutation_failure_surfaces_ShowMessage_on_channel() {
        graph.favoriteMutationKey.failWith(IllegalStateException("boom"))
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> searchScreenPresenter(channel, sampleTimetable) },
        ) {
            uiStates.awaitItem()
            send(SearchScreenAction.Bookmark(TimetableItemId("compose")))

            val result = results.awaitItem()
            assertIs<SearchScreenActionResult.ShowMessage>(result)
            assertEquals("boom", result.message.text)
        }
    }

    private fun item(id: String, title: MultiLangText, speaker: String) = TimetableItem(
        id = TimetableItemId(id),
        title = title,
        room = Room.NARWHAL,
        speaker = speaker,
        language = Language.MIXED,
        day = DroidKaigi2026Day.Day1,
        startsAt = "10:00",
        endsAt = "10:40",
        sessionType = SessionType.NORMAL,
    )
}

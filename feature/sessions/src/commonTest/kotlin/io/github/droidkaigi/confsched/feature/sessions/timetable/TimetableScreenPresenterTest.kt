package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.AppError
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableQueryKey
import io.github.droidkaigi.confsched.core.testing.FakeKaigiLogger
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import io.github.droidkaigi.confsched.core.testing.testTimetableItem
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableListSectionUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.test.runTest
import soil.query.QueryId
import soil.query.SwrCachePlus
import soil.query.buildQueryKey
import soil.query.compose.SwrClientProvider
import soil.query.compose.rememberQuery
import soil.query.core.Reply
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class TimetableScreenPresenterTest {

    private val graph = createGraph<TimetableScreenTestGraph>()

    private val sampleTimetable = Timetable(
        items = persistentListOf(
            testTimetableItem(id = "d1a", title = "Day1 A", room = SessionRoom.NARWHAL, speaker = "Sp1", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "10:00", endsAt = "10:40"),
            testTimetableItem(id = "d1b", title = "Day1 B", room = SessionRoom.OTTER, speaker = "Sp2", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "11:00", endsAt = "11:40"),
            testTimetableItem(id = "d2a", title = "Day2 A", room = SessionRoom.NARWHAL, speaker = "Sp3", language = Language.ENGLISH, day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40"),
        ),
        bookmarks = persistentSetOf(TimetableItemId("d1a")),
    )

    @Test
    fun initial_state_and_actions_drive_state_mutation_and_channel() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            val initial = uiStates.awaitItem()
            assertEquals(DroidKaigi2026Day.Day1, initial.day)
            assertEquals(TimetableViewMode.List, initial.viewMode)
            assertEquals(listOf("d1a", "d1b"), initial.selectedListSection.timeSlots.flatMap { slot -> slot.items.map { it.id.value } })
            assertEquals(listOf("d1a", "d1b"), initial.timetableGridSection.sessions.map { it.id.value })
            assertEquals(600, initial.timetableGridSection.nowMinute)
            assertEquals(setOf(TimetableItemId("d1a")), initial.selectedListSection.bookmarks)

            send(TimetableScreenAction.SelectDay(DroidKaigi2026Day.Day2))
            val onDay2 = uiStates.awaitItem()
            assertEquals(DroidKaigi2026Day.Day2, onDay2.day)
            assertEquals(listOf("d2a"), onDay2.selectedListSection.timeSlots.flatMap { slot -> slot.items.map { it.id.value } })
            assertEquals(listOf("d2a"), onDay2.timetableGridSection.sessions.map { it.id.value })
            assertEquals(null, onDay2.timetableGridSection.nowMinute)

            send(TimetableScreenAction.Bookmark(TimetableItemId("d2a")))
            assertEquals(TimetableItemId("d2a"), graph.favoriteMutationKey.invocations.receive())
        }
    }

    @Test
    fun sessions_sharing_a_time_are_grouped_into_one_slot() {
        val concurrent = Timetable(
            items = persistentListOf(
                testTimetableItem(id = "d1a", title = "Day1 A", room = SessionRoom.NARWHAL, speaker = "Sp1", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "10:00", endsAt = "10:40"),
                testTimetableItem(id = "d1b", title = "Day1 B", room = SessionRoom.OTTER, speaker = "Sp2", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "10:00", endsAt = "10:40"),
                testTimetableItem(id = "d1c", title = "Day1 C", room = SessionRoom.NARWHAL, speaker = "Sp3", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "11:00", endsAt = "11:40"),
            ),
        )
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = concurrent, offersFirstFavoriteGuidance = false) },
        ) {
            val slots = uiStates.awaitItem().selectedListSection.timeSlots
            assertEquals(listOf("10:00" to "10:40", "11:00" to "11:40"), slots.map { it.startsAt to it.endsAt })
            assertEquals(listOf("d1a", "d1b"), slots[0].items.map { it.id.value })
            assertEquals(listOf("d1c"), slots[1].items.map { it.id.value })
        }
    }

    @Test
    fun a_requested_day_becomes_the_selected_one() {
        graph.dayRequestStore.request(DroidKaigi2026Day.Day2)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            assertEquals(listOf("d2a"), uiStates.awaitDay(DroidKaigi2026Day.Day2).timetableGridSection.sessions.map { it.id.value })
        }
    }

    @Test
    fun a_second_day_request_while_composed_is_applied_too() {
        graph.dayRequestStore.request(DroidKaigi2026Day.Day2)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitDay(DroidKaigi2026Day.Day2)

            graph.dayRequestStore.request(DroidKaigi2026Day.Day1)
            assertEquals(DroidKaigi2026Day.Day1, uiStates.awaitItem().day)

            graph.dayRequestStore.request(DroidKaigi2026Day.Day2)
            assertEquals(DroidKaigi2026Day.Day2, uiStates.awaitItem().day)
        }
    }

    @Test
    fun a_day_request_is_consumed_once_so_a_later_choice_stands() {
        graph.dayRequestStore.request(DroidKaigi2026Day.Day2)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitDay(DroidKaigi2026Day.Day2)

            send(TimetableScreenAction.SelectDay(DroidKaigi2026Day.Day1))
            assertEquals(DroidKaigi2026Day.Day1, uiStates.awaitItem().day)
            uiStates.expectNoEvents()
        }
    }

    @Test
    fun toggling_view_mode_switches_between_list_and_grid() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()

            send(TimetableScreenAction.SwitchToGridView)
            assertEquals(TimetableViewMode.Grid, uiStates.awaitItem().viewMode)

            send(TimetableScreenAction.SwitchToGridView)
            assertEquals(TimetableViewMode.List, uiStates.awaitItem().viewMode)
        }
    }

    @Test
    fun mutation_failure_surfaces_ShowMessage_on_channel() {
        graph.favoriteMutationKey.failWith(IllegalStateException("boom"))
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()
            send(TimetableScreenAction.Bookmark(TimetableItemId("d1a")))

            val result = results.awaitItem()
            assertIs<TimetableScreenActionResult.ShowMessage>(result)
            assertIs<AppError.UnknownException>(result.message.error)
        }
    }

    @Test
    fun bookmark_addition_emits_FavoriteAdded_on_channel() {
        graph.favoriteMutationKey.complete(true)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()
            send(TimetableScreenAction.Bookmark(TimetableItemId("d1b")))

            val result = results.awaitItem()
            assertEquals(TimetableScreenActionResult.FavoriteAdded, result)
        }
    }

    @Test
    fun bookmark_removal_does_not_emit_FavoriteAdded_on_channel() {
        graph.favoriteMutationKey.complete(false)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable, offersFirstFavoriteGuidance = false) },
        ) {
            uiStates.awaitItem()
            send(TimetableScreenAction.Bookmark(TimetableItemId("d1a")))

            results.expectNoEvents()
        }
    }

    @Test
    fun soil_rememberQuery_goes_loading_to_content_under_molecule() = runTest {
        val client = SwrCachePlus(backgroundScope)

        val queryKey: TimetableQueryKey = buildQueryKey(
            id = QueryId("test-timetable"),
            fetch = { sampleTimetable },
        )

        moleculeFlow(RecompositionMode.Immediate) {
            var reply by remember { mutableStateOf<Reply<Timetable>?>(null) }
            val screenContext = object : ScreenContext {
                override val logger: KaigiLogger = FakeKaigiLogger()
            }
            SwrClientProvider(client = client) {
                context(screenContext) { reply = rememberProbeQueryReply(queryKey) }
            }
            reply
        }.distinctUntilChanged().test {
            assertIs<Reply.None>(awaitItem())
            val loaded = awaitItem()
            assertIs<Reply.Some<Timetable>>(loaded)
            assertEquals(sampleTimetable, loaded.value)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun countdown_banner_shows_next_favorited_session_and_handles_hours_and_past_sessions() {
        val timetable = Timetable(
            items = persistentListOf(
                testTimetableItem(id = "s1", title = "Session 1", room = SessionRoom.NARWHAL, speaker = "Sp1", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "10:00", endsAt = "10:40"),
                testTimetableItem(id = "s2", title = "Session 2", room = SessionRoom.OTTER, speaker = "Sp2", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "11:00", endsAt = "11:40"),
            ),
            bookmarks = persistentSetOf(TimetableItemId("s1"), TimetableItemId("s2")),
        )

        graph.clock.instant = DroidKaigi2026Day.Day1.at(8, 30)

        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = timetable, offersFirstFavoriteGuidance = false) },
        ) {
            val initialState = uiStates.awaitItem()
            assertEquals(1.hours + 30.minutes, initialState.selectedListSection.countdownBannerUiState?.remainingDuration)
            assertEquals("s1", initialState.selectedListSection.countdownBannerUiState?.nextSessions?.first()?.id?.value)

            graph.clock.advanceBy(1.hours)
            val at930 = uiStates.awaitItem()
            assertEquals(30.minutes, at930.selectedListSection.countdownBannerUiState?.remainingDuration)

            graph.clock.advanceBy(40.minutes)
            val at1010 = uiStates.awaitItem()
            assertEquals(50.minutes, at1010.selectedListSection.countdownBannerUiState?.remainingDuration)
            assertEquals("s2", at1010.selectedListSection.countdownBannerUiState?.nextSessions?.first()?.id?.value)

            graph.clock.advanceBy(1.hours)
            val at1110 = uiStates.awaitItem()
            assertEquals(null, at1110.selectedListSection.countdownBannerUiState)

            graph.clock.instant = DroidKaigi2026Day.Day1.at(8, 30)
            send(TimetableScreenAction.SelectDay(DroidKaigi2026Day.Day2))
            val atDay2 = uiStates.awaitItem()
            assertEquals(null, atDay2.selectedListSection.countdownBannerUiState)
        }
    }

    @Test
    fun countdown_banner_is_null_when_no_favorited_sessions_exist() {
        val timetable = Timetable(
            items = persistentListOf(
                testTimetableItem(id = "s1", title = "S1", room = SessionRoom.NARWHAL, speaker = "Sp1", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "10:00", endsAt = "10:40"),
            ),
            bookmarks = persistentSetOf(),
        )

        graph.clock.instant = DroidKaigi2026Day.Day1.at(8, 30)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = timetable, offersFirstFavoriteGuidance = false) },
        ) {
            val initialState = uiStates.awaitItem()
            assertEquals(null, initialState.selectedListSection.countdownBannerUiState)
        }
    }

    @Test
    fun countdown_banner_shows_multiple_sessions_if_concurrent_favorited_sessions_exist() {
        val timetable = Timetable(
            items = persistentListOf(
                testTimetableItem(id = "s1", title = "S1", room = SessionRoom.NARWHAL, speaker = "Sp1", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "10:00", endsAt = "10:40"),
                testTimetableItem(id = "s2", title = "S2", room = SessionRoom.OTTER, speaker = "Sp2", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "10:00", endsAt = "10:40"),
            ),
            bookmarks = persistentSetOf(TimetableItemId("s1"), TimetableItemId("s2")),
        )

        graph.clock.instant = DroidKaigi2026Day.Day1.at(8, 30)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = timetable, offersFirstFavoriteGuidance = false) },
        ) {
            val initialState = uiStates.awaitItem()
            val bannerState = initialState.selectedListSection.countdownBannerUiState
            assertEquals(1.hours + 30.minutes, bannerState?.remainingDuration)
            assertEquals(listOf("s1", "s2"), bannerState?.nextSessions?.map { it.id.value })
        }
    }

    @Composable
    context(_: ScreenContext)
    private fun rememberProbeQueryReply(key: TimetableQueryKey): Reply<Timetable> =
        rememberQuery(key).reply

    private val TimetableScreenUiState.selectedListSection: TimetableListSectionUiState
        get() = timetableListSections.getValue(day)

    private suspend fun ReceiveTurbine<TimetableScreenUiState>.awaitDay(day: DroidKaigi2026Day): TimetableScreenUiState {
        while (true) {
            val state = awaitItem()
            if (state.day == day) return state
        }
    }

    @Test
    fun bookmark_addition_offers_the_first_favorite_guidance_while_it_is_pending() {
        graph.favoriteMutationKey.complete(true)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable, offersFirstFavoriteGuidance = true)
            },
        ) {
            uiStates.awaitItem()
            send(TimetableScreenAction.Bookmark(TimetableItemId("d1b")))

            assertEquals(TimetableScreenActionResult.FavoriteAdded, results.awaitItem())
            assertEquals(
                TimetableScreenActionResult.OfferFirstFavoriteGuidance(SessionRoom.OTTER),
                results.awaitItem(),
            )
        }
    }

    @Test
    fun bookmark_addition_does_not_offer_the_first_favorite_guidance_once_it_was_answered() {
        graph.favoriteMutationKey.complete(true)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable, offersFirstFavoriteGuidance = false)
            },
        ) {
            uiStates.awaitItem()
            send(TimetableScreenAction.Bookmark(TimetableItemId("d1b")))

            assertEquals(TimetableScreenActionResult.FavoriteAdded, results.awaitItem())
            results.expectNoEvents()
        }
    }
}

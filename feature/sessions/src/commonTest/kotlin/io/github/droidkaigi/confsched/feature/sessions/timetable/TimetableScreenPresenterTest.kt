package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableQueryKey
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
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
import kotlin.time.Instant

class TimetableScreenPresenterTest {

    private val graph = createGraph<TimetableScreenTestGraph>()

    private val sampleTimetable = Timetable(
        items = persistentListOf(
            TimetableItem(
                id = TimetableItemId("d1a"),
                title = MultiLangText(ja = "Day1 A", en = "Day1 A"),
                room = Room.NARWHAL,
                speaker = "Sp1",
                language = Language.ENGLISH,
                day = DroidKaigi2026Day.Day1,
                startsAt = "10:00",
                endsAt = "10:40",
                startsAtInstant = Instant.parse("2026-09-02T10:00:00Z"),
                endsAtInstant = Instant.parse("2026-09-02T10:40:00Z"),
            ),
            TimetableItem(
                id = TimetableItemId("d1b"),
                title = MultiLangText(ja = "Day1 B", en = "Day1 B"),
                room = Room.OTTER,
                speaker = "Sp2",
                language = Language.ENGLISH,
                day = DroidKaigi2026Day.Day1,
                startsAt = "11:00",
                endsAt = "11:40",
                startsAtInstant = Instant.parse("2026-09-02T11:00:00Z"),
                endsAtInstant = Instant.parse("2026-09-02T11:40:00Z"),
            ),
            TimetableItem(
                id = TimetableItemId("d2a"),
                title = MultiLangText(ja = "Day2 A", en = "Day2 A"),
                room = Room.NARWHAL,
                speaker = "Sp3",
                language = Language.ENGLISH,
                day = DroidKaigi2026Day.Day2,
                startsAt = "10:00",
                endsAt = "10:40",
                startsAtInstant = Instant.parse("2026-09-03T10:00:00Z"),
                endsAtInstant = Instant.parse("2026-09-03T10:40:00Z"),
            ),
        ),
        bookmarks = persistentSetOf(TimetableItemId("d1a")),
    )

    @Test
    fun initial_state_and_actions_drive_state_mutation_and_channel() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable) },
        ) {
            val initial = uiStates.awaitItem()
            assertEquals(DroidKaigi2026Day.Day1, initial.day)
            assertEquals(listOf("d1a", "d1b"), initial.timetableListSection.timeSlots.flatMap { slot -> slot.items.map { it.id.value } })
            assertEquals(setOf(TimetableItemId("d1a")), initial.timetableListSection.bookmarks)

            send(TimetableScreenAction.SelectDay(DroidKaigi2026Day.Day2))
            val onDay2 = uiStates.awaitItem()
            assertEquals(DroidKaigi2026Day.Day2, onDay2.day)
            assertEquals(listOf("d2a"), onDay2.timetableListSection.timeSlots.flatMap { slot -> slot.items.map { it.id.value } })

            send(TimetableScreenAction.ToggleBookmark(TimetableItemId("d2a")))
            assertEquals(TimetableItemId("d2a"), graph.favoriteMutationKey.invocations.receive())
        }
    }

    @Test
    fun sessions_sharing_a_time_are_grouped_into_one_slot() {
        val concurrent = Timetable(
            items = persistentListOf(
                TimetableItem(
                    id = TimetableItemId("d1a"),
                    title = MultiLangText(ja = "Day1 A", en = "Day1 A"),
                    room = Room.NARWHAL,
                    speaker = "Sp1",
                    language = Language.ENGLISH,
                    day = DroidKaigi2026Day.Day1,
                    startsAt = "10:00",
                    endsAt = "10:40",
                    startsAtInstant = Instant.parse("2026-09-02T10:00:00Z"),
                    endsAtInstant = Instant.parse("2026-09-02T10:40:00Z"),
                ),
                TimetableItem(
                    id = TimetableItemId("d1b"),
                    title = MultiLangText(ja = "Day1 B", en = "Day1 B"),
                    room = Room.OTTER,
                    speaker = "Sp2",
                    language = Language.ENGLISH,
                    day = DroidKaigi2026Day.Day1,
                    startsAt = "10:00",
                    endsAt = "10:40",
                    startsAtInstant = Instant.parse("2026-09-02T10:00:00Z"),
                    endsAtInstant = Instant.parse("2026-09-02T10:40:00Z"),
                ),
                TimetableItem(
                    id = TimetableItemId("d1c"),
                    title = MultiLangText(ja = "Day1 C", en = "Day1 C"),
                    room = Room.NARWHAL,
                    speaker = "Sp3",
                    language = Language.ENGLISH,
                    day = DroidKaigi2026Day.Day1,
                    startsAt = "11:00",
                    endsAt = "11:40",
                    startsAtInstant = Instant.parse("2026-09-02T11:00:00Z"),
                    endsAtInstant = Instant.parse("2026-09-02T11:40:00Z"),
                ),
            ),
        )
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = concurrent) },
        ) {
            val slots = uiStates.awaitItem().timetableListSection.timeSlots
            assertEquals(listOf("10:00" to "10:40", "11:00" to "11:40"), slots.map { it.startsAt to it.endsAt })
            assertEquals(listOf("d1a", "d1b"), slots[0].items.map { it.id.value })
            assertEquals(listOf("d1c"), slots[1].items.map { it.id.value })
        }
    }

    @Test
    fun switching_to_the_grid_view_only_logs_until_the_grid_exists() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable) },
        ) {
            uiStates.awaitItem()

            send(TimetableScreenAction.SwitchToGridView)
            assertEquals("TODO: render the grid view", graph.logger.debugMessages.receive())
        }
    }

    @Test
    fun mutation_failure_surfaces_ShowMessage_on_channel() {
        graph.favoriteMutationKey.failWith(IllegalStateException("boom"))
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable) },
        ) {
            uiStates.awaitItem()
            send(TimetableScreenAction.ToggleBookmark(TimetableItemId("d1a")))

            val result = results.awaitItem()
            assertIs<TimetableScreenActionResult.ShowMessage>(result)
            assertEquals("boom", result.message.text)
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
            val screenContext = object : ScreenContext {}
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

    @Composable
    context(_: ScreenContext)
    private fun rememberProbeQueryReply(key: TimetableQueryKey): Reply<Timetable> =
        rememberQuery(key).reply
}

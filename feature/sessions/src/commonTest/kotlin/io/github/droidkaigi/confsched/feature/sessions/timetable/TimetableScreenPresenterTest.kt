package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.FavoriteTimetableItemIdMutationKey
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableQueryKey
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.test.runTest
import soil.query.MutationId
import soil.query.QueryId
import soil.query.SwrCachePlus
import soil.query.buildMutationKey
import soil.query.buildQueryKey
import soil.query.compose.SwrClientProvider
import soil.query.compose.rememberQuery
import soil.query.core.Reply
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TimetableScreenPresenterTest {

    private val sampleTimetable = Timetable(
        items = persistentListOf(
            TimetableItem(TimetableItemId("d1a"), "Day1 A", "Room1", "Sp1", DroidKaigi2026Day.Day1, "10:00", "10:40"),
            TimetableItem(TimetableItemId("d1b"), "Day1 B", "Room2", "Sp2", DroidKaigi2026Day.Day1, "11:00", "11:40"),
            TimetableItem(TimetableItemId("d2a"), "Day2 A", "Room1", "Sp3", DroidKaigi2026Day.Day2, "10:00", "10:40"),
        ),
        bookmarks = persistentSetOf(TimetableItemId("d1a")),
        rawResponse = """{ "sessions": [] }""",
    )

    @Test
    fun initial_state_and_actions_drive_state_mutation_and_channel() {
        val mutateInvocations = Channel<TimetableItemId>(Channel.UNLIMITED)
        val favoriteKey: FavoriteTimetableItemIdMutationKey = buildMutationKey(
            id = MutationId("test-favorite"),
            mutate = { id -> mutateInvocations.trySend(id) },
        )
        runPresenterTest(
            presenterContext = TimetablePresenterContext(favoriteTimetableItemIdMutationKey = favoriteKey),
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable) },
        ) {
            val initial = uiStates.awaitItem()
            assertEquals(DroidKaigi2026Day.Day1, initial.day)
            assertEquals(listOf("d1a", "d1b"), initial.sessions.map { it.id.value })
            assertEquals(setOf(TimetableItemId("d1a")), initial.bookmarks)

            send(TimetableScreenAction.SelectDay(DroidKaigi2026Day.Day2))
            val onDay2 = uiStates.awaitItem()
            assertEquals(DroidKaigi2026Day.Day2, onDay2.day)
            assertEquals(listOf("d2a"), onDay2.sessions.map { it.id.value })

            send(TimetableScreenAction.Bookmark(TimetableItemId("d2a")))
            assertEquals(TimetableItemId("d2a"), mutateInvocations.receive())
        }
    }

    @Test
    fun toggling_raw_response_flips_expansion_and_carries_the_payload() {
        val favoriteKey: FavoriteTimetableItemIdMutationKey = buildMutationKey(
            id = MutationId("test-favorite-raw"),
            mutate = { },
        )
        runPresenterTest(
            presenterContext = TimetablePresenterContext(favoriteTimetableItemIdMutationKey = favoriteKey),
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable) },
        ) {
            val initial = uiStates.awaitItem()
            assertEquals("""{ "sessions": [] }""", initial.rawResponse)
            assertEquals(false, initial.isRawResponseExpanded)

            send(TimetableScreenAction.ToggleRawResponse)
            assertEquals(true, uiStates.awaitItem().isRawResponseExpanded)

            send(TimetableScreenAction.ToggleRawResponse)
            assertEquals(false, uiStates.awaitItem().isRawResponseExpanded)
        }
    }

    @Test
    fun mutation_failure_surfaces_ShowMessage_on_channel() {
        val failingKey: FavoriteTimetableItemIdMutationKey = buildMutationKey(
            id = MutationId("test-failing"),
            mutate = { _ -> error("boom") },
        )
        runPresenterTest(
            presenterContext = TimetablePresenterContext(favoriteTimetableItemIdMutationKey = failingKey),
            presenter = { channel -> timetableScreenPresenter(screenChannel = channel, timetable = sampleTimetable) },
        ) {
            uiStates.awaitItem()
            send(TimetableScreenAction.Bookmark(TimetableItemId("d1a")))

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

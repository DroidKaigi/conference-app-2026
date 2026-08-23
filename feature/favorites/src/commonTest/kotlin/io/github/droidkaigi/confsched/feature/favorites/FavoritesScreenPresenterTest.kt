package io.github.droidkaigi.confsched.feature.favorites

import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import io.github.droidkaigi.confsched.core.testing.testTimetableItem
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FavoritesScreenPresenterTest {

    private val graph = createGraph<FavoritesScreenTestGraph>()

    private val sampleTimetable = Timetable(
        items = persistentListOf(
            testTimetableItem(id = "d1a", title = "Day1 A", room = Room.NARWHAL, speaker = "Sp1", language = Language.MIXED, day = DroidKaigi2026Day.Day1, startsAt = "10:00", endsAt = "10:40"),
            testTimetableItem(id = "d1b", title = "Day1 B", room = Room.OTTER, speaker = "Sp2", language = Language.MIXED, day = DroidKaigi2026Day.Day1, startsAt = "11:00", endsAt = "11:40"),
            testTimetableItem(id = "d2a", title = "Day2 A", room = Room.NARWHAL, speaker = "Sp3", language = Language.MIXED, day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40"),
            testTimetableItem(id = "d2b", title = "Day2 B", room = Room.OTTER, speaker = "Sp4", language = Language.MIXED, day = DroidKaigi2026Day.Day2, startsAt = "11:00", endsAt = "11:40"),
        ),
        bookmarks = persistentSetOf(TimetableItemId("d1a"), TimetableItemId("d2a"), TimetableItemId("d2b")),
    )

    @Test
    fun initial_state_lists_only_favorited_items_grouped_by_day_and_time() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> favoritesScreenPresenter(screenChannel = channel, timetable = sampleTimetable) },
        ) {
            val initial = uiStates.awaitItem()
            assertEquals(null, initial.selectedDayFilter)
            assertEquals(true, initial.favoritesListSection.dayHeadersVisible)

            val slots = initial.favoritesListSection.timeSlots
            assertEquals(
                listOf(
                    Triple(DroidKaigi2026Day.Day1, "10:00", "10:40"),
                    Triple(DroidKaigi2026Day.Day2, "10:00", "10:40"),
                    Triple(DroidKaigi2026Day.Day2, "11:00", "11:40"),
                ),
                slots.map { slot -> Triple(slot.day, slot.startsAt, slot.endsAt) },
            )
            assertEquals(listOf("d1a"), slots[0].items.map { it.id.value })
            assertEquals(listOf("d2a"), slots[1].items.map { it.id.value })
            assertEquals(listOf("d2b"), slots[2].items.map { it.id.value })
        }
    }

    @Test
    fun selecting_a_day_filter_narrows_the_list_to_that_day() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> favoritesScreenPresenter(screenChannel = channel, timetable = sampleTimetable) },
        ) {
            uiStates.awaitItem()

            send(FavoritesScreenAction.SelectDayFilter(DroidKaigi2026Day.Day2))
            val onDay2 = uiStates.awaitItem()
            assertEquals(DroidKaigi2026Day.Day2, onDay2.selectedDayFilter)
            assertEquals(false, onDay2.favoritesListSection.dayHeadersVisible)
            assertEquals(
                listOf("d2a", "d2b"),
                onDay2.favoritesListSection.timeSlots.flatMap { slot -> slot.items.map { it.id.value } },
            )
        }
    }

    @Test
    fun bookmark_action_forwards_the_id_to_the_mutation() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> favoritesScreenPresenter(screenChannel = channel, timetable = sampleTimetable) },
        ) {
            uiStates.awaitItem()

            send(FavoritesScreenAction.Bookmark(TimetableItemId("d1a")))
            assertEquals(TimetableItemId("d1a"), graph.favoriteMutationKey.invocations.receive())
        }
    }

    @Test
    fun mutation_failure_surfaces_ShowMessage_on_channel() {
        graph.favoriteMutationKey.failWith(IllegalStateException("boom"))
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> favoritesScreenPresenter(screenChannel = channel, timetable = sampleTimetable) },
        ) {
            uiStates.awaitItem()
            send(FavoritesScreenAction.Bookmark(TimetableItemId("d1a")))

            val result = results.awaitItem()
            assertIs<FavoritesScreenActionResult.ShowMessage>(result)
            assertEquals("boom", result.message.text)
        }
    }
}

package io.github.droidkaigi.confsched.feature.sessions.timetable

import dev.zacsweers.metro.createGraphFactory
import io.github.droidkaigi.confsched.core.model.DisplayLanguage
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.SessionMemoEdit
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import io.github.droidkaigi.confsched.core.testing.testTimetableItem
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailScreenUiState.DescriptionDisplay
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimetableItemDetailScreenPresenterTest {

    private val graph = createGraphFactory<TimetableItemDetailScreenTestGraph.Factory>()
        .create(TimetableItemId("d1a"))

    private val timetable = Timetable(
        items = persistentListOf(
            testTimetableItem(id = "d1a", title = "Day1 A", room = SessionRoom.NARWHAL, speaker = "Sp1", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "10:00", endsAt = "10:40"),
            testTimetableItem(id = "d1b", title = "Day1 B", room = SessionRoom.OTTER, speaker = "Sp2", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "10:00", endsAt = "10:40"),
            testTimetableItem(id = "d1c", title = "Day1 C", room = SessionRoom.PANDA, speaker = "Sp3", language = Language.ENGLISH, day = DroidKaigi2026Day.Day1, startsAt = "11:00", endsAt = "11:40"),
        ),
    )

    @Test
    fun the_state_carries_the_session_its_neighbours_and_the_memo() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                timetableItemDetailScreenPresenter(
                    screenChannel = channel,
                    detail = timetable.detailOf(TimetableItemId("d1a")),
                    favoriteIds = persistentSetOf(TimetableItemId("d1a")),
                    memo = "a note",
                    initialDisplayLanguage = DisplayLanguage.Japanese,
                )
            },
        ) {
            val initial = uiStates.awaitItem()
            assertEquals(TimetableItemId("d1a"), initial.item.id)
            assertEquals(listOf("d1b"), initial.sameSlotItems.map { it.item.id.value })
            assertEquals("a note", initial.memo)
            assertTrue(initial.isFavorite)
            assertFalse(initial.sameSlotItems.single().isFavorite)
        }
    }

    @Test
    fun actions_toggle_the_description_and_reach_the_mutations() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                timetableItemDetailScreenPresenter(
                    screenChannel = channel,
                    detail = timetable.detailOf(TimetableItemId("d1a")),
                    favoriteIds = persistentSetOf(),
                    memo = "",
                    initialDisplayLanguage = DisplayLanguage.Japanese,
                )
            },
        ) {
            assertEquals(DescriptionDisplay.Unmeasured, uiStates.awaitItem().descriptionDisplay)

            send(TimetableItemDetailScreenAction.UpdateDescriptionTruncation(true))
            assertEquals(DescriptionDisplay.Truncatable.Collapsed, uiStates.awaitItem().descriptionDisplay)

            send(TimetableItemDetailScreenAction.ToggleDescriptionExpansion)
            assertEquals(DescriptionDisplay.Truncatable.Expanded, uiStates.awaitItem().descriptionDisplay)

            // Expanding lifts the line limit, so the layout it triggers reports no overflow. The
            // language toggle fences that report: the state it emits shows the expansion outlived it.
            send(TimetableItemDetailScreenAction.UpdateDescriptionTruncation(false))
            send(TimetableItemDetailScreenAction.ToggleDisplayLanguage)
            val afterReport = uiStates.awaitItem()
            assertEquals(DescriptionDisplay.Truncatable.Expanded, afterReport.descriptionDisplay)
            assertEquals(DisplayLanguage.English, afterReport.displayLanguage)

            send(TimetableItemDetailScreenAction.Bookmark(TimetableItemId("d1b")))
            assertEquals(TimetableItemId("d1b"), graph.favoriteMutationKey.invocations.receive())

            send(TimetableItemDetailScreenAction.SaveMemo("written"))
            assertEquals(
                SessionMemoEdit(TimetableItemId("d1a"), "written"),
                graph.memoMutationKey.invocations.receive(),
            )
        }
    }

    @Test
    fun bookmark_addition_emits_FavoriteAdded_on_channel() {
        graph.favoriteMutationKey.complete(true)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                timetableItemDetailScreenPresenter(
                    screenChannel = channel,
                    detail = timetable.detailOf(TimetableItemId("d1a")),
                    favoriteIds = persistentSetOf(),
                    memo = "",
                    initialDisplayLanguage = DisplayLanguage.Japanese,
                )
            },
        ) {
            uiStates.awaitItem()
            send(TimetableItemDetailScreenAction.Bookmark(TimetableItemId("d1b")))

            val result = results.awaitItem()
            assertEquals(TimetableItemDetailScreenActionResult.FavoriteAdded, result)
        }
    }

    @Test
    fun bookmark_removal_does_not_emit_FavoriteAdded_on_channel() {
        graph.favoriteMutationKey.complete(false)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                timetableItemDetailScreenPresenter(
                    screenChannel = channel,
                    detail = timetable.detailOf(TimetableItemId("d1a")),
                    favoriteIds = persistentSetOf(TimetableItemId("d1a")),
                    memo = "",
                    initialDisplayLanguage = DisplayLanguage.Japanese,
                )
            },
        ) {
            uiStates.awaitItem()
            send(TimetableItemDetailScreenAction.Bookmark(TimetableItemId("d1a")))

            results.expectNoEvents()
        }
    }
}

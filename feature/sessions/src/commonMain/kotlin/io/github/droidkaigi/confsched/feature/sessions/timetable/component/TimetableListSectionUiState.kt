package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.ui.TimetableLineState
import io.github.droidkaigi.confsched.core.ui.lineState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentList
import kotlin.time.Instant

data class TimetableListSectionUiState(
    val timeSlots: PersistentList<TimeSlot>,
    val bookmarks: PersistentSet<TimetableItemId>,
) {
    data class TimeSlot(
        val startsAt: String,
        val endsAt: String,
        val timeRangeState: TimetableLineState,
        val items: PersistentList<TimetableItem>,
    )

    companion object
}

internal fun PersistentList<TimetableItem>.toTimeSlots(
    currentTime: Instant,
): PersistentList<TimetableListSectionUiState.TimeSlot> =
    groupBy { it.startsAt to it.endsAt }
        .map { (time, items) ->
            val representativeItem = items.first()

            TimetableListSectionUiState.TimeSlot(
                startsAt = time.first,
                endsAt = time.second,
                timeRangeState = representativeItem.lineState(currentTime),
                items = items.toPersistentList(),
            )
        }
        .toPersistentList()

internal fun TimetableListSectionUiState.Companion.fake(
    currentTime: Instant = Instant.parse("2026-09-02T12:00:00Z"),
): TimetableListSectionUiState {
    val timetable = Timetable.fake()

    return TimetableListSectionUiState(
        timeSlots = timetable.itemsOn(DroidKaigi2026Day.Day1).toTimeSlots(currentTime),
        bookmarks = timetable.bookmarks,
    )
}


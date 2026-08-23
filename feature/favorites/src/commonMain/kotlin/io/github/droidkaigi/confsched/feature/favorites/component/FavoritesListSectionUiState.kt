package io.github.droidkaigi.confsched.feature.favorites.component

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.ui.TimetableLineState
import io.github.droidkaigi.confsched.core.ui.lineState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlin.time.Instant

data class FavoritesListSectionUiState(
    val timeSlots: PersistentList<TimeSlot>,
) {
    data class TimeSlot(
        val day: DroidKaigi2026Day,
        val startsAt: String,
        val endsAt: String,
        val timeRangeState: TimetableLineState,
        val items: PersistentList<TimetableItem>,
    )

    companion object
}

internal fun List<TimetableItem>.toTimeSlots(
    currentTime: Instant,
): PersistentList<FavoritesListSectionUiState.TimeSlot> =
    groupBy { item -> Triple(item.day, item.startsAt, item.endsAt) }
        .map { entry ->
            val representativeItem = entry.value.first()

            FavoritesListSectionUiState.TimeSlot(
                day = entry.key.first,
                startsAt = entry.key.second,
                endsAt = entry.key.third,
                timeRangeState = representativeItem.lineState(currentTime),
                items = entry.value.sortedBy { item -> item.room }.toPersistentList(),
            )
        }
        .sortedWith(compareBy({ slot -> slot.day }, { slot -> slot.startsAt }, { slot -> slot.endsAt }))
        .toPersistentList()

internal fun FavoritesListSectionUiState.Companion.fake(
    currentTime: Instant = Instant.parse("2026-09-02T12:00:00Z"),
): FavoritesListSectionUiState {
    val timetable = Timetable.fake()
    return FavoritesListSectionUiState(
        timeSlots = timetable.items
            .filter { timetable.isFavorite(it.id) }
            .toTimeSlots(currentTime),
    )
}

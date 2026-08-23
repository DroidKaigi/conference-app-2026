package io.github.droidkaigi.confsched.feature.favorites.component

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.preview.fake
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

data class FavoritesListSectionUiState(
    val timeSlots: PersistentList<TimeSlot>,
    val dayHeadersVisible: Boolean,
) {
    data class TimeSlot(
        val day: DroidKaigi2026Day,
        val startsAt: String,
        val endsAt: String,
        val items: PersistentList<TimetableItem>,
    )

    companion object
}

internal fun List<TimetableItem>.toTimeSlots(): PersistentList<FavoritesListSectionUiState.TimeSlot> =
    groupBy { item -> Triple(item.day, item.startsAt, item.endsAt) }
        .map { entry ->
            FavoritesListSectionUiState.TimeSlot(
                day = entry.key.first,
                startsAt = entry.key.second,
                endsAt = entry.key.third,
                items = entry.value.sortedBy { item -> item.room }.toPersistentList(),
            )
        }
        .sortedWith(compareBy({ slot -> slot.day }, { slot -> slot.startsAt }, { slot -> slot.endsAt }))
        .toPersistentList()

internal fun FavoritesListSectionUiState.Companion.fake(): FavoritesListSectionUiState {
    val timetable = Timetable.fake()
    return FavoritesListSectionUiState(
        timeSlots = timetable.items.filter { timetable.isFavorite(it.id) }.toTimeSlots(),
        dayHeadersVisible = true,
    )
}

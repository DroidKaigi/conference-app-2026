package io.github.droidkaigi.confsched.feature.favorites.component

import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.ui.TimetableTimeSlot
import io.github.droidkaigi.confsched.core.ui.toTimetableTimeSlots
import kotlinx.collections.immutable.PersistentList
import kotlin.time.Instant

data class FavoritesListSectionUiState(
    val timeSlots: PersistentList<TimetableTimeSlot>,
    val dayHeadersVisible: Boolean,
) {
    companion object
}

internal fun FavoritesListSectionUiState.Companion.fake(
    currentTime: Instant = Instant.parse("2026-09-02T12:00:00Z"),
): FavoritesListSectionUiState {
    val timetable = Timetable.fake()
    return FavoritesListSectionUiState(
        timeSlots = timetable.items
            .filter { timetable.isFavorite(it.id) }
            .toTimetableTimeSlots(currentTime),
        dayHeadersVisible = true,
    )
}

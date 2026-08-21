package io.github.droidkaigi.confsched.feature.search.component

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentList

sealed interface SearchResultUiState {

    /** The states with no list to show. One block stands in for a list in both of them. */
    sealed interface Empty : SearchResultUiState {

        /** Nothing searched on yet. */
        data object Initial : Empty

        /** A search is in effect and no session answers it. */
        data object NoMatch : Empty
    }

    /**
     * The sessions a search found, under the slot each runs in.
     *
     * A search reaches both days, so a slot is a day and a time rather than a time alone.
     */
    data class Found(
        val matchCount: Int,
        val timeSlots: PersistentList<TimeSlot>,
        val bookmarks: PersistentSet<TimetableItemId>,
        /** The word typed, so a card can mark where it matched. A filter alone marks nothing. */
        val titleMark: String,
    ) : SearchResultUiState {
        data class TimeSlot(
            val day: DroidKaigi2026Day,
            val startsAt: String,
            val endsAt: String,
            val items: PersistentList<TimetableItem>,
        )
    }
}

internal fun List<TimetableItem>.toSearchTimeSlots(): PersistentList<SearchResultUiState.Found.TimeSlot> =
    groupBy { item -> Triple(item.day, item.startsAt, item.endsAt) }
        .map { entry ->
            SearchResultUiState.Found.TimeSlot(
                day = entry.key.first,
                startsAt = entry.key.second,
                endsAt = entry.key.third,
                items = entry.value.sortedBy { item -> item.room }.toPersistentList(),
            )
        }
        .sortedWith(compareBy({ slot -> slot.day }, { slot -> slot.startsAt }, { slot -> slot.endsAt }))
        .toPersistentList()

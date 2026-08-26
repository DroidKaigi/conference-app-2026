package io.github.droidkaigi.confsched.feature.search.component

import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.ui.TimetableTimeSlot
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet

sealed interface SearchResultUiState {
    sealed interface Empty : SearchResultUiState {
        data object Initial : Empty

        data object NoMatch : Empty
    }

    data class Found(
        val timeSlots: PersistentList<TimetableTimeSlot>,
        val bookmarks: PersistentSet<TimetableItemId>,
        val titleMark: String,
        val dayHeadersVisible: Boolean,
    ) : SearchResultUiState {
        val matchCount = timeSlots.sumOf { it.items.size }
    }
}

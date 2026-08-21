package io.github.droidkaigi.confsched.feature.search.component

import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet

sealed interface SearchResultUiState {

    /** The states with no list to show. One block stands in for a list in both of them. */
    sealed interface Empty : SearchResultUiState {

        /** Nothing searched on yet. */
        data object Initial : Empty

        /** A search is in effect and no session answers it. */
        data object NoMatch : Empty
    }

    /**
     * The sessions a search found, flat rather than grouped by time: a search reaches both days,
     * so one time slot would hold sessions running on different ones.
     */
    data class Found(
        val items: PersistentList<TimetableItem>,
        val bookmarks: PersistentSet<TimetableItemId>,
    ) : SearchResultUiState
}

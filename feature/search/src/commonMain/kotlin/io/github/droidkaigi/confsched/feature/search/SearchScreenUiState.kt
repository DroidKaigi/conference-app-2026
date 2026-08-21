package io.github.droidkaigi.confsched.feature.search

import io.github.droidkaigi.confsched.core.model.SessionSearchQuery
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.feature.search.component.SearchFilterRowUiState
import io.github.droidkaigi.confsched.feature.search.component.SearchResultUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

data class SearchScreenUiState(
    val query: String,
    val filterRow: SearchFilterRowUiState,
    val result: SearchResultUiState,
) {
    companion object
}

internal fun SearchScreenUiState.Companion.fake(): SearchScreenUiState {
    val timetable = Timetable.fake()
    val query = SessionSearchQuery(text = "Session")
    return SearchScreenUiState(
        query = query.text,
        filterRow = SearchFilterRowUiState(
            selectedDay = null,
            categories = timetable.categories,
            selectedCategoryIds = persistentSetOf(),
            sessionTypes = persistentListOf(),
            selectedSessionTypes = persistentSetOf(),
            selectedLanguages = persistentSetOf(),
        ),
        result = SearchResultUiState.Found(
            items = timetable.search(query),
            bookmarks = timetable.bookmarks,
        ),
    )
}

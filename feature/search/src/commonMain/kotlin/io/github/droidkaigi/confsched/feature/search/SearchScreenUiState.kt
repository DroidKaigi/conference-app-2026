package io.github.droidkaigi.confsched.feature.search

import io.github.droidkaigi.confsched.core.model.SessionSearchQuery
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.ui.toTimetableTimeSlots
import io.github.droidkaigi.confsched.feature.search.component.SearchFilterRowUiState
import io.github.droidkaigi.confsched.feature.search.component.SearchResultUiState
import kotlinx.collections.immutable.persistentSetOf
import kotlin.time.Instant

data class SearchScreenUiState(
    val queryText: String,
    val hasActiveFilters: Boolean,
    val filterRow: SearchFilterRowUiState,
    val result: SearchResultUiState,
) {
    companion object
}

internal fun SearchScreenUiState.Companion.fake(
    currentTime: Instant = Instant.parse("2026-09-02T12:00:00Z"),
): SearchScreenUiState {
    val timetable = Timetable.fake()
    val query = SessionSearchQuery(text = "Session")
    val matches = timetable.search(query)
    return SearchScreenUiState(
        queryText = query.text,
        hasActiveFilters = query.hasActiveFilters,
        filterRow = SearchFilterRowUiState(
            selectedDay = null,
            categories = timetable.categories,
            selectedCategoryIds = persistentSetOf(),
            sessionTypes = timetable.sessionTypes,
            selectedSessionTypes = persistentSetOf(),
            selectedLanguages = persistentSetOf(),
        ),
        result = SearchResultUiState.Found(
            timeSlots = matches.toTimetableTimeSlots(currentTime),
            bookmarks = timetable.bookmarks,
            titleMark = query.normalizedText,
            dayHeadersVisible = true,
        ),
    )
}

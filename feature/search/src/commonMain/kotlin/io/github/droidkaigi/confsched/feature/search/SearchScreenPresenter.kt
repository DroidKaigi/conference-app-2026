package io.github.droidkaigi.confsched.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.MutationSuccessEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.rememberCurrentTime
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.SessionSearchQuery
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.ui.toTimetableTimeSlots
import io.github.droidkaigi.confsched.feature.search.component.SearchFilterRowUiState
import io.github.droidkaigi.confsched.feature.search.component.SearchResultUiState
import kotlinx.collections.immutable.persistentListOf
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: SearchPresenterContext)
fun searchScreenPresenter(
    screenChannel: ScreenChannel<SearchScreenAction, SearchScreenActionResult>,
    timetable: Timetable,
): SearchScreenUiState {
    val favoriteMutation = rememberMutation(presenterContext.favoriteTimetableItemIdMutationKey)
    // The mutation reports only the direction of the toggle, so the session it applied to is kept here.
    var toggledFavoriteId by retain { mutableStateOf<TimetableItemId?>(null) }
    var query by retain { mutableStateOf(SessionSearchQuery()) }
    val currentTime = presenterContext.clock.rememberCurrentTime()

    ActionEffect(screenChannel) { action ->
        when (action) {
            is SearchScreenAction.ToggleBookmark -> {
                toggledFavoriteId = action.id
                favoriteMutation.mutateAsync(action.id)
            }

            is SearchScreenAction.ChangeQueryText -> query = query.copy(text = action.text)

            is SearchScreenAction.ToggleDay -> query = query.toggleDay(action.day)

            is SearchScreenAction.ToggleCategory -> query = query.toggleCategory(action.categoryId)

            is SearchScreenAction.ToggleSessionType -> query = query.toggleSessionType(action.sessionType)

            is SearchScreenAction.ToggleLanguage -> query = query.toggleLanguage(action.language)

            SearchScreenAction.ClearFilters -> query = query.clearFilters()
        }
    }

    MutationErrorEffect(favoriteMutation) { error ->
        screenChannel.emit(SearchScreenActionResult.ShowMessage(error.toUserMessage()))
        favoriteMutation.reset()
    }

    MutationSuccessEffect(favoriteMutation) { added ->
        val addedId = toggledFavoriteId.takeIf { added }
        if (addedId != null) {
            screenChannel.emit(SearchScreenActionResult.FavoriteAdded(timetable.roomOf(addedId)))
        }
        favoriteMutation.reset()
    }

    val timeSlots = remember(timetable.items, query, currentTime) {
        if (query.isEmpty) {
            persistentListOf()
        } else {
            timetable.search(query).toTimetableTimeSlots(currentTime)
        }
    }

    return SearchScreenUiState(
        queryText = query.text,
        hasActiveFilters = query.hasActiveFilters,
        filterRow = SearchFilterRowUiState(
            selectedDay = query.day,
            categories = timetable.categories,
            selectedCategoryIds = query.categoryIds,
            sessionTypes = timetable.sessionTypes,
            selectedSessionTypes = query.sessionTypes,
            selectedLanguages = query.languages,
        ),
        result = when {
            query.isEmpty -> SearchResultUiState.Empty.Initial

            timeSlots.isEmpty() -> SearchResultUiState.Empty.NoMatch

            else -> SearchResultUiState.Found(
                timeSlots = timeSlots,
                bookmarks = timetable.bookmarks,
                titleMark = query.normalizedText,
                dayHeadersVisible = query.day == null,
            )
        },
    )
}

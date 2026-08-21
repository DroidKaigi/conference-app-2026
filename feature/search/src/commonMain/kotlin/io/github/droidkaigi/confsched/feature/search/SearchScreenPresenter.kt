package io.github.droidkaigi.confsched.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.SessionSearchQuery
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.feature.search.component.SearchFilterRowUiState
import io.github.droidkaigi.confsched.feature.search.component.SearchResultUiState
import kotlinx.collections.immutable.toPersistentList
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: SearchPresenterContext)
fun searchScreenPresenter(
    screenChannel: ScreenChannel<SearchScreenAction, SearchScreenActionResult>,
    timetable: Timetable,
): SearchScreenUiState {
    val favoriteMutation = rememberMutation(presenterContext.favoriteTimetableItemIdMutationKey)
    var query by retain { mutableStateOf(SessionSearchQuery()) }

    ActionEffect(screenChannel) { action ->
        when (action) {
            is SearchScreenAction.Bookmark -> favoriteMutation.mutateAsync(action.id)
            is SearchScreenAction.ChangeQueryText -> query = query.copy(text = action.text)
            is SearchScreenAction.SelectDay -> query = query.copy(day = action.day)
            is SearchScreenAction.ToggleCategory -> query = query.toggleCategory(action.id)
            is SearchScreenAction.ToggleSessionType -> query = query.toggleSessionType(action.sessionType)
            is SearchScreenAction.ToggleLanguage -> query = query.toggleLanguage(action.language)
        }
    }

    MutationErrorEffect(favoriteMutation) { error ->
        screenChannel.emit(SearchScreenActionResult.ShowMessage(error.toUserMessage()))
        favoriteMutation.reset()
    }

    // Offering a session type no session has would filter every result away, so the row is built
    // from what the timetable holds rather than from the enum.
    val sessionTypes = remember(timetable.items) {
        timetable.items.map { it.sessionType }.distinct().sortedBy { it.ordinal }.toPersistentList()
    }
    val matches = timetable.search(query)

    return SearchScreenUiState(
        query = query.text,
        filterRow = SearchFilterRowUiState(
            selectedDay = query.day,
            categories = timetable.categories,
            selectedCategoryIds = query.categoryIds,
            sessionTypes = sessionTypes,
            selectedSessionTypes = query.sessionTypes,
            selectedLanguages = query.languages,
        ),
        result = when {
            query.isEmpty -> SearchResultUiState.Empty.Initial
            matches.isEmpty() -> SearchResultUiState.Empty.NoMatch
            else -> SearchResultUiState.Found(items = matches, bookmarks = timetable.bookmarks)
        },
    )
}

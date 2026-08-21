package io.github.droidkaigi.confsched.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.SessionSearchQuery
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.feature.search.component.SearchResultUiState
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
        }
    }

    MutationErrorEffect(favoriteMutation) { error ->
        screenChannel.emit(SearchScreenActionResult.ShowMessage(error.toUserMessage()))
        favoriteMutation.reset()
    }

    val matches = timetable.search(query)

    return SearchScreenUiState(
        query = query.text,
        result = when {
            query.isEmpty -> SearchResultUiState.Empty.Initial
            matches.isEmpty() -> SearchResultUiState.Empty.NoMatch
            else -> SearchResultUiState.Found(items = matches, bookmarks = timetable.bookmarks)
        },
    )
}

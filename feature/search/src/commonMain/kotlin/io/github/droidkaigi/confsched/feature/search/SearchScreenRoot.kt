package io.github.droidkaigi.confsched.feature.search

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import soil.query.compose.rememberQuery
import soil.query.compose.rememberSubscription

@Composable
context(screenContext: SearchScreenContext)
fun SearchScreenRoot(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (TimetableItemId) -> Unit,
) {
    SoilDataBoundary(
        state1 = rememberQuery(screenContext.timetableQueryKey),
        state2 = rememberSubscription(screenContext.favoriteTimetableIdsSubscriptionKey),
    ) { timetable, favoriteIds ->
        val screenChannel = retainScreenChannel<SearchScreenAction, SearchScreenActionResult>()

        val snackbarHostState = LocalSnackbarHostState.current

        ActionResultEffect(screenChannel) { result ->
            when (result) {
                is SearchScreenActionResult.ShowMessage -> snackbarHostState.showSnackbar(result.message.text)
            }
        }

        val uiState = context(screenContext.presenterContext) {
            searchScreenPresenter(
                screenChannel = screenChannel,
                timetable = timetable.copy(bookmarks = favoriteIds),
            )
        }

        SearchScreen(
            uiState = uiState,
            onQueryChange = { screenChannel.send(SearchScreenAction.ChangeQueryText(it)) },
            onBookmarkClick = { screenChannel.send(SearchScreenAction.Bookmark(it)) },
            onItemClick = onNavigateToDetail,
            onBackClick = onNavigateBack,
        )
    }
}

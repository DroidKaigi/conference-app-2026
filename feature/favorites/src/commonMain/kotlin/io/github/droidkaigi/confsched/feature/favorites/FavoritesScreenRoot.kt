package io.github.droidkaigi.confsched.feature.favorites

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import io.github.droidkaigi.confsched.core.ui.showSnackbar
import soil.query.compose.rememberQuery
import soil.query.compose.rememberSubscription

@Composable
context(screenContext: FavoritesScreenContext)
fun FavoritesScreenRoot(
    onNavigateToDetail: (TimetableItemId) -> Unit,
    onFavoriteAdded: suspend (SessionRoom) -> Unit,
) {
    SoilDataBoundary(
        state1 = rememberQuery(screenContext.timetableQueryKey),
        state2 = rememberSubscription(screenContext.favoriteTimetableIdsSubscriptionKey),
    ) { timetable, favoriteIds ->
        val screenChannel = retainScreenChannel<FavoritesScreenAction, FavoritesScreenActionResult>()

        val snackbarHostState = LocalSnackbarHostState.current

        ActionResultEffect(screenChannel) { result ->
            when (result) {
                is FavoritesScreenActionResult.ShowMessage -> snackbarHostState.showSnackbar(result.message)
                is FavoritesScreenActionResult.FavoriteAdded -> onFavoriteAdded(result.room)
            }
        }

        val uiState = context(screenContext.presenterContext) {
            favoritesScreenPresenter(
                screenChannel = screenChannel,
                timetable = timetable.copy(bookmarks = favoriteIds),
            )
        }

        FavoritesScreen(
            uiState = uiState,
            onBookmarkClick = { screenChannel.send(FavoritesScreenAction.Bookmark(it)) },
            onDayFilterClick = { screenChannel.send(FavoritesScreenAction.SelectDayFilter(it)) },
            onItemClick = onNavigateToDetail,
        )
    }
}

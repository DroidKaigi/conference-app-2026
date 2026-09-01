package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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
context(screenContext: TimetableScreenContext)
fun TimetableScreenRoot(
    onNavigateToDetail: (TimetableItemId) -> Unit,
    onNavigateToSearch: () -> Unit,
    onFavoriteAdded: (SessionRoom) -> Unit,
) {
    SoilDataBoundary(
        state1 = rememberQuery(screenContext.timetableQueryKey),
        state2 = rememberSubscription(screenContext.favoriteTimetableIdsSubscriptionKey),
    ) { timetable, favoriteIds ->
        val screenChannel = retainScreenChannel<TimetableScreenAction, TimetableScreenActionResult>()

        val snackbarHostState = LocalSnackbarHostState.current
        val hapticFeedback = LocalHapticFeedback.current

        ActionResultEffect(screenChannel) { result ->
            when (result) {
                is TimetableScreenActionResult.ShowMessage -> snackbarHostState.showSnackbar(result.message)

                is TimetableScreenActionResult.FavoriteAdded -> {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    onFavoriteAdded(result.room)
                }
            }
        }

        val uiState = context(screenContext.presenterContext) {
            timetableScreenPresenter(
                screenChannel = screenChannel,
                timetable = timetable.copy(bookmarks = favoriteIds),
            )
        }

        TimetableScreen(
            uiState = uiState,
            onBookmarkClick = { screenChannel.send(TimetableScreenAction.Bookmark(it)) },
            onDayClick = { screenChannel.send(TimetableScreenAction.SelectDay(it)) },
            onItemClick = onNavigateToDetail,
            onSearchClick = onNavigateToSearch,
            onUiTypeChangeClick = { screenChannel.send(TimetableScreenAction.SwitchToGridView) },
        )
    }
}

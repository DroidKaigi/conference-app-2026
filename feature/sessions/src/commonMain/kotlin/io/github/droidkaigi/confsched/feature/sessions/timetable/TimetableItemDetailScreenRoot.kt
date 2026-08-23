package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import soil.query.compose.rememberQuery
import soil.query.compose.rememberSubscription

@Composable
context(screenContext: TimetableItemDetailScreenContext)
fun TimetableItemDetailScreenRoot(
    onNavigateBack: () -> Unit,
) {
    SoilDataBoundary(
        state1 = rememberQuery(
            key = screenContext.timetableQueryKey,
            select = { timetable -> timetable.items.first { it.id == screenContext.timetableItemId } },
        ),
        state2 = rememberSubscription(screenContext.favoriteTimetableIdsSubscriptionKey),
    ) { item, favoriteIds ->
        val screenChannel =
            retainScreenChannel<TimetableItemDetailScreenAction, TimetableItemDetailScreenActionResult>()

        val snackbarHostState = LocalSnackbarHostState.current
        ActionResultEffect(screenChannel) { result ->
            when (result) {
                is TimetableItemDetailScreenActionResult.ShowMessage -> snackbarHostState.showSnackbar(result.message.text)
            }
        }

        val uiState = context(screenContext.presenterContext) {
            timetableItemDetailScreenPresenter(
                screenChannel = screenChannel,
                item = item,
                isFavorite = screenContext.timetableItemId in favoriteIds,
            )
        }
        TimetableItemDetailScreen(
            uiState = uiState,
            onBookmarkClick = {
                screenChannel.send(TimetableItemDetailScreenAction.ToggleBookmark(uiState.item.id))
            },
            onBackClick = onNavigateBack,
        )
    }
}

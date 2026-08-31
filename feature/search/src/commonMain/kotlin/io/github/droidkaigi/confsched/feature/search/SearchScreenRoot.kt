package io.github.droidkaigi.confsched.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import io.github.droidkaigi.confsched.core.ui.showSnackbar
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
        val hapticFeedback = LocalHapticFeedback.current

        ActionResultEffect(screenChannel) { result ->
            when (result) {
                is SearchScreenActionResult.ShowMessage -> snackbarHostState.showSnackbar(result.message)
                SearchScreenActionResult.FavoriteAdded -> hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
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
            onQueryTextChange = { screenChannel.send(SearchScreenAction.ChangeQueryText(it)) },
            onDayClick = { screenChannel.send(SearchScreenAction.ToggleDay(it)) },
            onCategoryClick = { screenChannel.send(SearchScreenAction.ToggleCategory(it)) },
            onSessionTypeClick = { screenChannel.send(SearchScreenAction.ToggleSessionType(it)) },
            onLanguageClick = { screenChannel.send(SearchScreenAction.ToggleLanguage(it)) },
            onClearFiltersClick = { screenChannel.send(SearchScreenAction.ClearFilters) },
            onBookmarkClick = { screenChannel.send(SearchScreenAction.ToggleBookmark(it)) },
            onItemClick = onNavigateToDetail,
            onBackClick = onNavigateBack,
        )
    }
}

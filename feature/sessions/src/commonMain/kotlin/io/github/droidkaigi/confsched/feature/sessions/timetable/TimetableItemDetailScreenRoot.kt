package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.sessionUrl
import io.github.droidkaigi.confsched.core.ui.CalendarEvent
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import io.github.droidkaigi.confsched.core.ui.currentDisplayLanguage
import io.github.droidkaigi.confsched.core.ui.showSnackbar
import soil.query.compose.rememberQuery
import soil.query.compose.rememberSubscription

@Composable
context(screenContext: TimetableItemDetailScreenContext)
fun TimetableItemDetailScreenRoot(
    onNavigateBack: () -> Unit,
    onNavigateToSession: (TimetableItemId) -> Unit,
    onOpenUrl: (String) -> Unit,
    onAddCalendarEvent: (CalendarEvent) -> Unit,
    onShareText: (String) -> Unit,
) {
    SoilDataBoundary(
        state1 = rememberQuery(
            key = screenContext.timetableQueryKey,
            select = { timetable -> timetable.detailOf(screenContext.timetableItemId) },
        ),
        state2 = rememberSubscription(screenContext.favoriteTimetableIdsSubscriptionKey),
        state3 = rememberSubscription(screenContext.sessionMemosSubscriptionKey),
    ) { detail, favoriteIds, memos ->
        val screenChannel =
            retainScreenChannel<TimetableItemDetailScreenAction, TimetableItemDetailScreenActionResult>()

        val snackbarHostState = LocalSnackbarHostState.current
        ActionResultEffect(screenChannel) { result ->
            when (result) {
                is TimetableItemDetailScreenActionResult.ShowMessage -> snackbarHostState.showSnackbar(result.message)
            }
        }

        val uiState = context(screenContext.presenterContext) {
            timetableItemDetailScreenPresenter(
                screenChannel = screenChannel,
                detail = detail,
                favoriteIds = favoriteIds,
                memo = memos[screenContext.timetableItemId].orEmpty(),
                initialDisplayLanguage = currentDisplayLanguage(),
            )
        }
        val shareText = "${uiState.item.title.of(uiState.displayLanguage)}\n${sessionUrl(uiState.item.id)}\n$CONFERENCE_HASHTAG"
        TimetableItemDetailScreen(
            uiState = uiState,
            onBookmarkClick = { screenChannel.send(TimetableItemDetailScreenAction.Bookmark(it)) },
            onDescriptionExpansionToggleClick = { screenChannel.send(TimetableItemDetailScreenAction.ToggleDescriptionExpansion) },
            onDisplayLanguageToggleClick = { screenChannel.send(TimetableItemDetailScreenAction.ToggleDisplayLanguage) },
            onMemoChange = { screenChannel.send(TimetableItemDetailScreenAction.SaveMemo(it)) },
            onArchiveVideoClick = onOpenUrl,
            onArchiveSlideClick = onOpenUrl,
            onCalendarClick = { onAddCalendarEvent(uiState.item.toCalendarEvent(uiState.displayLanguage)) },
            onShareClick = { onShareText(shareText) },
            onSessionClick = onNavigateToSession,
            onBackClick = onNavigateBack,
        )
    }
}

private const val CONFERENCE_HASHTAG = "#DroidKaigi"

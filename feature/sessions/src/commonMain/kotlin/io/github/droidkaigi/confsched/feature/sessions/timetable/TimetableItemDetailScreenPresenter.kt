package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.DisplayLanguage
import io.github.droidkaigi.confsched.core.model.SessionMemoEdit
import io.github.droidkaigi.confsched.core.model.TimetableItemDetail
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.collections.immutable.PersistentSet
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: TimetableItemDetailPresenterContext)
fun timetableItemDetailScreenPresenter(
    screenChannel: ScreenChannel<TimetableItemDetailScreenAction, TimetableItemDetailScreenActionResult>,
    detail: TimetableItemDetail,
    favoriteIds: PersistentSet<TimetableItemId>,
    memo: String,
    initialDisplayLanguage: DisplayLanguage,
): TimetableItemDetailScreenUiState {
    val favoriteMutation = rememberMutation(presenterContext.favoriteTimetableItemIdMutationKey)
    val memoMutation = rememberMutation(presenterContext.sessionMemoMutationKey)
    var isDescriptionExpanded by retain { mutableStateOf(false) }
    var displayLanguage by retain { mutableStateOf(initialDisplayLanguage) }

    ActionEffect(screenChannel) { action ->
        when (action) {
            is TimetableItemDetailScreenAction.Bookmark -> favoriteMutation.mutateAsync(action.id)
            is TimetableItemDetailScreenAction.SaveMemo -> memoMutation.mutateAsync(SessionMemoEdit(detail.item.id, action.text))
            TimetableItemDetailScreenAction.ToggleDescription -> isDescriptionExpanded = !isDescriptionExpanded
            TimetableItemDetailScreenAction.ToggleDisplayLanguage -> displayLanguage = displayLanguage.toggled()
        }
    }

    MutationErrorEffect(favoriteMutation) { error ->
        screenChannel.emit(TimetableItemDetailScreenActionResult.ShowMessage(error.toUserMessage()))
        favoriteMutation.reset()
    }

    MutationErrorEffect(memoMutation) { error ->
        screenChannel.emit(TimetableItemDetailScreenActionResult.ShowMessage(error.toUserMessage()))
        memoMutation.reset()
    }

    return TimetableItemDetailScreenUiState(
        item = detail.item,
        sameSlotItems = detail.sameSlotItems,
        bookmarks = favoriteIds,
        memo = memo,
        isDescriptionExpanded = isDescriptionExpanded,
        displayLanguage = displayLanguage,
    )
}

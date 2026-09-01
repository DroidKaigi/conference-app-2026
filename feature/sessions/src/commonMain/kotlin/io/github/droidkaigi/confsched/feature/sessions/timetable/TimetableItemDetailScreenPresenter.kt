package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.MutationSuccessEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.DisplayLanguage
import io.github.droidkaigi.confsched.core.model.SessionMemoEdit
import io.github.droidkaigi.confsched.core.model.TimetableItemDetail
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailScreenUiState.DescriptionDisplay
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentList
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
    var descriptionDisplay by retain { mutableStateOf<DescriptionDisplay>(DescriptionDisplay.Unmeasured) }
    var displayLanguage by retain { mutableStateOf(initialDisplayLanguage) }

    ActionEffect(screenChannel) { action ->
        when (action) {
            is TimetableItemDetailScreenAction.Bookmark -> favoriteMutation.mutateAsync(action.id)

            is TimetableItemDetailScreenAction.SaveMemo -> memoMutation.mutateAsync(SessionMemoEdit(detail.item.id, action.text))

            is TimetableItemDetailScreenAction.UpdateDescriptionTruncation -> {
                // Expanding lifts the line limit, so a layout measured while expanded always
                // reports no overflow and carries no verdict about the collapsed text.
                if (descriptionDisplay != DescriptionDisplay.Truncatable.Expanded) {
                    descriptionDisplay = if (action.isTruncated) {
                        DescriptionDisplay.Truncatable.Collapsed
                    } else {
                        DescriptionDisplay.NotTruncatable
                    }
                }
            }

            TimetableItemDetailScreenAction.ToggleDescriptionExpansion -> {
                (descriptionDisplay as? DescriptionDisplay.Truncatable)
                    ?.let { descriptionDisplay = it.toggled() }
            }

            TimetableItemDetailScreenAction.ToggleDisplayLanguage -> displayLanguage = displayLanguage.toggled()
        }
    }

    MutationErrorEffect(favoriteMutation) { error ->
        screenChannel.emit(TimetableItemDetailScreenActionResult.ShowMessage(error.toUserMessage()))
        favoriteMutation.reset()
    }

    MutationSuccessEffect(favoriteMutation) { added ->
        if (added) {
            screenChannel.emit(TimetableItemDetailScreenActionResult.FavoriteAdded)
        }
        favoriteMutation.reset()
    }

    MutationErrorEffect(memoMutation) { error ->
        screenChannel.emit(TimetableItemDetailScreenActionResult.ShowMessage(error.toUserMessage()))
        memoMutation.reset()
    }

    return TimetableItemDetailScreenUiState(
        item = detail.item,
        isFavorite = detail.item.id in favoriteIds,
        sameSlotItems = detail.sameSlotItems
            .map { TimetableItemDetailScreenUiState.SameSlotItem(item = it, isFavorite = it.id in favoriteIds) }
            .toPersistentList(),
        memo = memo,
        descriptionDisplay = descriptionDisplay,
        displayLanguage = displayLanguage,
    )
}

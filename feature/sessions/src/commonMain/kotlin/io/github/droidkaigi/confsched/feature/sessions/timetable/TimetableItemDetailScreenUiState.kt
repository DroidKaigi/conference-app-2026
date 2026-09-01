package io.github.droidkaigi.confsched.feature.sessions.timetable

import io.github.droidkaigi.confsched.core.model.DisplayLanguage
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemDetail
import io.github.droidkaigi.confsched.core.preview.fake
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

data class TimetableItemDetailScreenUiState(
    val item: TimetableItem,
    val isFavorite: Boolean,
    val sameSlotItems: PersistentList<SameSlotItem>,
    val memo: String,
    val descriptionDisplay: DescriptionDisplay,
    val displayLanguage: DisplayLanguage,
) {
    data class SameSlotItem(
        val item: TimetableItem,
        val isFavorite: Boolean,
    )

    sealed interface DescriptionDisplay {
        data object Unmeasured : DescriptionDisplay

        data object NotTruncatable : DescriptionDisplay

        sealed interface Truncatable : DescriptionDisplay {
            data object Collapsed : Truncatable

            data object Expanded : Truncatable

            fun toggled(): Truncatable = when (this) {
                Collapsed -> Expanded
                Expanded -> Collapsed
            }
        }
    }

    companion object
}

internal fun TimetableItemDetailScreenUiState.Companion.fake(
    isCancelled: Boolean,
    message: MultiLangText?,
    displayLanguage: DisplayLanguage,
): TimetableItemDetailScreenUiState {
    val detail = TimetableItemDetail.fake()
    return TimetableItemDetailScreenUiState(
        item = detail.item.copy(isCancelled = isCancelled, message = message),
        isFavorite = true,
        sameSlotItems = detail.sameSlotItems
            .map { TimetableItemDetailScreenUiState.SameSlotItem(item = it, isFavorite = false) }
            .toPersistentList(),
        memo = "",
        descriptionDisplay = TimetableItemDetailScreenUiState.DescriptionDisplay.Truncatable.Collapsed,
        displayLanguage = displayLanguage,
    )
}

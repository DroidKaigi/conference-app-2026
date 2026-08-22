package io.github.droidkaigi.confsched.feature.sessions.timetable

import io.github.droidkaigi.confsched.core.model.DisplayLanguage
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemDetail
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.fake
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

data class TimetableItemDetailScreenUiState(
    val item: TimetableItem,
    val sameSlotItems: PersistentList<TimetableItem>,
    val bookmarks: PersistentSet<TimetableItemId>,
    val memo: String,
    val isDescriptionExpanded: Boolean,
    val displayLanguage: DisplayLanguage,
) {
    val isFavorite: Boolean get() = item.id in bookmarks

    companion object
}

internal fun TimetableItemDetailScreenUiState.Companion.fake(
    isCancelled: Boolean,
    displayLanguage: DisplayLanguage,
): TimetableItemDetailScreenUiState {
    val detail = TimetableItemDetail.fake()
    return TimetableItemDetailScreenUiState(
        item = detail.item.copy(isCancelled = isCancelled),
        sameSlotItems = detail.sameSlotItems,
        bookmarks = persistentSetOf(detail.item.id),
        memo = "",
        isDescriptionExpanded = false,
        displayLanguage = displayLanguage,
    )
}

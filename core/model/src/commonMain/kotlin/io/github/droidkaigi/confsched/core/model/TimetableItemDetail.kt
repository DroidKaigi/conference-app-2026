package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.PersistentList

data class TimetableItemDetail(
    val item: TimetableItem,
    val sameSlotItems: PersistentList<TimetableItem>,
) {
    companion object
}

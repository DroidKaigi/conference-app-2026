package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList

data class Timetable(
    val items: PersistentList<TimetableItem>,
    val bookmarks: PersistentSet<TimetableItemId> = persistentSetOf(),
    val categories: PersistentList<SessionCategory> = persistentListOf(),
) {
    val sessionTypes = items
        .map { it.sessionType }
        .distinct()
        .sortedBy { it.ordinal }
        .toPersistentList()

    fun itemsOn(day: DroidKaigi2026Day): PersistentList<TimetableItem> =
        items.filter { it.day == day }.toPersistentList()

    fun isFavorite(id: TimetableItemId): Boolean = id in bookmarks

    fun detailOf(id: TimetableItemId): TimetableItemDetail {
        val item = items.first { it.id == id }
        return TimetableItemDetail(
            item = item,
            sameSlotItems = items
                .filter { it.id != id && it.day == item.day && it.startsAt == item.startsAt }
                .toPersistentList(),
        )
    }

    companion object
}

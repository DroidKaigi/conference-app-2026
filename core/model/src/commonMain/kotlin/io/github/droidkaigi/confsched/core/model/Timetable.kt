package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class TimetableItemId(val value: String)

data class TimetableItem(
    val id: TimetableItemId,
    val title: MultiLangText,
    val room: Room,
    val speaker: String,
    val language: Language,
    val day: DroidKaigi2026Day,
    val startsAt: String,
    val endsAt: String,
    val sessionType: SessionType,
    // The API leaves sessionCategoryItemId out, and names a category this app cannot resolve, so a
    // session reaches the timetable whether or not it has one.
    val category: SessionCategory? = null,
) {
    companion object
}

data class Timetable(
    val items: PersistentList<TimetableItem>,
    val bookmarks: PersistentSet<TimetableItemId> = persistentSetOf(),
    /** The categories a filter offers: those the payload names, in the order it sorts them. */
    val categories: PersistentList<SessionCategory> = persistentListOf(),
) {
    fun itemsOn(day: DroidKaigi2026Day): PersistentList<TimetableItem> =
        items.filter { it.day == day }.toPersistentList()

    fun isFavorite(id: TimetableItemId): Boolean = id in bookmarks

    companion object
}

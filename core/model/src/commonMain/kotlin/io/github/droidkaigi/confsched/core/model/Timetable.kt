package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class TimetableItemId(val value: String)

/** Someone giving a session, and the picture the timetable shows them by. */
data class Speaker(
    val name: String,
    val iconUrl: String?,
)

data class TimetableItem(
    val id: TimetableItemId,
    val title: MultiLangText,
    val room: Room,
    val speakers: PersistentList<Speaker>,
    val language: Language,
    val day: DroidKaigi2026Day,
    val startsAt: String,
    val endsAt: String,
) {
    companion object
}

data class Timetable(
    val items: PersistentList<TimetableItem>,
    val bookmarks: PersistentSet<TimetableItemId> = persistentSetOf(),
) {
    fun itemsOn(day: DroidKaigi2026Day): PersistentList<TimetableItem> =
        items.filter { it.day == day }.toPersistentList()

    fun isFavorite(id: TimetableItemId): Boolean = id in bookmarks

    companion object
}

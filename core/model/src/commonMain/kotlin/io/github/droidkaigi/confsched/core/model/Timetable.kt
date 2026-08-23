package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.time.Instant

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
    val startsAtInstant: Instant,
    val endsAtInstant: Instant,
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

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

@Serializable
enum class DroidKaigi2026Day { Day1, Day2 }

@Serializable
data class TimetableItem(
    val id: TimetableItemId,
    val title: String,
    val room: String,
    val speaker: String,
    val day: DroidKaigi2026Day,
    val startsAt: String,
    val endsAt: String,
)

@Serializable
data class Timetable(
    val items: PersistentList<TimetableItem>,
    val bookmarks: PersistentSet<TimetableItemId> = persistentSetOf(),
    val rawResponse: String = "",
) {
    fun itemsOn(day: DroidKaigi2026Day): PersistentList<TimetableItem> =
        items.filter { it.day == day }.toPersistentList()

    fun isFavorite(id: TimetableItemId): Boolean = id in bookmarks
}

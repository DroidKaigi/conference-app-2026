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

@Serializable
@JvmInline
value class TimetableSpeakerId(val value: String)

data class TimetableSpeaker(
    val id: TimetableSpeakerId,
    val name: String,
    val tagLine: String,
    val iconUrl: String?,
)

data class TimetableItemAsset(
    val videoUrl: String?,
    val slideUrl: String?,
) {
    val isEmpty: Boolean get() = videoUrl == null && slideUrl == null

    companion object {
        val Empty = TimetableItemAsset(videoUrl = null, slideUrl = null)
    }
}

data class TimetableItem(
    val id: TimetableItemId,
    val title: MultiLangText,
    val room: Room,
    val speakers: PersistentList<TimetableSpeaker>,
    val language: Language,
    val day: DroidKaigi2026Day,
    val startsAt: String,
    val endsAt: String,
    val startsAtInstant: Instant,
    val endsAtInstant: Instant,
    val description: MultiLangText,
    val targetAudience: MultiLangText,
    val category: MultiLangText?,
    val asset: TimetableItemAsset,
    val hasInterpretation: Boolean,
    val isCancelled: Boolean,
) {
    val speakerNames: String get() = speakers.joinToString(", ") { it.name }

    companion object
}

data class TimetableItemDetail(
    val item: TimetableItem,
    val sameSlotItems: PersistentList<TimetableItem>,
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

fun sessionUrl(id: TimetableItemId): String = "https://2026.droidkaigi.jp/timetable/${id.value}"

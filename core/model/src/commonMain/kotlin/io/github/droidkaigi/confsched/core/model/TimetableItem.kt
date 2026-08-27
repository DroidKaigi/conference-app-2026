package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.time.Instant

@Serializable
@JvmInline
value class TimetableItemId(val value: String)

data class TimetableItem(
    val id: TimetableItemId,
    val title: MultiLangText,
    val room: SessionRoom,
    val speakers: PersistentList<TimetableSpeaker>,
    val language: Language,
    val day: DroidKaigi2026Day,
    val startsAt: String,
    val endsAt: String,
    val sessionType: SessionType,
    val startsAtInstant: Instant,
    val endsAtInstant: Instant,
    val description: MultiLangText,
    val targetAudience: MultiLangText,
    val category: SessionCategory?,
    val asset: TimetableItemAsset,
    val hasInterpretation: Boolean,
    val isCancelled: Boolean,
    val message: MultiLangText?,
) {
    companion object
}

// The site redirects the slashless form, so the canonical one is what a share carries.
fun sessionUrl(id: TimetableItemId): String = "https://2026.droidkaigi.jp/timetable/${id.value}/"

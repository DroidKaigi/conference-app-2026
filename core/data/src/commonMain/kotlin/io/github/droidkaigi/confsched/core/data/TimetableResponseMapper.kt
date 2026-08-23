package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemAsset
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableSpeaker
import io.github.droidkaigi.confsched.core.model.TimetableSpeakerId
import kotlinx.collections.immutable.toPersistentList
import kotlin.time.Instant

fun TimetableResponse.toTimetableItems(): List<TimetableItem> {
    // Room is named in English, so that is the side Room.of matches.
    val roomNameById = rooms.associateBy({ it.id }, { it.name.toMultiLangText().en })
    val speakerById = speakers.associateBy { it.id }
    val categoryNameById = categories
        .flatMap { it.items }
        .associateBy({ it.id }, { it.name.toMultiLangText() })
    // Conference days are not encoded in the payload; the two distinct dates map to Day1/Day2.
    val dayByDate = sessions.map { it.startsAt.date() }.distinct().sorted()
        .mapIndexed { index, date -> date to DroidKaigi2026Day.entries[index.coerceAtMost(DroidKaigi2026Day.entries.lastIndex)] }
        .toMap()
    return sessions
        .sortedBy { it.startsAt }
        .map { session ->
            TimetableItem(
                id = TimetableItemId(session.id),
                title = session.title.toMultiLangText(),
                room = Room.of(roomNameById[session.roomId].orEmpty()),
                speakers = session.speakers
                    .mapNotNull { speakerById[it] }
                    .map { it.toTimetableSpeaker() }
                    .toPersistentList(),
                language = when (session.language) {
                    LanguageResponse.JAPANESE -> Language.JAPANESE
                    LanguageResponse.ENGLISH -> Language.ENGLISH
                    LanguageResponse.MIXED -> Language.MIXED
                },
                day = dayByDate.getValue(session.startsAt.date()),
                startsAt = session.startsAt.time(),
                endsAt = session.endsAt.time(),
                // The API provides full ISO-8601 strings with offsets (e.g., "2026-09-02T10:00:00+09:00").
                // Parsing them directly here is the most accurate approach for the data layer.
                startsAtInstant = Instant.parse(session.startsAt),
                endsAtInstant = Instant.parse(session.endsAt),
                description = session.description?.toMultiLangText() ?: MultiLangText(ja = "", en = ""),
                targetAudience = session.targetAudience.toMultiLangText(),
                category = session.sessionCategoryItemId?.let { categoryNameById[it] },
                asset = TimetableItemAsset(
                    videoUrl = session.asset.videoUrl,
                    slideUrl = session.asset.slideUrl,
                ),
                hasInterpretation = session.interpretationTarget,
                isCancelled = session.noShow,
            )
        }
}

private fun SpeakerResponse.toTimetableSpeaker(): TimetableSpeaker = TimetableSpeaker(
    id = TimetableSpeakerId(id),
    name = fullName,
    tagLine = tagLine,
    iconUrl = profilePicture,
)

// Timestamps arrive as ISO-8601 with offset ("2026-09-02T10:00:00+09:00", wall-clock JST).
private fun String.date(): String = substringBefore('T')
private fun String.time(): String = substringAfter('T').take(5)

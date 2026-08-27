package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.SessionCategory
import io.github.droidkaigi.confsched.core.model.SessionType
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemAsset
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableSpeaker
import io.github.droidkaigi.confsched.core.model.TimetableSpeakerId
import kotlinx.collections.immutable.toPersistentList
import kotlin.time.Instant

internal fun TimetableResponse.toTimetable(): Timetable {
    val categories = toSessionCategories().toPersistentList()
    val items = toTimetableItems(categories.associateBy { it.id }).toPersistentList()
    return Timetable(
        items = items,
        categories = categories,
    )
}

fun TimetableResponse.toTimetableItems(): List<TimetableItem> =
    toTimetableItems(toSessionCategories().associateBy { it.id })

private fun TimetableResponse.toTimetableItems(
    categoryById: Map<Long, SessionCategory>,
): List<TimetableItem> {
    // Room is named in English, so that is the side SessionRoom.of matches.
    val roomNameById = rooms.associateBy({ it.id }, { it.name.toMultiLangText().en })
    val speakerById = speakers.associateBy { it.id }
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
                room = SessionRoom.of(roomNameById[session.roomId].orEmpty()),
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
                sessionType = session.sessionType.toSessionType(),
                // The API provides full ISO-8601 strings with offsets (e.g., "2026-09-02T10:00:00+09:00").
                // Parsing them directly here is the most accurate approach for the data layer.
                startsAtInstant = Instant.parse(session.startsAt),
                endsAtInstant = Instant.parse(session.endsAt),
                description = session.description?.toMultiLangText() ?: MultiLangText(ja = "", en = ""),
                targetAudience = session.targetAudience.toMultiLangText(),
                category = categoryById[session.sessionCategoryItemId],
                asset = TimetableItemAsset(
                    videoUrl = session.asset.videoUrl,
                    slideUrl = session.asset.slideUrl,
                ),
                hasInterpretation = session.interpretationTarget,
                isCancelled = session.noShow,
                message = session.message?.toMultiLangText(),
            )
        }
}

private fun SpeakerResponse.toTimetableSpeaker(): TimetableSpeaker = TimetableSpeaker(
    id = TimetableSpeakerId(id),
    name = fullName,
    tagLine = tagLine,
    iconUrl = profilePicture,
)

internal fun TimetableResponse.toSessionCategories(): List<SessionCategory> = categories
    .sortedBy { it.sort }
    .flatMap { group -> group.items.sortedBy { it.sort } }
    .map { item -> SessionCategory(id = item.id, name = item.name.toMultiLangText()) }

private fun SessionTypeResponse.toSessionType(): SessionType = when (this) {
    SessionTypeResponse.NORMAL -> SessionType.NORMAL
    SessionTypeResponse.WELCOME_TALK -> SessionType.WELCOME_TALK
    SessionTypeResponse.RESERVED -> SessionType.RESERVED
    SessionTypeResponse.CODELABS -> SessionType.CODELABS
    SessionTypeResponse.FIRESIDE_CHAT -> SessionType.FIRESIDE_CHAT
    SessionTypeResponse.LUNCH -> SessionType.LUNCH
    SessionTypeResponse.BREAK -> SessionType.BREAK
    SessionTypeResponse.AFTER_PARTY -> SessionType.AFTER_PARTY
    SessionTypeResponse.RECAP -> SessionType.RECAP
}

// Timestamps arrive as ISO-8601 with offset ("2026-09-02T10:00:00+09:00", wall-clock JST).
private fun String.date(): String = substringBefore('T')

private fun String.time(): String = substringAfter('T').take(5)

package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.SessionCategory
import io.github.droidkaigi.confsched.core.model.SessionType
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId

fun TimetableResponse.toTimetableItems(): List<TimetableItem> {
    // Room is named in English, so that is the side Room.of matches.
    val roomNameById = rooms.associateBy({ it.id }, { it.name.toMultiLangText().en })
    val speakerNameById = speakers.associateBy({ it.id }, { it.fullName })
    val categoryById = toSessionCategories().associateBy { it.id }
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
                speaker = session.speakers.mapNotNull { speakerNameById[it] }.joinToString(", "),
                language = when (session.language) {
                    LanguageResponse.JAPANESE -> Language.JAPANESE
                    LanguageResponse.ENGLISH -> Language.ENGLISH
                    LanguageResponse.MIXED -> Language.MIXED
                },
                day = dayByDate.getValue(session.startsAt.date()),
                startsAt = session.startsAt.time(),
                endsAt = session.endsAt.time(),
                sessionType = session.sessionType.toSessionType(),
                category = categoryById[session.sessionCategoryItemId],
            )
        }
}

/** The category items the payload names, lifted out of their groups and ordered as it sorts them. */
fun TimetableResponse.toSessionCategories(): List<SessionCategory> = categories
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

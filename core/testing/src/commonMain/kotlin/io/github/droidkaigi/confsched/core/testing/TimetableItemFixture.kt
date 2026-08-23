package io.github.droidkaigi.confsched.core.testing

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemAsset
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableSpeaker
import io.github.droidkaigi.confsched.core.model.TimetableSpeakerId
import kotlinx.collections.immutable.persistentListOf

/** Everything a test does not name takes a neutral value. */
fun testTimetableItem(
    id: String,
    title: String,
    room: Room,
    speaker: String,
    language: Language,
    day: DroidKaigi2026Day,
    startsAt: String,
    endsAt: String,
    isCancelled: Boolean = false,
): TimetableItem = TimetableItem(
    id = TimetableItemId(id),
    title = MultiLangText(ja = title, en = title),
    room = room,
    speakers = persistentListOf(
        TimetableSpeaker(
            id = TimetableSpeakerId(speaker),
            name = speaker,
            tagLine = "",
            iconUrl = null,
        ),
    ),
    language = language,
    day = day,
    startsAt = startsAt,
    endsAt = endsAt,
    startsAtInstant = day.at(startsAt),
    endsAtInstant = day.at(endsAt),
    description = MultiLangText(ja = "", en = ""),
    targetAudience = MultiLangText(ja = "", en = ""),
    category = null,
    asset = TimetableItemAsset.Empty,
    hasInterpretation = false,
    isCancelled = isCancelled,
)

private fun DroidKaigi2026Day.at(time: String) =
    at(hour = time.substringBefore(':').toInt(), minute = time.substringAfter(':').toInt())

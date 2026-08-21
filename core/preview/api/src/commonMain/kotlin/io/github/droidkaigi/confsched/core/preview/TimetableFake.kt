package io.github.droidkaigi.confsched.core.preview

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

fun Timetable.Companion.fake(): Timetable = Timetable(
    items = persistentListOf(
        fakeItem("d1a", MultiLangText(ja = "サンプルセッションA", en = "Sample Session A"), Room.NARWHAL, "", Language.MIXED, DroidKaigi2026Day.Day1, "10:00", "10:20"),
        TimetableItem.fake(),
        fakeItem("d1c", MultiLangText(ja = "サンプルセッションC", en = "Sample Session C"), Room.PANDA, "Speaker C", Language.ENGLISH, DroidKaigi2026Day.Day1, "11:00", "11:40"),
        fakeItem(
            "d1d",
            MultiLangText(
                ja = "サンプルセッションD、折り返しを確かめるための長いプレースホルダーのタイトル",
                en = "Sample Session D, with a placeholder title long enough to wrap onto several lines",
            ),
            Room.QUAIL,
            "Speaker D",
            Language.ENGLISH,
            DroidKaigi2026Day.Day1,
            "13:00",
            "13:45",
        ),
        fakeItem(
            "d1e",
            MultiLangText(
                ja = "サンプルセッションE、折り返しを確かめるための長いプレースホルダーのタイトル",
                en = "Sample Session E, with a placeholder title long enough to wrap onto several lines",
            ),
            Room.MEERKAT,
            "Speaker E",
            Language.ENGLISH,
            DroidKaigi2026Day.Day1,
            "13:00",
            "13:45",
        ),
        fakeItem("d2a", MultiLangText(ja = "サンプルセッションF", en = "Sample Session F"), Room.OTTER, "Speaker F", Language.MIXED, DroidKaigi2026Day.Day2, "10:00", "10:40"),
        fakeItem(
            "d2b",
            MultiLangText(
                ja = "サンプルセッションG、そこそこ長いプレースホルダーのタイトル",
                en = "Sample Session G, with a moderately long placeholder title",
            ),
            Room.NARWHAL,
            "Speaker A",
            Language.MIXED,
            DroidKaigi2026Day.Day2,
            "11:00",
            "11:40",
        ),
    ),
    bookmarks = persistentSetOf(TimetableItemId("d1a"), TimetableItemId("d1b"), TimetableItemId("d2a")),
)

fun TimetableItem.Companion.fake(): TimetableItem = fakeItem(
    id = "d1b",
    title = MultiLangText(ja = "サンプルセッションB", en = "Sample Session B"),
    room = Room.OTTER,
    speaker = "Speaker B",
    language = Language.ENGLISH,
    day = DroidKaigi2026Day.Day1,
    startsAt = "11:00",
    endsAt = "11:40",
)

private fun fakeItem(
    id: String,
    title: MultiLangText,
    room: Room,
    speaker: String,
    language: Language,
    day: DroidKaigi2026Day,
    startsAt: String,
    endsAt: String,
): TimetableItem {
    // Unlike the data layer which parses full ISO-8601 strings, we construct Instants manually here.
    // This allows us to keep the mock data definitions simple and readable (e.g., startsAt = "10:00")
    // without requiring full timestamp strings for every fake item.
    val startHour = startsAt.substringBefore(':').toInt()
    val startMinute = startsAt.substringAfter(':').toInt()
    val endHour = endsAt.substringBefore(':').toInt()
    val endMinute = endsAt.substringAfter(':').toInt()

    return TimetableItem(
        id = TimetableItemId(id),
        title = title,
        room = room,
        speaker = speaker,
        language = language,
        day = day,
        startsAt = startsAt,
        endsAt = endsAt,
        startsAtInstant = day.at(hour = startHour, minute = startMinute),
        endsAtInstant = day.at(hour = endHour, minute = endMinute),
    )
}

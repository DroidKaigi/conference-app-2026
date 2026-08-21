package io.github.droidkaigi.confsched.core.preview

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.SessionCategory
import io.github.droidkaigi.confsched.core.model.SessionType
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

private val sampleCategories = persistentListOf(
    SessionCategory(id = 11L, name = MultiLangText(ja = "サンプル分類1", en = "Sample Category 1")),
    SessionCategory(id = 12L, name = MultiLangText(ja = "サンプル分類2", en = "Sample Category 2")),
)

fun Timetable.Companion.fake(): Timetable = Timetable(
    items = persistentListOf(
        fakeItem("d1a", MultiLangText(ja = "サンプルセッションA", en = "Sample Session A"), Room.NARWHAL, "", Language.MIXED, DroidKaigi2026Day.Day1, "10:00", "10:20"),
        TimetableItem.fake(),
        fakeItem("d1c", MultiLangText(ja = "サンプルセッションC", en = "Sample Session C"), Room.PANDA, "Speaker C", Language.ENGLISH, DroidKaigi2026Day.Day1, "11:00", "11:40", SessionType.CODELABS),
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
            category = sampleCategories[1],
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
            sessionType = SessionType.FIRESIDE_CHAT,
            category = null,
        ),
    ),
    bookmarks = persistentSetOf(TimetableItemId("d1a"), TimetableItemId("d1b"), TimetableItemId("d2a")),
    categories = sampleCategories,
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
    category = sampleCategories[1],
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
    sessionType: SessionType = SessionType.NORMAL,
    category: SessionCategory? = sampleCategories.first(),
) = TimetableItem(
    id = TimetableItemId(id),
    title = title,
    room = room,
    speaker = speaker,
    language = language,
    day = day,
    startsAt = startsAt,
    endsAt = endsAt,
    sessionType = sessionType,
    category = category,
)

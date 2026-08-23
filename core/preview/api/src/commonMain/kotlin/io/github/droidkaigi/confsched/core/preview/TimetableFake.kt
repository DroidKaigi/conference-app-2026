package io.github.droidkaigi.confsched.core.preview

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.Speaker
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.persistentSetOf

fun Timetable.Companion.fake(): Timetable = Timetable(
    items = persistentListOf(
        fakeItem("d1a", MultiLangText(ja = "サンプルセッションA", en = "Sample Session A"), Room.NARWHAL, listOf("Speaker A"), Language.MIXED, DroidKaigi2026Day.Day1, "10:00", "10:20"),
        TimetableItem.fake(),
        fakeItem("d1c", MultiLangText(ja = "サンプルセッションC", en = "Sample Session C"), Room.PANDA, listOf("Speaker C", "Speaker D"), Language.ENGLISH, DroidKaigi2026Day.Day1, "11:00", "11:40"),
        fakeItem(
            "d1d",
            MultiLangText(
                ja = "サンプルセッションD、折り返しを確かめるための長いプレースホルダーのタイトル",
                en = "Sample Session D, with a placeholder title long enough to wrap onto several lines",
            ),
            Room.QUAIL,
            listOf("Speaker D", "Speaker B", "Speaker C"),
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
            listOf("Speaker E", "Speaker A"),
            Language.ENGLISH,
            DroidKaigi2026Day.Day1,
            "13:00",
            "13:45",
        ),
        fakeItem("d2a", MultiLangText(ja = "サンプルセッションF", en = "Sample Session F"), Room.OTTER, listOf("Speaker F"), Language.MIXED, DroidKaigi2026Day.Day2, "10:00", "10:40"),
        fakeItem(
            "d2b",
            MultiLangText(
                ja = "サンプルセッションG、そこそこ長いプレースホルダーのタイトル",
                en = "Sample Session G, with a moderately long placeholder title",
            ),
            Room.NARWHAL,
            listOf("Speaker A", "Speaker B"),
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
    speakers = listOf("Speaker B"),
    language = Language.ENGLISH,
    day = DroidKaigi2026Day.Day1,
    startsAt = "11:00",
    endsAt = "11:40",
)

private fun fakeItem(
    id: String,
    title: MultiLangText,
    room: Room,
    speakers: List<String>,
    language: Language,
    day: DroidKaigi2026Day,
    startsAt: String,
    endsAt: String,
) = TimetableItem(
    id = TimetableItemId(id),
    title = title,
    room = room,
    speakers = speakers.map(::fakeSpeaker).toPersistentList(),
    language = language,
    day = day,
    startsAt = startsAt,
    endsAt = endsAt,
)

private fun fakeSpeaker(name: String) = Speaker(
    name = name,
    iconUrl = PreviewImage.SpeakerAvatarA.imageUrl,
)

package io.github.droidkaigi.confsched.core.preview

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemAsset
import io.github.droidkaigi.confsched.core.model.TimetableItemDetail
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableSpeaker
import io.github.droidkaigi.confsched.core.model.TimetableSpeakerId
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

fun Timetable.Companion.fake(): Timetable = Timetable(
    items = persistentListOf(
        fakeItem(
            id = "d1a",
            title = MultiLangText(ja = "サンプルセッションA", en = "Sample Session A"),
            room = Room.NARWHAL,
            speakers = persistentListOf(),
            language = Language.MIXED,
            day = DroidKaigi2026Day.Day1,
            startsAt = "10:00",
            endsAt = "10:20",
            asset = TimetableItemAsset.Empty,
            isCancelled = false,
        ),
        TimetableItem.fake(),
        fakeItem(
            id = "d1c",
            title = MultiLangText(ja = "サンプルセッションC", en = "Sample Session C"),
            room = Room.MEERKAT,
            speakers = persistentListOf(fakeSpeaker("sp3", "Speaker C")),
            language = Language.ENGLISH,
            day = DroidKaigi2026Day.Day1,
            startsAt = "11:00",
            endsAt = "11:40",
            asset = TimetableItemAsset.Empty,
            isCancelled = false,
        ),
        fakeItem(
            id = "d1d",
            title = MultiLangText(
                ja = "サンプルセッションD、折り返しを確かめるための長いプレースホルダーのタイトル",
                en = "Sample Session D, with a placeholder title long enough to wrap onto several lines",
            ),
            room = Room.PANDA,
            speakers = persistentListOf(fakeSpeaker("sp4", "Speaker D")),
            language = Language.ENGLISH,
            day = DroidKaigi2026Day.Day1,
            startsAt = "11:00",
            endsAt = "11:40",
            asset = TimetableItemAsset.Empty,
            isCancelled = true,
        ),
        fakeItem(
            id = "d1e",
            title = MultiLangText(
                ja = "サンプルセッションE、折り返しを確かめるための長いプレースホルダーのタイトル",
                en = "Sample Session E, with a placeholder title long enough to wrap onto several lines",
            ),
            room = Room.QUAIL,
            speakers = persistentListOf(fakeSpeaker("sp5", "Speaker E")),
            language = Language.ENGLISH,
            day = DroidKaigi2026Day.Day1,
            startsAt = "13:00",
            endsAt = "13:45",
            asset = TimetableItemAsset.Empty,
            isCancelled = false,
        ),
        fakeItem(
            id = "d2a",
            title = MultiLangText(ja = "サンプルセッションF", en = "Sample Session F"),
            room = Room.OTTER,
            speakers = persistentListOf(fakeSpeaker("sp6", "Speaker F")),
            language = Language.MIXED,
            day = DroidKaigi2026Day.Day2,
            startsAt = "10:00",
            endsAt = "10:40",
            asset = TimetableItemAsset.Empty,
            isCancelled = false,
        ),
        fakeItem(
            id = "d2b",
            title = MultiLangText(
                ja = "サンプルセッションG、そこそこ長いプレースホルダーのタイトル",
                en = "Sample Session G, with a moderately long placeholder title",
            ),
            room = Room.NARWHAL,
            speakers = persistentListOf(fakeSpeaker("sp1", "Speaker A")),
            language = Language.MIXED,
            day = DroidKaigi2026Day.Day2,
            startsAt = "11:00",
            endsAt = "11:40",
            asset = TimetableItemAsset.Empty,
            isCancelled = false,
        ),
    ),
    bookmarks = persistentSetOf(TimetableItemId("d1a"), TimetableItemId("d1b"), TimetableItemId("d2a")),
)

fun TimetableItem.Companion.fake(): TimetableItem = fakeItem(
    id = "d1b",
    title = MultiLangText(
        ja = "サンプルセッションB、折り返しを確かめるための長いプレースホルダーのタイトル",
        en = "Sample Session B, with a placeholder title long enough to wrap onto several lines",
    ),
    room = Room.OTTER,
    speakers = persistentListOf(
        fakeSpeaker("sp1", "Speaker A"),
        fakeSpeaker("sp2", "Speaker B"),
        fakeSpeaker("sp3", "Speaker C"),
    ),
    language = Language.JAPANESE,
    day = DroidKaigi2026Day.Day1,
    startsAt = "11:00",
    endsAt = "11:40",
    asset = TimetableItemAsset(
        videoUrl = "https://example.com/sessions/d1b/video",
        slideUrl = "https://example.com/sessions/d1b/slides",
    ),
    isCancelled = false,
)

fun TimetableItemDetail.Companion.fake(): TimetableItemDetail =
    Timetable.fake().detailOf(TimetableItemId("d1b"))

private fun fakeSpeaker(id: String, name: String) = TimetableSpeaker(
    id = TimetableSpeakerId(id),
    name = name,
    tagLine = "Job Title / Affiliation",
    iconUrl = null,
)

private fun fakeItem(
    id: String,
    title: MultiLangText,
    room: Room,
    speakers: PersistentList<TimetableSpeaker>,
    language: Language,
    day: DroidKaigi2026Day,
    startsAt: String,
    endsAt: String,
    asset: TimetableItemAsset,
    isCancelled: Boolean,
): TimetableItem {
    // Fake items take HH:mm strings; derive Instants via day.at to avoid full ISO-8601 timestamps.
    val startHour = startsAt.substringBefore(':').toInt()
    val startMinute = startsAt.substringAfter(':').toInt()
    val endHour = endsAt.substringBefore(':').toInt()
    val endMinute = endsAt.substringAfter(':').toInt()

    return TimetableItem(
        id = TimetableItemId(id),
        title = title,
        room = room,
        speakers = speakers,
        language = language,
        day = day,
        startsAt = startsAt,
        endsAt = endsAt,
        startsAtInstant = day.at(hour = startHour, minute = startMinute),
        endsAtInstant = day.at(hour = endHour, minute = endMinute),
        description = MultiLangText(
            ja = "本セッションでは、サンプルアプリの設計とその変遷をたどります。積み重ねてきた選択のひとつひとつを、実際のコードとともに解説いたします。折り返しと「もっとみる」の挙動を確かめられるだけの長さを持たせたプレースホルダーの本文です。",
            en = "This session walks through the architecture of a sample app and how it has changed, taking each of the choices behind it in turn alongside the code. The placeholder body runs long enough to exercise wrapping and the show-more control.",
        ),
        targetAudience = MultiLangText(
            ja = "- モダンなAndroidアプリ開発の設計に興味がある方\n- Kotlin Multiplatformの実践的な適用例を知りたい方\n- マルチプラットフォーム対応の知見を自分のプロジェクトに活かしたい方",
            en = "- Anyone interested in the design of a modern Android app\n- Anyone after a worked example of Kotlin Multiplatform\n- Anyone taking multiplatform findings back to their own project",
        ),
        category = MultiLangText(ja = "Jetpack Compose", en = "Jetpack Compose"),
        asset = asset,
        hasInterpretation = language == Language.JAPANESE,
        isCancelled = isCancelled,
    )
}

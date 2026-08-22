package io.github.droidkaigi.confsched.core.data

import kotlinx.coroutines.delay

class FakeTimetableApi : TimetableApi {
    override suspend fun getTimetable(): TimetableResponse {
        delay(300)
        return TimetableResponse(
            status = HttpStatusResponse.OK,
            sessions = listOf(
                fakeSession(
                    id = "s1",
                    title = LocaledResponse(ja = "サンプルセッションA", en = "Sample Session A"),
                    roomId = NARWHAL,
                    speakerIds = listOf("sp1"),
                    language = LanguageResponse.JAPANESE,
                    startsAt = "2026-09-02T10:00:00+09:00",
                    endsAt = "2026-09-02T10:40:00+09:00",
                    categoryItemId = JETPACK_COMPOSE,
                    interpretationTarget = false,
                    noShow = false,
                    asset = SessionAssetResponse(),
                ),
                fakeSession(
                    id = "s2",
                    title = LocaledResponse(
                        ja = "サンプルセッションB、折り返しを確かめるための長いプレースホルダーのタイトル",
                        en = "Sample Session B, with a placeholder title long enough to wrap onto several lines",
                    ),
                    roomId = OTTER,
                    speakerIds = listOf("sp1", "sp2", "sp3"),
                    language = LanguageResponse.JAPANESE,
                    startsAt = "2026-09-02T11:20:00+09:00",
                    endsAt = "2026-09-02T12:00:00+09:00",
                    categoryItemId = JETPACK_COMPOSE,
                    interpretationTarget = true,
                    noShow = false,
                    asset = SessionAssetResponse(
                        videoUrl = "https://example.com/sessions/s2/video",
                        slideUrl = "https://example.com/sessions/s2/slides",
                    ),
                ),
                fakeSession(
                    id = "s3",
                    title = LocaledResponse(ja = "サンプルセッションC", en = "Sample Session C"),
                    roomId = MEERKAT,
                    speakerIds = listOf("sp4"),
                    language = LanguageResponse.ENGLISH,
                    startsAt = "2026-09-02T11:20:00+09:00",
                    endsAt = "2026-09-02T12:00:00+09:00",
                    categoryItemId = KOTLIN_MULTIPLATFORM,
                    interpretationTarget = false,
                    noShow = false,
                    asset = SessionAssetResponse(),
                ),
                fakeSession(
                    id = "s4",
                    title = LocaledResponse(ja = "サンプルセッションD", en = "Sample Session D"),
                    roomId = PANDA,
                    speakerIds = listOf("sp5"),
                    language = LanguageResponse.ENGLISH,
                    startsAt = "2026-09-02T11:20:00+09:00",
                    endsAt = "2026-09-02T12:00:00+09:00",
                    categoryItemId = KOTLIN_MULTIPLATFORM,
                    interpretationTarget = false,
                    noShow = true,
                    asset = SessionAssetResponse(),
                ),
                fakeSession(
                    id = "s5",
                    title = LocaledResponse(ja = "サンプルセッションE", en = "Sample Session E"),
                    roomId = QUAIL,
                    speakerIds = listOf("sp6"),
                    language = LanguageResponse.MIXED,
                    startsAt = "2026-09-03T10:00:00+09:00",
                    endsAt = "2026-09-03T10:40:00+09:00",
                    categoryItemId = BUILD_AND_TOOLING,
                    interpretationTarget = false,
                    noShow = false,
                    asset = SessionAssetResponse(),
                ),
                fakeSession(
                    id = "s6",
                    title = LocaledResponse(
                        ja = "サンプルセッションF、そこそこ長いプレースホルダーのタイトル",
                        en = "Sample Session F, with a moderately long placeholder title",
                    ),
                    roomId = OTTER,
                    speakerIds = listOf("sp2"),
                    language = LanguageResponse.ENGLISH,
                    startsAt = "2026-09-03T11:00:00+09:00",
                    endsAt = "2026-09-03T11:40:00+09:00",
                    categoryItemId = BUILD_AND_TOOLING,
                    interpretationTarget = false,
                    noShow = false,
                    asset = SessionAssetResponse(),
                ),
            ),
            rooms = listOf(
                RoomResponse(name = LocaledResponse(ja = "Narwhal", en = "Narwhal"), id = NARWHAL, sort = 1),
                RoomResponse(name = LocaledResponse(ja = "Otter", en = "Otter"), id = OTTER, sort = 2),
                RoomResponse(name = LocaledResponse(ja = "Panda", en = "Panda"), id = PANDA, sort = 3),
                RoomResponse(name = LocaledResponse(ja = "Quail", en = "Quail"), id = QUAIL, sort = 4),
                RoomResponse(name = LocaledResponse(ja = "Meerkat", en = "Meerkat"), id = MEERKAT, sort = 5),
            ),
            speakers = listOf(
                fakeSpeaker("sp1", "Speaker A"),
                fakeSpeaker("sp2", "Speaker B"),
                fakeSpeaker("sp3", "Speaker C"),
                fakeSpeaker("sp4", "Speaker D"),
                fakeSpeaker("sp5", "Speaker E"),
                fakeSpeaker("sp6", "Speaker F"),
            ),
            categories = listOf(
                CategoryResponse(
                    id = 1L,
                    title = LocaledResponse(ja = "カテゴリ", en = "Category"),
                    items = listOf(
                        CategoryItemResponse(
                            id = JETPACK_COMPOSE,
                            name = LocaledResponse(ja = "Jetpack Compose", en = "Jetpack Compose"),
                            sort = 1,
                        ),
                        CategoryItemResponse(
                            id = KOTLIN_MULTIPLATFORM,
                            name = LocaledResponse(ja = "Kotlin Multiplatform", en = "Kotlin Multiplatform"),
                            sort = 2,
                        ),
                        CategoryItemResponse(
                            id = BUILD_AND_TOOLING,
                            name = LocaledResponse(ja = "ビルドとツール", en = "Build and Tooling"),
                            sort = 3,
                        ),
                    ),
                    sort = 1,
                ),
            ),
        )
    }

    private fun fakeSession(
        id: String,
        title: LocaledResponse,
        roomId: Long,
        speakerIds: List<String>,
        language: LanguageResponse,
        startsAt: String,
        endsAt: String,
        categoryItemId: Long,
        interpretationTarget: Boolean,
        noShow: Boolean,
        asset: SessionAssetResponse,
    ) = SessionResponse(
        id = id,
        title = title,
        speakers = speakerIds,
        startsAt = startsAt,
        endsAt = endsAt,
        language = language,
        roomId = roomId,
        lengthInMinutes = 40,
        sessionType = SessionTypeResponse.NORMAL,
        noShow = noShow,
        targetAudience = LocaledResponse(
            ja = "モダンなAndroidアプリ開発の設計に興味がある方\nKotlin Multiplatformの実践的な適用例を知りたい方\nマルチプラットフォーム対応の知見を自分のプロジェクトに活かしたい方",
            en = "Anyone interested in the design of a modern Android app\nAnyone after a worked example of Kotlin Multiplatform\nAnyone taking multiplatform findings back to their own project",
        ),
        interpretationTarget = interpretationTarget,
        asset = asset,
        description = LocaledResponse(
            ja = "本セッションでは、サンプルアプリの設計とその変遷をたどります。積み重ねてきた選択のひとつひとつを、実際のコードとともに解説いたします。折り返しと「もっとみる」の挙動を確かめられるだけの長さを持たせたプレースホルダーの本文です。",
            en = "This session walks through the architecture of a sample app and how it has changed, taking each of the choices behind it in turn alongside the code. The placeholder body runs long enough to exercise wrapping and the show-more control.",
        ),
    )

    private fun fakeSpeaker(id: String, fullName: String) = SpeakerResponse(
        id = id,
        fullName = fullName,
        tagLine = "Job Title / Affiliation",
        sessions = emptyList(),
    )

    private companion object {
        const val NARWHAL = 81669L
        const val OTTER = 81667L
        const val PANDA = 81671L
        const val QUAIL = 81670L
        const val MEERKAT = 81666L

        const val JETPACK_COMPOSE = 1001L
        const val KOTLIN_MULTIPLATFORM = 1002L
        const val BUILD_AND_TOOLING = 1003L
    }
}

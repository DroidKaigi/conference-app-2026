package io.github.droidkaigi.confsched.core.data

import kotlinx.coroutines.delay

class FakeTimetableApi : TimetableApi {
    override suspend fun getTimetable(): TimetableResponse {
        delay(300)
        return TimetableResponse(
            status = HttpStatusResponse.OK,
            sessions = listOf(
                fakeSession("s1", LocaledResponse(ja = "サンプルセッションA", en = "Sample Session A"), 81669L, "sp1", LanguageResponse.JAPANESE, "2026-09-02T10:00:00+09:00", "2026-09-02T10:40:00+09:00", SessionTypeResponse.NORMAL, 11L),
                fakeSession(
                    "s2",
                    LocaledResponse(
                        ja = "サンプルセッションB、折り返しを確かめるための長いプレースホルダーのタイトル",
                        en = "Sample Session B, with a placeholder title long enough to wrap onto several lines",
                    ),
                    81667L,
                    "sp2",
                    LanguageResponse.ENGLISH,
                    "2026-09-02T11:00:00+09:00",
                    "2026-09-02T11:40:00+09:00",
                    SessionTypeResponse.NORMAL,
                    12L,
                ),
                fakeSession("s3", LocaledResponse(ja = "サンプルセッションC", en = "Sample Session C"), 81669L, "sp3", LanguageResponse.MIXED, "2026-09-03T10:00:00+09:00", "2026-09-03T10:40:00+09:00", SessionTypeResponse.CODELABS, 11L),
                fakeSession(
                    "s4",
                    LocaledResponse(
                        ja = "サンプルセッションD、そこそこ長いプレースホルダーのタイトル",
                        en = "Sample Session D, with a moderately long placeholder title",
                    ),
                    81667L,
                    "sp1",
                    LanguageResponse.ENGLISH,
                    "2026-09-03T11:00:00+09:00",
                    "2026-09-03T11:40:00+09:00",
                    SessionTypeResponse.FIRESIDE_CHAT,
                    null,
                ),
            ),
            rooms = listOf(
                RoomResponse(name = LocaledResponse(ja = "Narwhal", en = "Narwhal"), id = 81669L, sort = 1),
                RoomResponse(name = LocaledResponse(ja = "Otter", en = "Otter"), id = 81667L, sort = 2),
            ),
            speakers = listOf(
                fakeSpeaker("sp1", "Speaker A"),
                fakeSpeaker("sp2", "Speaker B"),
                fakeSpeaker("sp3", "Speaker C"),
            ),
            categories = listOf(
                CategoryResponse(
                    id = 1L,
                    title = LocaledResponse(ja = "カテゴリ", en = "Category"),
                    sort = 1,
                    items = listOf(
                        CategoryItemResponse(id = 11L, name = LocaledResponse(ja = "サンプル分類1", en = "Sample Category 1"), sort = 1),
                        CategoryItemResponse(id = 12L, name = LocaledResponse(ja = "サンプル分類2", en = "Sample Category 2"), sort = 2),
                    ),
                ),
            ),
        )
    }

    private fun fakeSession(
        id: String,
        title: LocaledResponse,
        roomId: Long,
        speakerId: String,
        language: LanguageResponse,
        startsAt: String,
        endsAt: String,
        sessionType: SessionTypeResponse,
        categoryItemId: Long?,
    ) = SessionResponse(
        id = id,
        title = title,
        speakers = listOf(speakerId),
        startsAt = startsAt,
        endsAt = endsAt,
        language = language,
        roomId = roomId,
        lengthInMinutes = 40,
        sessionType = sessionType,
        noShow = false,
        targetAudience = LocaledResponse(ja = "全員", en = "All"),
        interpretationTarget = false,
        asset = SessionAssetResponse(),
        sessionCategoryItemId = categoryItemId,
    )

    private fun fakeSpeaker(id: String, fullName: String) = SpeakerResponse(
        id = id,
        fullName = fullName,
        tagLine = "",
        sessions = emptyList(),
    )
}

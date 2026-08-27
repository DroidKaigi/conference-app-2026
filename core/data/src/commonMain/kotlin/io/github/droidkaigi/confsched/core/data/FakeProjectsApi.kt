package io.github.droidkaigi.confsched.core.data

import kotlinx.coroutines.delay

class FakeProjectsApi : ProjectsApi {
    override suspend fun getProjects(): ProjectListResponse {
        delay(300)
        return ProjectListResponse(
            status = HttpStatusResponse.OK,
            projects = listOf(
                fakeProject(
                    id = "1",
                    titleJa = "Meetup（ランチタイム）",
                    titleEn = "Meetup (Lunchtime)",
                    descJa = "様々なテーマごとに集まって、一緒にランチを食べながらお話ししましょう。席に限りがありますので、お弁当受け取り後お早めにお越しください。",
                    descEn = "Gather around a topic and chat over lunch. Seats are limited, so please come soon after picking up your bento.",
                    roomId = 100,
                    messageJa = "※こちらのイベントは時間が変更されました",
                    messageEn = "* The time of this event has changed",
                ),
                fakeProject(
                    id = "2",
                    titleJa = "キャリア相談会",
                    titleEn = "Career Counseling",
                    descJa = "様々なテーマごとに集まって、一緒にランチを食べながらお話ししましょう。席に限りがありますので、お弁当受け取り後お早めにお越しください。",
                    descEn = "Gather around a topic and chat over lunch. Seats are limited, so please come soon after picking up your bento.",
                    roomId = 102,
                ),
                fakeProject(
                    id = "3",
                    titleJa = "コミュニケーションエリア",
                    titleEn = "Communication Area",
                    descJa = "様々なテーマごとに集まって、一緒にランチを食べながらお話ししましょう。席に限りがありますので、お弁当受け取り後お早めにお越しください。",
                    descEn = "Gather around a topic and chat over lunch. Seats are limited, so please come soon after picking up your bento.",
                    roomId = 103,
                ),
            ),
            rooms = listOf(
                RoomResponse(id = 100, name = LocaledResponse(ja = "ホワイエ", en = "Foyer"), sort = 0),
                fakeRoom(102, "OTTER"),
                fakeRoom(103, "QUAIL"),
            ),
        )
    }

    private fun fakeProject(
        id: String,
        titleJa: String,
        titleEn: String,
        descJa: String,
        descEn: String,
        roomId: Long,
        messageJa: String? = null,
        messageEn: String? = null,
    ) = ProjectResponse(
        id = id,
        title = LocaledResponse(ja = titleJa, en = titleEn),
        i18nDesc = LocaledResponse(ja = descJa, en = descEn),
        roomId = roomId,
        startsAt = "2026-09-02T12:00:00+09:00",
        endsAt = "2026-09-02T13:00:00+09:00",
        noShow = false,
        message = if (messageJa != null && messageEn != null) {
            LocaledResponse(ja = messageJa, en = messageEn)
        } else {
            null
        },
        moreDetailsUrl = "https://example.com/",
    )

    private fun fakeRoom(id: Long, name: String) = RoomResponse(
        id = id,
        name = LocaledResponse(ja = name, en = name),
        sort = 0,
    )
}

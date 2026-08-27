package io.github.droidkaigi.confsched.core.preview

import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.NamedRoom
import io.github.droidkaigi.confsched.core.model.Project
import io.github.droidkaigi.confsched.core.model.ProjectId
import io.github.droidkaigi.confsched.core.model.Projects
import io.github.droidkaigi.confsched.core.model.SessionRoom
import kotlinx.collections.immutable.persistentListOf

fun Projects.Companion.fake(): Projects = Projects(
    items = persistentListOf(
        Project(
            id = ProjectId("1"),
            title = MultiLangText(
                ja = "Meetup（ランチタイム）",
                en = "Meetup (Lunchtime)",
            ),
            description = MultiLangText(
                ja = "様々なテーマごとに集まって、一緒にランチを食べながらお話ししましょう。席に限りがありますので、お弁当受け取り後お早めにお越しください。",
                en = "Gather around a topic and chat over lunch. Seats are limited, so please come soon after picking up your bento.",
            ),
            room = NamedRoom(MultiLangText(ja = "ホワイエ", en = "Foyer")),
            message = MultiLangText(
                ja = "※こちらのイベントは時間が変更されました",
                en = "* The time of this event has changed",
            ),
            moreDetailsUrl = "https://example.com/",
        ),
        Project(
            id = ProjectId("2"),
            title = MultiLangText(
                ja = "キャリア相談会",
                en = "Career Counseling",
            ),
            description = MultiLangText(
                ja = "様々なテーマごとに集まって、一緒にランチを食べながらお話ししましょう。席に限りがありますので、お弁当受け取り後お早めにお越しください。",
                en = "Gather around a topic and chat over lunch. Seats are limited, so please come soon after picking up your bento.",
            ),
            room = SessionRoom.OTTER,
            moreDetailsUrl = "https://droidkaigi.jp/2026/",
        ),
        Project(
            id = ProjectId("3"),
            title = MultiLangText(
                ja = "コミュニケーションエリア",
                en = "Communication Area",
            ),
            description = MultiLangText(
                ja = "様々なテーマごとに集まって、一緒にランチを食べながらお話ししましょう。席に限りがありますので、お弁当受け取り後お早めにお越しください。",
                en = "Gather around a topic and chat over lunch. Seats are limited, so please come soon after picking up your bento.",
            ),
            room = SessionRoom.QUAIL,
        ),
    ),
)

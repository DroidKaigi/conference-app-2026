package io.github.droidkaigi.confsched.core.preview

import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Project
import io.github.droidkaigi.confsched.core.model.ProjectId
import io.github.droidkaigi.confsched.core.model.Projects
import io.github.droidkaigi.confsched.core.model.Room
import kotlinx.collections.immutable.persistentListOf

fun Projects.Companion.fake(): Projects = Projects(
    items = persistentListOf(
        Project(
            id = ProjectId("1"),
            title = MultiLangText(ja = "Meetup（ランチタイム）", en = "Meetup (Lunchtime)"),
            i18nDesc = MultiLangText(ja = "Description Ja", en = "Description En"),
            room = Room.NARWHAL,
            message = MultiLangText(ja = "Message Ja", en = "Message En"),
            moreDetailsUrl = "https://droidkaigi.jp/2026/",
        ),
        Project(
            id = ProjectId("2"),
            title = MultiLangText(ja = "キャリア相談会", en = "Career Counseling"),
            i18nDesc = MultiLangText(ja = "Description Ja", en = "Description En"),
            room = Room.OTTER,
            moreDetailsUrl = "https://droidkaigi.jp/2026/",
        ),
        Project(
            id = ProjectId("3"),
            title = MultiLangText(ja = "コミュニケーションエリア", en = "Communication Area"),
            i18nDesc = MultiLangText(ja = "Description Ja", en = "Description En"),
            room = Room.QUAIL,
        ),
    ),
)

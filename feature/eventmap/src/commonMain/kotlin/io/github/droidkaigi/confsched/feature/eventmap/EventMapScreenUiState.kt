package io.github.droidkaigi.confsched.feature.eventmap

import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Project
import io.github.droidkaigi.confsched.core.model.Room
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class EventMapScreenUiState(
    val selectedFloor: EventMapFloor,
    val projects: PersistentList<Project>,
) {
    companion object {
        fun mock(selectedFloor: EventMapFloor): PersistentList<Project> {
            return when (selectedFloor) {
                EventMapFloor.Ground -> persistentListOf(
                    Project(
                        title = MultiLangText(
                            ja = "Meetup（ランチタイム）",
                            en = "Meetup (Lunchtime)"
                        ),
                        description = MultiLangText(
                            ja = "様々なテーマごとに集まって、一緒にランチを食べながらお話ししましょう。席에限りがありますので、お弁当受け取り後お早めにお越しください。",
                            en = "Gather around a topic and chat over lunch. Seats are limited, so please come soon after picking up your bento.",
                        ),
                        room = Room.NARWHAL,
                        note = MultiLangText(
                            ja = "※こちらのイベントは時間が変更されました",
                            en = "* The time of this event has changed",
                        ),
                        detailPage = "https://droidkaigi.jp/2026/",
                    ),
                    Project(
                        title = MultiLangText(
                            ja = "キャリア相談会",
                            en = "Career Counseling"
                        ),
                        description = MultiLangText(
                            ja = "様々なテーマごとに集まって、一緒にランチを食べながらお話ししましょう。席에限りがありますので、お弁当受け取り後お早めにお越しください。",
                            en = "Gather around a topic and chat over lunch. Seats are limited, so please come soon after picking up your bento.",
                        ),
                        room = Room.OTTER,
                        detailPage = "https://droidkaigi.jp/2026/",
                    ),
                    Project(
                        title = MultiLangText(
                            ja = "コミュニケーションエリア",
                            en = "Communication Area"
                        ),
                        description = MultiLangText(
                            ja = "様々なテーマごとに集まって、一緒にランチを食べながらお話ししましょう。席에限りがありますので、お弁当受け取り後お早めにお越しください。",
                            en = "Gather around a topic and chat over lunch. Seats are limited, so please come soon after picking up your bento.",
                        ),
                        room = Room.QUAIL,
                    ),
                )

                EventMapFloor.Basement -> persistentListOf()
            }
        }
    }
}

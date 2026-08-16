package io.github.droidkaigi.confsched.feature.eventmap

import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.Res
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_career_description
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_career_title
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_communication_description
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_communication_title
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_meetup_description
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_meetup_message
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_meetup_title
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class EventMapScreenUiState(
    val selectedFloor: EventMapFloor,
    val eventMapItems: PersistentList<EventMapItem>,
) {
    companion object {
        fun mock(selectedFloor: EventMapFloor): PersistentList<EventMapItem> {
            return when (selectedFloor) {
                EventMapFloor.Ground -> persistentListOf(
                    EventMapItem(
                        title = Res.string.event_map_meetup_title,
                        description = Res.string.event_map_meetup_description,
                        room = Room.NARWHAL,
                        note = Res.string.event_map_meetup_message,
                        detailPage = "https://droidkaigi.jp/2026/",
                    ),
                    EventMapItem(
                        title = Res.string.event_map_career_title,
                        description = Res.string.event_map_career_description,
                        room = Room.OTTER,
                        detailPage = "https://droidkaigi.jp/2026/",
                    ),
                    EventMapItem(
                        title = Res.string.event_map_communication_title,
                        description = Res.string.event_map_communication_description,
                        room = Room.QUAIL,
                    ),
                )

                EventMapFloor.Basement -> persistentListOf()
            }
        }
    }
}

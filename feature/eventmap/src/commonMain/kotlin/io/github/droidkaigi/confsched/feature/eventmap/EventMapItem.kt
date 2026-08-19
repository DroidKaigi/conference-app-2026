package io.github.droidkaigi.confsched.feature.eventmap

import io.github.droidkaigi.confsched.core.model.Room
import org.jetbrains.compose.resources.StringResource

data class EventMapItem(
    val title: StringResource,
    val description: StringResource,
    val room: Room,
    val note: StringResource? = null,
    val detailPage: String? = null,
)

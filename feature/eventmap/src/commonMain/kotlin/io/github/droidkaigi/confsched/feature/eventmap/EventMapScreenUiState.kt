package io.github.droidkaigi.confsched.feature.eventmap

import io.github.droidkaigi.confsched.core.model.Project
import kotlinx.collections.immutable.PersistentList

data class EventMapScreenUiState(
    val selectedFloor: EventMapFloor,
    val projects: PersistentList<Project>,
)

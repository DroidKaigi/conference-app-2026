package io.github.droidkaigi.confsched.feature.eventmap

import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.model.Project
import kotlinx.collections.immutable.PersistentList

data class EventMapScreenUiState(
    val selectedFloor: Floor,
    val projects: PersistentList<Project>,
)

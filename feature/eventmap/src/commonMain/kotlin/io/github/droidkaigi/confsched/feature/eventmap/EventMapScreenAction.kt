package io.github.droidkaigi.confsched.feature.eventmap

import io.github.droidkaigi.confsched.core.model.Floor

sealed interface EventMapScreenAction {
    data class SelectFloor(val floor: Floor) : EventMapScreenAction
}

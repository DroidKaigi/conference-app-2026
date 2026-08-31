package io.github.droidkaigi.confsched.feature.about

import io.github.droidkaigi.confsched.core.model.Doodle

sealed interface AboutScreenAction {
    data object StartDoodling : AboutScreenAction

    data object CancelDoodling : AboutScreenAction

    data class SaveWallDoodle(val doodle: Doodle) : AboutScreenAction
}

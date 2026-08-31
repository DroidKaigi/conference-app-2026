package io.github.droidkaigi.confsched.feature.doodle

import io.github.droidkaigi.confsched.core.model.Doodle

sealed interface DoodleScreenAction {
    data class SaveWall(val doodle: Doodle) : DoodleScreenAction

    data class SaveCard(val frontDoodle: Doodle, val backDoodle: Doodle) : DoodleScreenAction
}

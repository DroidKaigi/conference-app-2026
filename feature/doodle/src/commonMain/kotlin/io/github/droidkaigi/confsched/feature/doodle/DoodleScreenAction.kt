package io.github.droidkaigi.confsched.feature.doodle

import io.github.droidkaigi.confsched.core.model.Doodle

sealed interface DoodleScreenAction {
    data class Save(val doodle: Doodle) : DoodleScreenAction
}

package io.github.droidkaigi.confsched.feature.doodle

import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.ProfileCard

data class DoodleScreenUiState(
    val target: DoodleTarget,
    val savedDoodle: Doodle,
    /** Null while the user has not created their card; the card targets are then unreachable. */
    val card: ProfileCard?,
    val isSaving: Boolean,
)

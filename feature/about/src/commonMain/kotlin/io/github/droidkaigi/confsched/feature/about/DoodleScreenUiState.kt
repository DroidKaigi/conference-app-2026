package io.github.droidkaigi.confsched.feature.about

import io.github.droidkaigi.confsched.core.model.Doodle

data class DoodleScreenUiState(
    val savedDoodle: Doodle,
    val isSaving: Boolean,
)

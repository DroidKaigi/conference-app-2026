package io.github.droidkaigi.confsched.feature.about

import io.github.droidkaigi.confsched.core.model.Doodle

data class AboutScreenUiState(
    val versionName: String,
    val doodle: Doodle,
    val isDoodlingWall: Boolean,
)

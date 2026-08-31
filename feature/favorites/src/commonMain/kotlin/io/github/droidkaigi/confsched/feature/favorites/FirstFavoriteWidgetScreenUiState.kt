package io.github.droidkaigi.confsched.feature.favorites

import io.github.droidkaigi.confsched.core.model.Mascot

data class FirstFavoriteWidgetScreenUiState(
    val canAddWidget: Boolean,
    val mascot: Mascot,
)

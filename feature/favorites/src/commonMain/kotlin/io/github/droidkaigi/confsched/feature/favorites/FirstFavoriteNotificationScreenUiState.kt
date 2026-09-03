package io.github.droidkaigi.confsched.feature.favorites

import io.github.droidkaigi.confsched.core.model.Mascot

data class FirstFavoriteNotificationScreenUiState(
    val isAnswering: Boolean,
    val areNotificationsOn: Boolean,
    val mascot: Mascot,
)

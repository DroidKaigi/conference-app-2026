package io.github.droidkaigi.confsched.feature.favorites

import io.github.droidkaigi.confsched.core.model.Mascot

data class FirstFavoriteWidgetScreenUiState(
    val pinSupport: FirstFavoriteWidgetPinSupport,
    val mascot: Mascot,
)

enum class FirstFavoriteWidgetPinSupport {
    /** The home screen accepts a pin request, so the step offers an add button. */
    Requestable,

    /** The platform has no pin request API; manual instructions are the only path. */
    ManualOnly,

    /** This device cannot pin the widget; the step only acknowledges and closes. */
    Unsupported,
}

package io.github.droidkaigi.confsched.feature.profilecard

import io.github.droidkaigi.confsched.feature.profilecard.component.Mascot
import io.github.droidkaigi.confsched.feature.profilecard.component.SketchIntensity

sealed interface ProfileCardScreenUiState {
    data class Form(
        val nickName: String = "",
        val occupation: String = "",
        val link: String = "",
        val mascot: Mascot = Mascot.Koala,
        val sketchIntensity: SketchIntensity = SketchIntensity.Normal,
        val hasAvatarImage: Boolean = false,
    ) : ProfileCardScreenUiState

    data class Card(
        val nickName: String,
        val occupation: String,
        val link: String,
        val mascot: Mascot,
        val sketchIntensity: SketchIntensity,
        val hasAvatarImage: Boolean,
    ) : ProfileCardScreenUiState
}

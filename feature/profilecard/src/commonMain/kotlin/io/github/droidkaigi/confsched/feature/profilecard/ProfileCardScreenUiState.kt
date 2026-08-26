package io.github.droidkaigi.confsched.feature.profilecard

import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.model.Sketchiness

sealed interface ProfileCardScreenUiState {
    data class Form(
        val nickName: String = "",
        val occupation: String = "",
        val link: String = "",
        val mascot: Mascot = ProfileCard.DefaultMascot,
        val sketchiness: Sketchiness = ProfileCard.DefaultSketchiness,
        val avatarImage: AvatarImage? = null,
        val isSubmitting: Boolean = false,
    ) : ProfileCardScreenUiState

    data class Card(
        val nickName: String,
        val occupation: String,
        val link: String,
        val mascot: Mascot,
        val sketchiness: Sketchiness,
        val avatarImage: AvatarImage?,
        val isShowingBack: Boolean = false,
    ) : ProfileCardScreenUiState
}

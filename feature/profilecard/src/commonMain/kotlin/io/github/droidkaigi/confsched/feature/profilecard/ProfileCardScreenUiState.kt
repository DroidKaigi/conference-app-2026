package io.github.droidkaigi.confsched.feature.profilecard

import io.github.droidkaigi.confsched.feature.profilecard.component.Mascot
import io.github.droidkaigi.confsched.feature.profilecard.component.Sketchiness

sealed interface ProfileCardScreenUiState {
    data class Form(
        val nickName: String = "",
        val occupation: String = "",
        val link: String = "",
        val mascot: Mascot = Mascot.Koala,
        val sketchiness: Sketchiness = Sketchiness.Normal,
        val hasAvatarImage: Boolean = false,
        val nickNameErrorMessage: String? = null,
        val occupationErrorMessage: String? = null,
        val linkErrorMessage: String? = null,
        val avatarImageErrorMessage: String? = null,
    ) : ProfileCardScreenUiState

    data class Card(
        val nickName: String,
        val occupation: String,
        val link: String,
        val mascot: Mascot,
        val sketchiness: Sketchiness,
        val hasAvatarImage: Boolean,
        val isShowingBack: Boolean = false,
    ) : ProfileCardScreenUiState
}

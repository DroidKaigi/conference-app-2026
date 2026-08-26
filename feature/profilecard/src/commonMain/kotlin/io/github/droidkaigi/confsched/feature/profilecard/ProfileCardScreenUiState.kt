package io.github.droidkaigi.confsched.feature.profilecard

import io.github.droidkaigi.confsched.feature.profilecard.component.Mascot
import io.github.droidkaigi.confsched.feature.profilecard.component.Sketchiness
import io.github.vinceglb.filekit.PlatformFile

sealed interface ProfileCardScreenUiState {
    data class Form(
        val nickName: String = "",
        val occupation: String = "",
        val link: String = "",
        val mascot: Mascot = Mascot.Koala,
        val sketchiness: Sketchiness = Sketchiness.Normal,
        val avatarImage: PlatformFile? = null,
    ) : ProfileCardScreenUiState

    data class Card(
        val nickName: String,
        val occupation: String,
        val link: String,
        val mascot: Mascot,
        val sketchiness: Sketchiness,
        val avatarImage: PlatformFile?,
        val isShowingBack: Boolean = false,
    ) : ProfileCardScreenUiState
}

package io.github.droidkaigi.confsched.feature.profilecard

import io.github.droidkaigi.confsched.feature.profilecard.component.Mascot

sealed interface ProfileCardScreenUiState {
    data class Form(
        val nickName: String = "",
        val occupation: String = "",
        val link: String = "",
        val mascot: Mascot = Mascot.Koala,
    ) : ProfileCardScreenUiState

    data class Card(
        val nickName: String,
        val occupation: String,
        val link: String,
        val mascot: Mascot,
    ) : ProfileCardScreenUiState
}

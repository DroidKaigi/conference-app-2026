package io.github.droidkaigi.confsched.feature.profilecard

sealed interface ProfileCardScreenUiState {
    data class Form(
        val nickName: String = "",
        val occupation: String = "",
        val link: String = "",
    ) : ProfileCardScreenUiState

    data class Card(
        val nickName: String,
        val occupation: String,
        val link: String,
    ) : ProfileCardScreenUiState
}

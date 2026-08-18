package io.github.droidkaigi.confsched.feature.profilecard

sealed interface ProfileCardScreenAction {
    data class UpdateNickName(val nickName: String) : ProfileCardScreenAction
    data class UpdateOccupation(val occupation: String) : ProfileCardScreenAction
    data class UpdateLink(val link: String) : ProfileCardScreenAction
    data object Submit : ProfileCardScreenAction
}

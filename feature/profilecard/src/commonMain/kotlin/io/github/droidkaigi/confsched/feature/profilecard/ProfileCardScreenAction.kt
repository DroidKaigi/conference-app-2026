package io.github.droidkaigi.confsched.feature.profilecard

import io.github.droidkaigi.confsched.feature.profilecard.component.Mascot

sealed interface ProfileCardScreenAction {
    data class UpdateNickName(val nickName: String) : ProfileCardScreenAction
    data class UpdateOccupation(val occupation: String) : ProfileCardScreenAction
    data class UpdateLink(val link: String) : ProfileCardScreenAction
    data class UpdateMascot(val mascot: Mascot) : ProfileCardScreenAction
    data object Submit : ProfileCardScreenAction
}

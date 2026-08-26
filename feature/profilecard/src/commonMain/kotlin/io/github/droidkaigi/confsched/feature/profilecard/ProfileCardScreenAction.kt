package io.github.droidkaigi.confsched.feature.profilecard

import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.Sketchiness

sealed interface ProfileCardScreenAction {
    data class UpdateNickName(val nickName: String) : ProfileCardScreenAction

    data class UpdateOccupation(val occupation: String) : ProfileCardScreenAction

    data class UpdateLink(val link: String) : ProfileCardScreenAction

    data class UpdateMascot(val mascot: Mascot) : ProfileCardScreenAction

    data class UpdateSketchiness(val sketchiness: Sketchiness) : ProfileCardScreenAction

    data class UpdateAvatarImage(val avatarImage: AvatarImage) : ProfileCardScreenAction

    data object Submit : ProfileCardScreenAction

    data object FlipCard : ProfileCardScreenAction

    data object EditCard : ProfileCardScreenAction
}

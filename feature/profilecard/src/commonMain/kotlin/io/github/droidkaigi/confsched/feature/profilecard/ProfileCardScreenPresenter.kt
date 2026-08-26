package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel

@Composable
context(_: ProfileCardPresenterContext)
fun profileCardScreenPresenter(
    screenChannel: ScreenChannel<ProfileCardScreenAction, Nothing>,
): ProfileCardScreenUiState {
    var form by retain { mutableStateOf(ProfileCardScreenUiState.Form()) }
    var isSubmitted by retain { mutableStateOf(false) }
    var isShowingBack by retain { mutableStateOf(false) }

    ActionEffect(screenChannel) { action ->
        when (action) {
            is ProfileCardScreenAction.UpdateNickName -> form = form.copy(nickName = action.nickName)
            is ProfileCardScreenAction.UpdateOccupation -> form = form.copy(occupation = action.occupation)
            is ProfileCardScreenAction.UpdateLink -> form = form.copy(link = action.link)
            is ProfileCardScreenAction.UpdateMascot -> form = form.copy(mascot = action.mascot)
            is ProfileCardScreenAction.UpdateSketchiness -> form = form.copy(sketchiness = action.sketchiness)

            is ProfileCardScreenAction.UpdateAvatarImage -> form = form.copy(avatarImage = action.file)

            ProfileCardScreenAction.Submit -> isSubmitted = true

            ProfileCardScreenAction.FlipCard -> isShowingBack = !isShowingBack

            ProfileCardScreenAction.EditCard -> {
                isSubmitted = false
                isShowingBack = false
            }
        }
    }

    return if (isSubmitted) {
        ProfileCardScreenUiState.Card(
            nickName = form.nickName,
            occupation = form.occupation,
            link = form.link,
            mascot = form.mascot,
            sketchiness = form.sketchiness,
            avatarImage = form.avatarImage,
            isShowingBack = isShowingBack,
        )
    } else {
        form
    }
}

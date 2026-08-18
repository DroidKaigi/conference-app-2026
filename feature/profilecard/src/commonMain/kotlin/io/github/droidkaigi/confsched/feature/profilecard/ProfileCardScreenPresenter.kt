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

    ActionEffect(screenChannel) { action ->
        when (action) {
            is ProfileCardScreenAction.UpdateNickName -> form = form.copy(nickName = action.nickName)
            is ProfileCardScreenAction.UpdateOccupation -> form = form.copy(occupation = action.occupation)
            is ProfileCardScreenAction.UpdateLink -> form = form.copy(link = action.link)
            ProfileCardScreenAction.Submit -> isSubmitted = true
        }
    }

    return if (isSubmitted) {
        ProfileCardScreenUiState.Card(
            nickName = form.nickName,
            occupation = form.occupation,
            link = form.link,
        )
    } else {
        form
    }
}

package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel

@Composable
context(screenContext: ProfileCardScreenContext)
fun ProfileCardScreenRoot() {
    val screenChannel = retainScreenChannel<ProfileCardScreenAction, Nothing>()

    val uiState = context(screenContext.presenterContext) {
        profileCardScreenPresenter(screenChannel = screenChannel)
    }
    ProfileCardScreen(
        uiState = uiState,
        onNickNameChange = { screenChannel.send(ProfileCardScreenAction.UpdateNickName(it)) },
        onOccupationChange = { screenChannel.send(ProfileCardScreenAction.UpdateOccupation(it)) },
        onLinkChange = { screenChannel.send(ProfileCardScreenAction.UpdateLink(it)) },
        onMascotSelected = { screenChannel.send(ProfileCardScreenAction.UpdateMascot(it)) },
        onSubmitClick = { screenChannel.send(ProfileCardScreenAction.Submit) },
    )
}

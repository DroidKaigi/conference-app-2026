package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher

@Composable
context(screenContext: ProfileCardScreenContext)
fun ProfileCardScreenRoot() {
    val screenChannel = retainScreenChannel<ProfileCardScreenAction, Nothing>()
    val imagePickerLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        if (file != null) screenChannel.send(ProfileCardScreenAction.UpdateAvatarImage(file))
    }

    val uiState = context(screenContext.presenterContext) {
        profileCardScreenPresenter(screenChannel = screenChannel)
    }
    ProfileCardScreen(
        uiState = uiState,
        onNickNameChange = { screenChannel.send(ProfileCardScreenAction.UpdateNickName(it)) },
        onOccupationChange = { screenChannel.send(ProfileCardScreenAction.UpdateOccupation(it)) },
        onLinkChange = { screenChannel.send(ProfileCardScreenAction.UpdateLink(it)) },
        onMascotSelected = { screenChannel.send(ProfileCardScreenAction.UpdateMascot(it)) },
        onSketchinessSelected = { screenChannel.send(ProfileCardScreenAction.UpdateSketchiness(it)) },
        onAddImageClick = imagePickerLauncher::launch,
        onSubmitClick = { screenChannel.send(ProfileCardScreenAction.Submit) },
        onFlipCard = { screenChannel.send(ProfileCardScreenAction.FlipCard) },
        onEditCard = { screenChannel.send(ProfileCardScreenAction.EditCard) },
    )
}

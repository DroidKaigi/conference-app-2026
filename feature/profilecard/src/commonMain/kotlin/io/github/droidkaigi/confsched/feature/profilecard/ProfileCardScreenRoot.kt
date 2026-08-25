package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.feature.profilecard.component.AvatarImage
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch

@Composable
context(screenContext: ProfileCardScreenContext)
fun ProfileCardScreenRoot() {
    val screenChannel = retainScreenChannel<ProfileCardScreenAction, Nothing>()
    val coroutineScope = rememberCoroutineScope()
    val imagePickerLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        if (file != null) {
            coroutineScope.launch {
                screenChannel.send(ProfileCardScreenAction.UpdateAvatarImage(AvatarImage(file.readBytes())))
            }
        }
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

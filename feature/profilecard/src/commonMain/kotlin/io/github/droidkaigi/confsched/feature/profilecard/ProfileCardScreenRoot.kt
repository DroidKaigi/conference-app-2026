package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import io.github.droidkaigi.confsched.core.ui.rememberImagePicker
import io.github.droidkaigi.confsched.core.ui.rememberImageSharer
import soil.query.compose.rememberSubscription

@Composable
context(screenContext: ProfileCardScreenContext)
fun ProfileCardScreenRoot() {
    SoilDataBoundary(
        state1 = rememberSubscription(screenContext.profileCardSubscriptionKey),
        state2 = rememberSubscription(screenContext.appearanceSubscriptionKey),
    ) { storedCard, appearance ->
        val screenChannel = retainScreenChannel<ProfileCardScreenAction, ProfileCardScreenActionResult>()
        val snackbarHostState = LocalSnackbarHostState.current
        val launchImagePicker = rememberImagePicker { bytes ->
            screenChannel.send(ProfileCardScreenAction.UpdateAvatarImage(AvatarImage(bytes)))
        }
        val shareImage = rememberImageSharer()

        ActionResultEffect(screenChannel) { result ->
            when (result) {
                is ProfileCardScreenActionResult.ShowMessage -> snackbarHostState.showSnackbar(result.message.text)
                is ProfileCardScreenActionResult.ShareImage -> shareImage(result.image.pngBytes)
            }
        }

        val uiState = context(screenContext.presenterContext) {
            profileCardScreenPresenter(screenChannel = screenChannel, storedCard = storedCard)
        }
        ProfileCardScreen(
            uiState = uiState,
            colorScheme = appearance.colorScheme,
            onNickNameChange = { screenChannel.send(ProfileCardScreenAction.UpdateNickName(it)) },
            onOccupationChange = { screenChannel.send(ProfileCardScreenAction.UpdateOccupation(it)) },
            onLinkChange = { screenChannel.send(ProfileCardScreenAction.UpdateLink(it)) },
            onMascotClick = { screenChannel.send(ProfileCardScreenAction.UpdateMascot(it)) },
            onSketchinessClick = { screenChannel.send(ProfileCardScreenAction.UpdateSketchiness(it)) },
            onAddImageClick = launchImagePicker,
            onRemoveAvatarImageClick = { screenChannel.send(ProfileCardScreenAction.RemoveAvatarImage) },
            onSubmitClick = { screenChannel.send(ProfileCardScreenAction.Submit) },
            onCardClick = { screenChannel.send(ProfileCardScreenAction.FlipCard) },
            onEditClick = { screenChannel.send(ProfileCardScreenAction.EditCard) },
            onShareClick = { screenChannel.send(ProfileCardScreenAction.Share(it)) },
        )
    }
}

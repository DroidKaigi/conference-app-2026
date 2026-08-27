package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.MutationSuccessEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.ProfileCard
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: ProfileCardPresenterContext)
fun profileCardScreenPresenter(
    screenChannel: ScreenChannel<ProfileCardScreenAction, ProfileCardScreenActionResult>,
    storedCard: ProfileCard?,
): ProfileCardScreenUiState {
    val profileCardMutation = rememberMutation(presenterContext.profileCardMutationKey)
    val shareMutation = rememberMutation(presenterContext.shareProfileCardMutationKey)
    var form by retain { mutableStateOf(ProfileCardScreenUiState.Form()) }
    var isEditing by retain { mutableStateOf(false) }
    var isShowingBack by retain { mutableStateOf(false) }

    // EditCard reads the card of the composition it fires in rather than the one the effect was
    // launched with.
    val currentStoredCard by rememberUpdatedState(storedCard)

    ActionEffect(screenChannel) { action ->
        when (action) {
            is ProfileCardScreenAction.UpdateNickName -> form = form.copy(nickName = action.nickName, nickNameError = null)

            is ProfileCardScreenAction.UpdateOccupation -> form = form.copy(occupation = action.occupation, occupationError = null)

            is ProfileCardScreenAction.UpdateLink -> form = form.copy(link = action.link, linkError = null)

            is ProfileCardScreenAction.UpdateMascot -> form = form.copy(mascot = action.mascot)

            is ProfileCardScreenAction.UpdateSketchiness -> form = form.copy(sketchiness = action.sketchiness)

            is ProfileCardScreenAction.UpdateAvatarImage -> form = form.copy(avatarImage = action.avatarImage, avatarImageError = null)

            ProfileCardScreenAction.RemoveAvatarImage -> form = form.copy(avatarImage = null)

            ProfileCardScreenAction.Submit -> {
                val validated = form.validated()
                form = validated
                if (validated.hasNoError) {
                    profileCardMutation.mutateAsync(
                        ProfileCard(
                            nickName = validated.nickName,
                            occupation = validated.occupation,
                            link = validated.link,
                            mascot = validated.mascot,
                            sketchiness = validated.sketchiness,
                            avatarImage = validated.avatarImage,
                        ),
                    )
                }
            }

            ProfileCardScreenAction.FlipCard -> isShowingBack = !isShowingBack

            is ProfileCardScreenAction.Share -> shareMutation.mutateAsync(action.image)

            ProfileCardScreenAction.EditCard -> {
                form = currentStoredCard.toForm()
                isEditing = true
                isShowingBack = false
            }
        }
    }

    MutationSuccessEffect(profileCardMutation) {
        isEditing = false
        profileCardMutation.reset()
    }
    MutationErrorEffect(profileCardMutation) { error ->
        screenChannel.emit(ProfileCardScreenActionResult.ShowMessage(error.toUserMessage()))
        profileCardMutation.reset()
    }

    MutationSuccessEffect(shareMutation) { image ->
        screenChannel.emit(ProfileCardScreenActionResult.ShareImage(image))
        shareMutation.reset()
    }
    MutationErrorEffect(shareMutation) { error ->
        screenChannel.emit(ProfileCardScreenActionResult.ShowMessage(error.toUserMessage()))
        shareMutation.reset()
    }

    return if (storedCard == null || isEditing) {
        form.copy(isSubmitting = profileCardMutation.isPending)
    } else {
        ProfileCardScreenUiState.Card(
            nickName = storedCard.nickName,
            occupation = storedCard.occupation,
            link = storedCard.link,
            mascot = storedCard.mascot,
            sketchiness = storedCard.sketchiness,
            avatarImage = storedCard.avatarImage,
            isShowingBack = isShowingBack,
            isSharing = shareMutation.isPending,
        )
    }
}

private fun ProfileCard?.toForm(): ProfileCardScreenUiState.Form = if (this == null) {
    ProfileCardScreenUiState.Form()
} else {
    ProfileCardScreenUiState.Form(
        nickName = nickName,
        occupation = occupation,
        link = link,
        mascot = mascot,
        sketchiness = sketchiness,
        avatarImage = avatarImage,
    )
}

private fun ProfileCardScreenUiState.Form.validated(): ProfileCardScreenUiState.Form = copy(
    nickNameError = ProfileCardFormError.NickNameRequired.takeIf { nickName.isBlank() },
    occupationError = ProfileCardFormError.OccupationRequired.takeIf { occupation.isBlank() },
    linkError = when {
        link.isBlank() -> ProfileCardFormError.LinkRequired
        !LINK_PATTERN.matches(link) -> ProfileCardFormError.LinkMalformed
        else -> null
    },
    avatarImageError = ProfileCardFormError.AvatarImageRequired.takeIf { avatarImage == null },
)

private val ProfileCardScreenUiState.Form.hasNoError: Boolean
    get() = nickNameError == null &&
        occupationError == null &&
        linkError == null &&
        avatarImageError == null

private val LINK_PATTERN = Regex("""^https?://[^\s/?#.]+(?:\.[^\s/?#.]+)+(?:[/?#]\S*)?$""")

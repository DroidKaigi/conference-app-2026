package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.feature.profilecard.component.AvatarImage
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.avatar_image_required_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.link_invalid_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.link_required_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.nickname_required_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.occupation_required_error
import org.jetbrains.compose.resources.stringResource

// A single local profile card per device; there is no multi-account concept to key this by.
internal const val LOCAL_PROFILE_ID = "local"

@Composable
context(presenterContext: ProfileCardPresenterContext)
fun profileCardScreenPresenter(
    screenChannel: ScreenChannel<ProfileCardScreenAction, Nothing>,
): ProfileCardScreenUiState {
    var form by retain { mutableStateOf(ProfileCardScreenUiState.Form()) }
    var isSubmitted by retain { mutableStateOf(false) }
    var isShowingBack by retain { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        presenterContext.profileImageStore.loadImage(LOCAL_PROFILE_ID)?.let { bytes ->
            form = form.copy(avatarImage = AvatarImage(bytes))
        }
    }

    val nicknameRequiredMessage = stringResource(Res.string.nickname_required_error)
    val occupationRequiredMessage = stringResource(Res.string.occupation_required_error)
    val linkRequiredMessage = stringResource(Res.string.link_required_error)
    val linkInvalidMessage = stringResource(Res.string.link_invalid_error)
    val avatarImageRequiredMessage = stringResource(Res.string.avatar_image_required_error)

    ActionEffect(screenChannel) { action ->
        when (action) {
            is ProfileCardScreenAction.UpdateNickName -> form = form.copy(nickName = action.nickName, nickNameErrorMessage = null)

            is ProfileCardScreenAction.UpdateOccupation -> form = form.copy(occupation = action.occupation, occupationErrorMessage = null)

            is ProfileCardScreenAction.UpdateLink -> form = form.copy(link = action.link, linkErrorMessage = null)

            is ProfileCardScreenAction.UpdateMascot -> form = form.copy(mascot = action.mascot)

            is ProfileCardScreenAction.UpdateSketchiness -> form = form.copy(sketchiness = action.sketchiness)

            is ProfileCardScreenAction.UpdateAvatarImage -> {
                form = form.copy(avatarImage = action.image, avatarImageErrorMessage = null)
                presenterContext.profileImageStore.saveImage(LOCAL_PROFILE_ID, action.image.bytes)
            }

            ProfileCardScreenAction.Submit -> {
                val validated = form.copy(
                    nickNameErrorMessage = nicknameRequiredMessage.takeIf { form.nickName.isBlank() },
                    occupationErrorMessage = occupationRequiredMessage.takeIf { form.occupation.isBlank() },
                    linkErrorMessage = when {
                        form.link.isBlank() -> linkRequiredMessage
                        !form.link.isValidLink() -> linkInvalidMessage
                        else -> null
                    },
                    avatarImageErrorMessage = avatarImageRequiredMessage.takeIf { form.avatarImage == null },
                )
                form = validated
                if (validated.hasNoErrors) isSubmitted = true
            }

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

private val ProfileCardScreenUiState.Form.hasNoErrors: Boolean
    get() = nickNameErrorMessage == null &&
        occupationErrorMessage == null &&
        linkErrorMessage == null &&
        avatarImageErrorMessage == null

private val linkRegex = Regex("^(?:https?://)?(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z0-9-]{2,}(?:/\\S*)?$")

private fun String.isValidLink(): Boolean = linkRegex.matches(this)

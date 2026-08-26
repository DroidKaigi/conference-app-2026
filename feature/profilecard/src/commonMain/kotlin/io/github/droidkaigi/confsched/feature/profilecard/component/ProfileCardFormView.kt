package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.Add
import io.github.droidkaigi.confsched.core.designsystem.icon.Check
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.core.ui.KaigiOutlinedButton
import io.github.droidkaigi.confsched.core.ui.KaigiTextField
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardFormError
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardScreenUiState
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.add_image_button
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.create_card_button
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.link_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.link_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.link_malformed_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.link_placeholder
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.mascot_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.nickname_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.nickname_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.nickname_placeholder
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.occupation_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.occupation_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.occupation_placeholder
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.profile_image_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.profile_image_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.sketchiness_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.subtitle
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileCardFormView(
    uiState: ProfileCardScreenUiState.Form,
    onNickNameChange: (String) -> Unit,
    onOccupationChange: (String) -> Unit,
    onLinkChange: (String) -> Unit,
    onMascotSelected: (Mascot) -> Unit,
    onSketchinessSelected: (Sketchiness) -> Unit,
    onAddImageClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 16.dp + LocalNavigationBarOccupiedHeight.current),
        verticalArrangement = Arrangement.spacedBy(ProfileCardFormViewDefaults.sectionSpacing),
    ) {
        Text(stringResource(Res.string.subtitle), style = MaterialTheme.typography.bodyMedium)
        ProfileCardFormSection(label = stringResource(Res.string.nickname_label)) {
            KaigiTextField(
                value = uiState.nickName,
                onValueChange = onNickNameChange,
                placeholder = stringResource(Res.string.nickname_placeholder),
                seed = ProfileCardFormViewDefaults.nickNameFieldSeed,
                keyboardOptions = KeyboardOptions.Default,
                isError = uiState.nickNameError != null,
            )
            ProfileCardFormErrorText(uiState.nickNameError)
        }
        ProfileCardFormSection(label = stringResource(Res.string.occupation_label)) {
            KaigiTextField(
                value = uiState.occupation,
                onValueChange = onOccupationChange,
                placeholder = stringResource(Res.string.occupation_placeholder),
                seed = ProfileCardFormViewDefaults.occupationFieldSeed,
                keyboardOptions = KeyboardOptions.Default,
                isError = uiState.occupationError != null,
            )
            ProfileCardFormErrorText(uiState.occupationError)
        }
        ProfileCardFormSection(label = stringResource(Res.string.link_label)) {
            KaigiTextField(
                value = uiState.link,
                onValueChange = onLinkChange,
                placeholder = stringResource(Res.string.link_placeholder),
                seed = ProfileCardFormViewDefaults.linkFieldSeed,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                isError = uiState.linkError != null,
            )
            ProfileCardFormErrorText(uiState.linkError)
        }
        ProfileCardFormSection(label = stringResource(Res.string.profile_image_label)) {
            KaigiOutlinedButton(onClick = onAddImageClick, seed = ProfileCardFormViewDefaults.addImageButtonSeed) {
                Icon(
                    imageVector = if (uiState.avatarImage != null) KaigiIcons.Default.Check else KaigiIcons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(KaigiButtonDefaults.iconSize),
                )
                Text(stringResource(Res.string.add_image_button), style = KaigiButtonDefaults.labelStyle)
            }
            ProfileCardFormErrorText(uiState.avatarImageError)
        }
        ProfileCardFormSection(label = stringResource(Res.string.mascot_label)) {
            MascotPicker(selected = uiState.mascot, onMascotSelected = onMascotSelected)
        }
        ProfileCardFormSection(label = stringResource(Res.string.sketchiness_label)) {
            SketchinessPicker(selected = uiState.sketchiness, onSketchinessSelected = onSketchinessSelected)
        }
        KaigiButton(
            onClick = onSubmitClick,
            seed = ProfileCardFormViewDefaults.submitButtonSeed,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSubmitting,
        ) {
            Text(stringResource(Res.string.create_card_button), style = KaigiButtonDefaults.labelStyle)
        }
    }
}

@Composable
private fun ProfileCardFormSection(
    label: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(ProfileCardFormViewDefaults.labelSpacing)) {
        Text(text = label, style = ProfileCardFormViewDefaults.labelStyle)
        content()
    }
}

@Composable
private fun ProfileCardFormErrorText(error: ProfileCardFormError?) {
    if (error == null) return
    Text(
        text = stringResource(error.message),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
    )
}

private val ProfileCardFormError.message: StringResource
    get() = when (this) {
        ProfileCardFormError.NickNameRequired -> Res.string.nickname_error
        ProfileCardFormError.OccupationRequired -> Res.string.occupation_error
        ProfileCardFormError.LinkRequired -> Res.string.link_error
        ProfileCardFormError.LinkMalformed -> Res.string.link_malformed_error
        ProfileCardFormError.AvatarImageRequired -> Res.string.profile_image_error
    }

private object ProfileCardFormViewDefaults {
    val sectionSpacing = 12.dp
    val labelSpacing = 6.dp
    val nickNameFieldSeed = 701
    val occupationFieldSeed = 702
    val linkFieldSeed = 703
    val addImageButtonSeed = 710
    val submitButtonSeed = 720

    // The design sets every field label in the display face, which the type scale reserves for
    // headline and display roles.
    val labelStyle: TextStyle
        @Composable get() = MaterialTheme.typography.titleSmall.copy(
            fontFamily = MaterialTheme.typography.displaySmall.fontFamily,
        )
}

@LocalePreviews
@Composable
private fun ProfileCardFormViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardFormView(
            uiState = ProfileCardScreenUiState.Form(),
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotSelected = {},
            onSketchinessSelected = {},
            onAddImageClick = {},
            onSubmitClick = {},
        )
    }
}

@LocalePreviews
@Composable
private fun ProfileCardFormViewErrorPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardFormView(
            uiState = ProfileCardScreenUiState.Form(
                nickNameError = ProfileCardFormError.NickNameRequired,
                occupationError = ProfileCardFormError.OccupationRequired,
                linkError = ProfileCardFormError.LinkRequired,
                avatarImageError = ProfileCardFormError.AvatarImageRequired,
            ),
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotSelected = {},
            onSketchinessSelected = {},
            onAddImageClick = {},
            onSubmitClick = {},
        )
    }
}

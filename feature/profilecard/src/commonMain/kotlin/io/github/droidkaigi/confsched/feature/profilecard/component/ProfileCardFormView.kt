package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.Add
import io.github.droidkaigi.confsched.core.designsystem.icon.Close
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.ByteArrayImage
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.core.ui.KaigiOutlinedButton
import io.github.droidkaigi.confsched.core.ui.KaigiTextField
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.core.ui.SketchEllipseShape
import io.github.droidkaigi.confsched.core.ui.encodeToPng
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardFormError
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardScreenUiState
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.add_image_button
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.create_card_button
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.link_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.link_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.link_malformed_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.mascot_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.nickname_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.nickname_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.occupation_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.occupation_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.profile_image_error
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.profile_image_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.remove_image_button
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
    onMascotClick: (Mascot) -> Unit,
    onSketchinessClick: (Sketchiness) -> Unit,
    onAddImageClick: () -> Unit,
    onRemoveAvatarImageClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ProfileCardFormViewDefaults.contentInset)
            .padding(
                top = ProfileCardFormViewDefaults.contentInset,
                bottom = ProfileCardFormViewDefaults.contentInset + LocalNavigationBarOccupiedHeight.current,
            ),
        verticalArrangement = Arrangement.spacedBy(ProfileCardFormViewDefaults.sectionSpacing),
    ) {
        Text(stringResource(Res.string.subtitle), style = MaterialTheme.typography.bodyMedium)
        ProfileCardFormSection(label = stringResource(Res.string.nickname_label)) {
            KaigiTextField(
                value = uiState.nickName,
                onValueChange = onNickNameChange,
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
                seed = ProfileCardFormViewDefaults.linkFieldSeed,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                isError = uiState.linkError != null,
            )
            ProfileCardFormErrorText(uiState.linkError)
        }
        ProfileCardFormSection(label = stringResource(Res.string.profile_image_label)) {
            val avatarImage = uiState.avatarImage
            if (avatarImage == null) {
                KaigiOutlinedButton(onClick = onAddImageClick, seed = ProfileCardFormViewDefaults.addImageButtonSeed) {
                    Icon(
                        imageVector = KaigiIcons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(KaigiButtonDefaults.iconSize),
                    )
                    Text(stringResource(Res.string.add_image_button), style = ProfileCardTextStyles.accent)
                }
            } else {
                PickedAvatarImageItem(
                    avatarImage = avatarImage,
                    sketchiness = uiState.sketchiness,
                    onImageClick = onAddImageClick,
                    onRemoveClick = onRemoveAvatarImageClick,
                )
            }
            ProfileCardFormErrorText(uiState.avatarImageError)
        }
        ProfileCardFormSection(label = stringResource(Res.string.mascot_label)) {
            MascotPicker(selectedMascot = uiState.mascot, onMascotClick = onMascotClick)
        }
        ProfileCardFormSection(label = stringResource(Res.string.sketchiness_label)) {
            SketchinessPicker(selectedSketchiness = uiState.sketchiness, onSketchinessClick = onSketchinessClick)
        }
        KaigiButton(
            onClick = onSubmitClick,
            seed = ProfileCardFormViewDefaults.submitButtonSeed,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSubmitting,
        ) {
            Text(stringResource(Res.string.create_card_button), style = ProfileCardTextStyles.accent)
        }
    }
}

/**
 * The picked photograph, centre-cropped into the same wobbly mat the card front sets it in, with
 * the contour over it and a close button to discard it. Tapping the photograph re-opens the picker.
 */
@Composable
private fun PickedAvatarImageItem(
    avatarImage: AvatarImage,
    sketchiness: Sketchiness,
    onImageClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val previewSize = ProfileCardFormViewDefaults.imagePreviewSize
    val shape = SketchEllipseShape(
        seed = ProfileCardFormViewDefaults.imagePreviewSeed,
        roughness = profileCardRoughness(previewSize, sketchiness),
        tremor = profileCardTremor(previewSize, sketchiness),
        sweepWavelength = ProfileCardSweepWavelength,
        borderThickness = ProfileCardFormViewDefaults.matBorderThickness,
    )
    Box(modifier = modifier.size(previewSize)) {
        ByteArrayImage(
            bytes = avatarImage.bytes,
            contentDescription = stringResource(Res.string.profile_image_label),
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceBright)
                .clickable(onClick = onImageClick),
            contentScale = ContentScale.Crop,
        )
        // Drawn over the photograph rather than under it, so the whole stroke reads at its width.
        Box(modifier = Modifier.fillMaxSize().sketchBorder(shape, MaterialTheme.colorScheme.onSurface))
        RemoveImageButton(
            onClick = onRemoveClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = ProfileCardFormViewDefaults.removeButtonOffset.x, y = ProfileCardFormViewDefaults.removeButtonOffset.y),
        )
    }
}

@Composable
private fun RemoveImageButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val ringShape = SketchEllipseShape(
        seed = ProfileCardFormViewDefaults.removeButtonSeed,
        roughness = ProfileCardFormViewDefaults.removeButtonRoughness,
        tremor = ProfileCardFormViewDefaults.removeButtonTremor,
        sweepWavelength = ProfileCardSweepWavelength,
        borderThickness = ProfileCardFormViewDefaults.ringBorderThickness,
    )
    Box(
        modifier = modifier
            .size(ProfileCardFormViewDefaults.removeButtonHitTarget)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(ProfileCardFormViewDefaults.removeButtonRingSize)
                .clip(ringShape)
                .background(MaterialTheme.colorScheme.surfaceBright)
                .sketchBorder(ringShape, MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = KaigiIcons.Default.Close,
                contentDescription = stringResource(Res.string.remove_image_button),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(ProfileCardFormViewDefaults.removeButtonGlyphSize),
            )
        }
    }
}

@Composable
private fun ProfileCardFormSection(
    label: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(ProfileCardFormViewDefaults.labelSpacing)) {
        Text(text = label, style = ProfileCardTextStyles.accent)
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
    val contentInset = 16.dp
    val sectionSpacing = 12.dp
    val labelSpacing = 6.dp
    val nickNameFieldSeed = 701
    val occupationFieldSeed = 702
    val linkFieldSeed = 703
    val addImageButtonSeed = 710
    val imagePreviewSeed = 711
    val removeButtonSeed = 712
    val submitButtonSeed = 720
    val imagePreviewSize = 96.dp
    val matBorderThickness = 1.5.dp
    val ringBorderThickness = 1.5.dp
    val removeButtonHitTarget = 40.dp
    val removeButtonRingSize = 30.dp
    val removeButtonGlyphSize = 20.dp
    val removeButtonRoughness = 0.4.dp
    val removeButtonTremor = 0.15.dp

    // The ring is centred on the mat's contour at 45 degrees, which leaves its hit target half a
    // dp outside the top-end corner of the box the mat fills.
    val removeButtonOffset = DpOffset(0.5.dp, (-0.5).dp)
}

/**
 * The photograph the previews stand in for, drawn rather than shipped as a file: the same face the
 * card front falls back to, over the sample skin tone the design file uses for its own mock image.
 */
internal fun sampleAvatarImage(): AvatarImage {
    val side = 96
    val bitmap = ImageBitmap(side, side)
    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(bitmap), Size(side.toFloat(), side.toFloat())) {
        drawRect(SampleAvatarSkin)
        drawPlaceholderFace(SampleAvatarInk)
    }
    return AvatarImage(bitmap.encodeToPng())
}

private val SampleAvatarSkin = Color(0xFFE8D8B8)

private val SampleAvatarInk = Color(0xFF3B2F1F)

@LocalePreviews
@Composable
private fun ProfileCardFormViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardFormView(
            uiState = ProfileCardScreenUiState.Form(
                nickName = "Speaker A",
                occupation = "Software Engineer",
                link = "https://example.com/user",
            ),
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotClick = {},
            onSketchinessClick = {},
            onAddImageClick = {},
            onRemoveAvatarImageClick = {},
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
            onMascotClick = {},
            onSketchinessClick = {},
            onAddImageClick = {},
            onRemoveAvatarImageClick = {},
            onSubmitClick = {},
        )
    }
}

@LocalePreviews
@Composable
private fun ProfileCardFormViewWithImagePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardFormView(
            uiState = ProfileCardScreenUiState.Form(
                nickName = "Speaker A",
                occupation = "Software Engineer",
                link = "https://example.com/user",
                avatarImage = sampleAvatarImage(),
            ),
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotClick = {},
            onSketchinessClick = {},
            onAddImageClick = {},
            onRemoveAvatarImageClick = {},
            onSubmitClick = {},
        )
    }
}

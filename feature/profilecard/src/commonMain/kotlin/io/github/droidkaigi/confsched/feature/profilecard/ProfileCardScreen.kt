package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.common.SystemBackEffect
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.PaperGrain
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.feature.profilecard.component.ProfileCardFormView
import io.github.droidkaigi.confsched.feature.profilecard.component.ProfileCardView
import io.github.droidkaigi.confsched.feature.profilecard.component.sampleAvatarImage
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.profile_card
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileCardScreen(
    uiState: ProfileCardScreenUiState,
    colorScheme: KaigiColorScheme,
    onNickNameChange: (String) -> Unit,
    onOccupationChange: (String) -> Unit,
    onLinkChange: (String) -> Unit,
    onMascotClick: (Mascot) -> Unit,
    onSketchinessClick: (Sketchiness) -> Unit,
    onPaperGrainClick: (PaperGrain) -> Unit,
    onAddImageClick: () -> Unit,
    onRemoveAvatarImageClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: (ImageBitmap) -> Unit,
    onStartDoodlingClick: () -> Unit,
    onCancelDoodlingClick: () -> Unit,
    onDoodlesDoneClick: (Doodle, Doodle) -> Unit,
) {
    SystemBackEffect(
        enabled = uiState is ProfileCardScreenUiState.Card && uiState.isDoodling,
        onBack = onCancelDoodlingClick,
    )
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { KaigiTopAppBar(title = stringResource(Res.string.profile_card)) },
        contentWindowInsets = WindowInsets(),
    ) { innerPadding ->
        when (uiState) {
            is ProfileCardScreenUiState.Form -> ProfileCardFormView(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                onNickNameChange = onNickNameChange,
                onOccupationChange = onOccupationChange,
                onLinkChange = onLinkChange,
                onMascotClick = onMascotClick,
                onSketchinessClick = onSketchinessClick,
                onPaperGrainClick = onPaperGrainClick,
                onAddImageClick = onAddImageClick,
                onRemoveAvatarImageClick = onRemoveAvatarImageClick,
                onSubmitClick = onSubmitClick,
            )

            is ProfileCardScreenUiState.Card -> ProfileCardView(
                uiState = uiState,
                colorScheme = colorScheme,
                onCardClick = onCardClick,
                onEditClick = onEditClick,
                onShareClick = onShareClick,
                onStartDoodlingClick = onStartDoodlingClick,
                onDoodlesDoneClick = onDoodlesDoneClick,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

// A screen preview names its own frame; the side-by-side doodle layout needs one wider than a phone's.
private val ExpandedScreenPreviewSize = DpSize(width = 1000.dp, height = 800.dp)

@LocaleScreenPreviews
@Composable
private fun ProfileCardScreenFormPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardScreen(
            uiState = ProfileCardScreenUiState.Form(
                nickName = "Speaker A",
                occupation = "Software Engineer",
                link = "https://example.com/user",
            ),
            colorScheme = colorScheme,
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotClick = {},
            onSketchinessClick = {},
            onPaperGrainClick = {},
            onAddImageClick = {},
            onRemoveAvatarImageClick = {},
            onSubmitClick = {},
            onCardClick = {},
            onEditClick = {},
            onShareClick = {},
            onStartDoodlingClick = {},
            onCancelDoodlingClick = {},
            onDoodlesDoneClick = { _, _ -> },
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun ProfileCardScreenFormErrorPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardScreen(
            uiState = ProfileCardScreenUiState.Form(
                nickNameError = ProfileCardFormError.NickNameRequired,
                occupationError = ProfileCardFormError.OccupationRequired,
                linkError = ProfileCardFormError.LinkRequired,
                avatarImageError = ProfileCardFormError.AvatarImageRequired,
            ),
            colorScheme = colorScheme,
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotClick = {},
            onSketchinessClick = {},
            onPaperGrainClick = {},
            onAddImageClick = {},
            onRemoveAvatarImageClick = {},
            onSubmitClick = {},
            onCardClick = {},
            onEditClick = {},
            onShareClick = {},
            onStartDoodlingClick = {},
            onCancelDoodlingClick = {},
            onDoodlesDoneClick = { _, _ -> },
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun ProfileCardScreenCardPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardScreen(
            uiState = sampleCardUiState(isShowingBack = false, isDoodling = false),
            colorScheme = colorScheme,
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotClick = {},
            onSketchinessClick = {},
            onPaperGrainClick = {},
            onAddImageClick = {},
            onRemoveAvatarImageClick = {},
            onSubmitClick = {},
            onCardClick = {},
            onEditClick = {},
            onShareClick = {},
            onStartDoodlingClick = {},
            onCancelDoodlingClick = {},
            onDoodlesDoneClick = { _, _ -> },
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun ProfileCardScreenCardBackPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardScreen(
            uiState = sampleCardUiState(isShowingBack = true, isDoodling = false),
            colorScheme = colorScheme,
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotClick = {},
            onSketchinessClick = {},
            onPaperGrainClick = {},
            onAddImageClick = {},
            onRemoveAvatarImageClick = {},
            onSubmitClick = {},
            onCardClick = {},
            onEditClick = {},
            onShareClick = {},
            onStartDoodlingClick = {},
            onCancelDoodlingClick = {},
            onDoodlesDoneClick = { _, _ -> },
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun ProfileCardScreenDoodlingPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardScreen(
            uiState = sampleCardUiState(isShowingBack = false, isDoodling = true),
            colorScheme = colorScheme,
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotClick = {},
            onSketchinessClick = {},
            onPaperGrainClick = {},
            onAddImageClick = {},
            onRemoveAvatarImageClick = {},
            onSubmitClick = {},
            onCardClick = {},
            onEditClick = {},
            onShareClick = {},
            onStartDoodlingClick = {},
            onCancelDoodlingClick = {},
            onDoodlesDoneClick = { _, _ -> },
        )
    }
}

@LocalePreviews
@Composable
private fun ProfileCardScreenDoodlingSideBySidePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Box(modifier = Modifier.size(ExpandedScreenPreviewSize)) {
            ProfileCardScreen(
                uiState = sampleCardUiState(isShowingBack = false, isDoodling = true),
                colorScheme = colorScheme,
                onNickNameChange = {},
                onOccupationChange = {},
                onLinkChange = {},
                onMascotClick = {},
                onSketchinessClick = {},
                onPaperGrainClick = {},
                onAddImageClick = {},
                onRemoveAvatarImageClick = {},
                onSubmitClick = {},
                onCardClick = {},
                onEditClick = {},
                onShareClick = {},
                onStartDoodlingClick = {},
                onCancelDoodlingClick = {},
                onDoodlesDoneClick = { _, _ -> },
            )
        }
    }
}

@LocaleScreenPreviews
@Composable
private fun ProfileCardScreenFormWithImagePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardScreen(
            uiState = ProfileCardScreenUiState.Form(
                nickName = "Speaker A",
                occupation = "Software Engineer",
                link = "https://example.com/user",
                avatarImage = sampleAvatarImage(),
            ),
            colorScheme = colorScheme,
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotClick = {},
            onSketchinessClick = {},
            onPaperGrainClick = {},
            onAddImageClick = {},
            onRemoveAvatarImageClick = {},
            onSubmitClick = {},
            onCardClick = {},
            onEditClick = {},
            onShareClick = {},
            onStartDoodlingClick = {},
            onCancelDoodlingClick = {},
            onDoodlesDoneClick = { _, _ -> },
        )
    }
}

private fun sampleCardUiState(
    isShowingBack: Boolean,
    isDoodling: Boolean,
) = ProfileCardScreenUiState.Card(
    nickName = "Speaker A",
    occupation = "Software Engineer",
    link = "https://example.com/user",
    mascot = Mascot.C,
    sketchiness = Sketchiness.Normal,
    paperGrain = PaperGrain.Smooth,
    avatarImage = null,
    frontDoodle = Doodle.fakeOnCardFace(),
    backDoodle = if (isShowingBack) Doodle.fakeOnCardFace() else Doodle.Empty,
    isShowingBack = isShowingBack,
    isDoodling = isDoodling,
)

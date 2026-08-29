package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
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
    onAddImageClick: () -> Unit,
    onRemoveAvatarImageClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: (ImageBitmap) -> Unit,
) {
    Scaffold(
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
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@LocaleScreenPreviews
@Composable
private fun ProfileCardScreenFormPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardScreen(
            colorScheme = colorScheme,
            onShareClick = {},
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
            onCardClick = {},
            onEditClick = {},
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
            onAddImageClick = {},
            onRemoveAvatarImageClick = {},
            onSubmitClick = {},
            onCardClick = {},
            onEditClick = {},
            onShareClick = {},
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
            uiState = ProfileCardScreenUiState.Card(
                nickName = "Speaker A",
                occupation = "Software Engineer",
                link = "https://example.com/user",
                mascot = Mascot.Koala,
                sketchiness = Sketchiness.Normal,
                avatarImage = null,
            ),
            colorScheme = colorScheme,
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotClick = {},
            onSketchinessClick = {},
            onAddImageClick = {},
            onRemoveAvatarImageClick = {},
            onSubmitClick = {},
            onCardClick = {},
            onEditClick = {},
            onShareClick = {},
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
            colorScheme = colorScheme,
            onShareClick = {},
            uiState = ProfileCardScreenUiState.Card(
                nickName = "Speaker A",
                occupation = "Software Engineer",
                link = "https://example.com/user",
                mascot = Mascot.Koala,
                sketchiness = Sketchiness.Normal,
                avatarImage = null,
                isShowingBack = true,
            ),
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotClick = {},
            onSketchinessClick = {},
            onAddImageClick = {},
            onRemoveAvatarImageClick = {},
            onSubmitClick = {},
            onCardClick = {},
            onEditClick = {},
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun ProfileCardScreenFormWithImagePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardScreen(
            colorScheme = colorScheme,
            onShareClick = {},
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
            onCardClick = {},
            onEditClick = {},
        )
    }
}

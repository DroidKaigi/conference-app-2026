package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.feature.profilecard.component.Mascot
import io.github.droidkaigi.confsched.feature.profilecard.component.ProfileCardBack
import io.github.droidkaigi.confsched.feature.profilecard.component.ProfileCardFormView
import io.github.droidkaigi.confsched.feature.profilecard.component.ProfileCardFront
import io.github.droidkaigi.confsched.feature.profilecard.component.Sketchiness
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.edit_button
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.profile_card
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.share_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileCardScreen(
    uiState: ProfileCardScreenUiState,
    onNickNameChange: (String) -> Unit,
    onOccupationChange: (String) -> Unit,
    onLinkChange: (String) -> Unit,
    onMascotSelected: (Mascot) -> Unit,
    onSketchinessSelected: (Sketchiness) -> Unit,
    onAddImageClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onFlipCard: () -> Unit,
    onEditCard: () -> Unit,
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
                onMascotSelected = onMascotSelected,
                onSketchinessSelected = onSketchinessSelected,
                onAddImageClick = onAddImageClick,
                onSubmitClick = onSubmitClick,
            )

            is ProfileCardScreenUiState.Card -> Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (uiState.isShowingBack) {
                    ProfileCardBack(
                        nickName = uiState.nickName,
                        mascot = uiState.mascot,
                        sketchiness = uiState.sketchiness,
                        modifier = Modifier.clickable(onClick = onFlipCard),
                    )
                } else {
                    ProfileCardFront(
                        nickName = uiState.nickName,
                        occupation = uiState.occupation,
                        mascot = uiState.mascot,
                        sketchiness = uiState.sketchiness,
                        avatarImage = uiState.avatarImage,
                        modifier = Modifier.clickable(onClick = onFlipCard),
                    )
                }
                KaigiButton(
                    onClick = {},
                    seed = ProfileCardScreenDefaults.shareButtonSeed,
                    containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f),
                    contentColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(Res.string.share_button), style = KaigiButtonDefaults.labelStyle)
                }
                TextButton(onClick = onEditCard) {
                    Text(stringResource(Res.string.edit_button))
                }
            }
        }
    }
}

private object ProfileCardScreenDefaults {
    val shareButtonSeed = 730
}

@LocaleScreenPreviews
@Composable
private fun ProfileCardScreenFormPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardScreen(
            uiState = ProfileCardScreenUiState.Form(),
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotSelected = {},
            onSketchinessSelected = {},
            onAddImageClick = {},
            onSubmitClick = {},
            onFlipCard = {},
            onEditCard = {},
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
                link = "https://example.com",
                mascot = Mascot.Koala,
                sketchiness = Sketchiness.Normal,
                avatarImage = null,
            ),
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotSelected = {},
            onSketchinessSelected = {},
            onAddImageClick = {},
            onSubmitClick = {},
            onFlipCard = {},
            onEditCard = {},
        )
    }
}

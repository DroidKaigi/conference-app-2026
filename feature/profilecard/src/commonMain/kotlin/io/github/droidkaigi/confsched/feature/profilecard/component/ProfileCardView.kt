package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Share
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardScreenUiState
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.edit_button
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.share_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileCardView(
    uiState: ProfileCardScreenUiState.Card,
    onFlipCard: () -> Unit,
    onEditCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState())
                .padding(ProfileCardViewDefaults.cardSpacePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ProfileCardViewDefaults.cardSpacing),
        ) {
            if (uiState.isShowingBack) {
                ProfileCardBack(
                    nickName = uiState.nickName,
                    link = uiState.link,
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
            ProfileCardActions(onEditCard = onEditCard)
        }
    }
}

@Composable
private fun ProfileCardActions(onEditCard: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ProfileCardViewDefaults.actionsInset),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        KaigiButton(
            onClick = {},
            seed = ProfileCardViewDefaults.shareButtonSeed,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = KaigiIcons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(KaigiButtonDefaults.iconSize),
            )
            Text(stringResource(Res.string.share_button), style = ProfileCardTextStyles.accent)
        }
        Text(
            text = stringResource(Res.string.edit_button),
            style = ProfileCardTextStyles.accent,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onEditCard)
                // The design sets the label straight under the button with no chrome of its own;
                // the padding is what the row is spaced by.
                .padding(vertical = ProfileCardViewDefaults.actionSpacing),
        )
    }
}

private object ProfileCardViewDefaults {
    val shareButtonSeed = 730
    val cardSpacePadding = 24.dp
    val cardSpacing = 24.dp
    val actionsInset = 24.dp
    val actionSpacing = 12.dp
}

@LocalePreviews
@Composable
private fun ProfileCardViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardView(
            uiState = ProfileCardScreenUiState.Card(
                nickName = "Speaker A",
                occupation = "Software Engineer",
                link = "https://example.com/user",
                mascot = Mascot.Koala,
                sketchiness = Sketchiness.Normal,
                avatarImage = null,
            ),
            onFlipCard = {},
            onEditCard = {},
        )
    }
}

@LocalePreviews
@Composable
private fun ProfileCardActionsPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardActions(onEditCard = {})
    }
}

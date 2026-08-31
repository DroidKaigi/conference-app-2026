package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Share
import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.core.ui.RecordedOffScreen
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardScreenUiState
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.edit_button
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.share_button
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileCardView(
    uiState: ProfileCardScreenUiState.Card,
    colorScheme: KaigiColorScheme,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: (ImageBitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shareImageLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ProfileCardViewDefaults.cardSpacePadding)
                .padding(bottom = LocalNavigationBarOccupiedHeight.current),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ProfileCardViewDefaults.cardSpacing, Alignment.CenterVertically),
        ) {
            FlippableProfileCard(
                nickName = uiState.nickName,
                occupation = uiState.occupation,
                link = uiState.link,
                mascot = uiState.mascot,
                sketchiness = uiState.sketchiness,
                avatarImage = uiState.avatarImage,
                isShowingBack = uiState.isShowingBack,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .clickable(onClick = onCardClick),
            )
            ProfileCardActionsSection(
                isSharing = uiState.isSharing,
                onShareClick = { coroutineScope.launch { onShareClick(shareImageLayer.toImageBitmap()) } },
                onEditClick = onEditClick,
            )
        }
        // Recorded regardless of which face is turned up, so the share image always carries both.
        RecordedOffScreen(layer = shareImageLayer) { recordingModifier ->
            ProfileCardShareImage(
                nickName = uiState.nickName,
                occupation = uiState.occupation,
                link = uiState.link,
                mascot = uiState.mascot,
                sketchiness = uiState.sketchiness,
                avatarImage = uiState.avatarImage,
                colorScheme = colorScheme,
                modifier = recordingModifier,
            )
        }
    }
}

/**
 * The card turned over about its vertical axis. The back face is laid out already mirrored so
 * that, once turned past the edge, it reads the right way round and its outline lands exactly on
 * the front's.
 */
@Composable
private fun FlippableProfileCard(
    nickName: String,
    occupation: String,
    link: String,
    mascot: Mascot,
    sketchiness: Sketchiness,
    avatarImage: AvatarImage?,
    isShowingBack: Boolean,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isShowingBack) 180f else 0f,
        animationSpec = tween(durationMillis = ProfileCardViewDefaults.flipDurationMillis, easing = FastOutSlowInEasing),
    )
    val showsBack = rotation > 90f
    Box(
        modifier = modifier.graphicsLayer {
            rotationY = rotation
            cameraDistance = ProfileCardViewDefaults.flipCameraDistance * density
        },
    ) {
        if (showsBack) {
            ProfileCardBack(
                nickName = nickName,
                link = link,
                mascot = mascot,
                sketchiness = sketchiness,
                taped = false,
                modifier = Modifier.graphicsLayer { rotationY = 180f },
            )
        } else {
            ProfileCardFront(
                nickName = nickName,
                occupation = occupation,
                mascot = mascot,
                sketchiness = sketchiness,
                taped = false,
                avatarImage = avatarImage,
            )
        }
    }
}

@Composable
private fun ProfileCardActionsSection(
    isSharing: Boolean,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .widthIn(max = ProfileCardFaceDefaults.size.width)
            .fillMaxWidth()
            .padding(horizontal = ProfileCardViewDefaults.actionsInset),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        KaigiButton(
            onClick = onShareClick,
            seed = ProfileCardViewDefaults.shareButtonSeed,
            enabled = !isSharing,
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
                .clickable(role = Role.Button, onClick = onEditClick)
                // The design sets the label straight under the button with no chrome of its own;
                // the padding is what the row is spaced by.
                .padding(vertical = ProfileCardViewDefaults.actionSpacing),
        )
    }
}

private object ProfileCardViewDefaults {
    val shareButtonSeed = 730
    val flipDurationMillis = 500
    val flipCameraDistance = 12f
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
                mascot = Mascot.C,
                sketchiness = Sketchiness.Normal,
                avatarImage = null,
            ),
            colorScheme = colorScheme,
            onCardClick = {},
            onEditClick = {},
            onShareClick = {},
        )
    }
}

@LocalePreviews
@Composable
private fun ProfileCardActionsSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardActionsSection(isSharing = false, onShareClick = {}, onEditClick = {})
    }
}

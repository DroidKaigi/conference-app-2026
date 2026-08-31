package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.Edit
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Share
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.core.ui.KaigiOutlinedButton
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardFaceDefaults
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardTextStyles
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.doodle_button
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.edit_button
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.share_button
import org.jetbrains.compose.resources.stringResource

internal const val PROFILE_CARD_VIEW_SHARE_BUTTON_TEST_TAG = "ProfileCardViewShareButtonTestTag"
internal const val PROFILE_CARD_VIEW_DOODLE_BUTTON_TEST_TAG = "ProfileCardViewDoodleButtonTestTag"
internal const val PROFILE_CARD_VIEW_EDIT_BUTTON_TEST_TAG = "ProfileCardViewEditButtonTestTag"

/** What the finished card offers: share it, draw on it, or go back to the form that made it. */
@Composable
internal fun ProfileCardActionsSection(
    isSharing: Boolean,
    onShareClick: () -> Unit,
    onDoodleClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .widthIn(max = ProfileCardFaceDefaults.size.width)
            .fillMaxWidth()
            .padding(horizontal = ProfileCardActionsDefaults.inset),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ProfileCardActionsDefaults.spacing),
    ) {
        KaigiButton(
            onClick = onShareClick,
            seed = ProfileCardActionsDefaults.shareButtonSeed,
            enabled = !isSharing,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(PROFILE_CARD_VIEW_SHARE_BUTTON_TEST_TAG),
        ) {
            Icon(
                imageVector = KaigiIcons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(KaigiButtonDefaults.iconSize),
            )
            Text(stringResource(Res.string.share_button), style = ProfileCardTextStyles.accent)
            // Balances the leading icon: the row spaces both sides of the label alike, so a
            // spacer of the icon's width lands the label on the button's center.
            Spacer(modifier = Modifier.width(KaigiButtonDefaults.iconSize))
        }
        KaigiOutlinedButton(
            onClick = onDoodleClick,
            seed = ProfileCardActionsDefaults.doodleButtonSeed,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(PROFILE_CARD_VIEW_DOODLE_BUTTON_TEST_TAG),
        ) {
            Icon(
                imageVector = KaigiIcons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(KaigiButtonDefaults.iconSize),
            )
            Text(stringResource(Res.string.doodle_button), style = ProfileCardTextStyles.accent)
        }
        Text(
            text = stringResource(Res.string.edit_button),
            style = ProfileCardTextStyles.accent,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(PROFILE_CARD_VIEW_EDIT_BUTTON_TEST_TAG)
                .clickable(role = Role.Button, onClick = onEditClick)
                // The design sets the label straight under the buttons with no chrome of its own;
                // the padding is what gives it a reachable height.
                .padding(vertical = ProfileCardActionsDefaults.spacing),
        )
    }
}

private object ProfileCardActionsDefaults {
    val inset = 24.dp
    val spacing = 12.dp
    val shareButtonSeed = 730
    val doodleButtonSeed = 731
}

@LocalePreviews
@Composable
private fun ProfileCardActionsSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardActionsSection(
            isSharing = false,
            onShareClick = {},
            onDoodleClick = {},
            onEditClick = {},
        )
    }
}

@LocalePreviews
@Composable
private fun ProfileCardActionsSectionSharingPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardActionsSection(
            isSharing = true,
            onShareClick = {},
            onDoodleClick = {},
            onEditClick = {},
        )
    }
}

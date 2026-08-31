package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.Check
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.DoodlePenSize
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.DoodleFlipButton
import io.github.droidkaigi.confsched.core.ui.DoodlePenSizeRow
import io.github.droidkaigi.confsched.core.ui.DoodleStrokeControlsRow
import io.github.droidkaigi.confsched.core.ui.DoodleStrokeControlsSpacing
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardFaceDefaults
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardTextStyles
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.doodle_done
import org.jetbrains.compose.resources.stringResource

/**
 * The controls a card being drawn on offers. [sideBySide] says both faces are on screen at once, so
 * each face takes its own Undo and Clear and there is no face to switch to.
 */
@Composable
internal fun ProfileCardDoodleControlsSection(
    penSize: DoodlePenSize,
    isShowingBack: Boolean,
    sideBySide: Boolean,
    canEditFront: Boolean,
    canEditBack: Boolean,
    onPenSizeClick: (DoodlePenSize) -> Unit,
    onFlipClick: () -> Unit,
    onFrontUndoClick: () -> Unit,
    onFrontClearClick: () -> Unit,
    onBackUndoClick: () -> Unit,
    onBackClearClick: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .widthIn(max = ProfileCardDoodleControlsDefaults.maxWidth)
            .fillMaxWidth()
            .padding(horizontal = ProfileCardDoodleControlsDefaults.inset),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ProfileCardDoodleControlsDefaults.spacing),
    ) {
        DoodlePenSizeRow(selectedPenSize = penSize, onPenSizeClick = onPenSizeClick)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DoodleStrokeControlsSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (sideBySide) {
                DoodleStrokeControlsRow(
                    canEdit = canEditFront,
                    onUndoClick = onFrontUndoClick,
                    onClearClick = onFrontClearClick,
                    modifier = Modifier.weight(1f),
                )
                DoodleStrokeControlsRow(
                    canEdit = canEditBack,
                    onUndoClick = onBackUndoClick,
                    onClearClick = onBackClearClick,
                    modifier = Modifier.weight(1f),
                )
            } else {
                DoodleFlipButton(isShowingBack = isShowingBack, onClick = onFlipClick)
                DoodleStrokeControlsRow(
                    canEdit = if (isShowingBack) canEditBack else canEditFront,
                    onUndoClick = if (isShowingBack) onBackUndoClick else onFrontUndoClick,
                    onClearClick = if (isShowingBack) onBackClearClick else onFrontClearClick,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        KaigiButton(
            onClick = onDoneClick,
            seed = ProfileCardDoodleControlsDefaults.doneButtonSeed,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = KaigiIcons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(KaigiButtonDefaults.iconSize),
            )
            Text(stringResource(Res.string.doodle_done), style = ProfileCardTextStyles.accent)
        }
    }
}

private object ProfileCardDoodleControlsDefaults {
    val inset = 24.dp
    val spacing = 12.dp

    /** Two faces' worth of controls, so the block may grow past one card face's width. */
    val maxWidth = ProfileCardFaceDefaults.size.width * 2
    val doneButtonSeed = 732
}

@LocalePreviews
@Composable
private fun ProfileCardDoodleControlsSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardDoodleControlsSection(
            penSize = DoodlePenSize.Normal,
            isShowingBack = false,
            sideBySide = false,
            canEditFront = true,
            canEditBack = false,
            onPenSizeClick = {},
            onFlipClick = {},
            onFrontUndoClick = {},
            onFrontClearClick = {},
            onBackUndoClick = {},
            onBackClearClick = {},
            onDoneClick = {},
        )
    }
}

@LocalePreviews
@Composable
private fun ProfileCardDoodleControlsSectionSideBySidePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardDoodleControlsSection(
            penSize = DoodlePenSize.Thick,
            isShowingBack = false,
            sideBySide = true,
            canEditFront = true,
            canEditBack = true,
            onPenSizeClick = {},
            onFlipClick = {},
            onFrontUndoClick = {},
            onFrontClearClick = {},
            onBackUndoClick = {},
            onBackClearClick = {},
            onDoneClick = {},
        )
    }
}

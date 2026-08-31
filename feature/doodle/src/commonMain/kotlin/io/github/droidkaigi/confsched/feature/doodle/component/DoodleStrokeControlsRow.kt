package io.github.droidkaigi.confsched.feature.doodle.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.core.ui.KaigiOutlinedButton
import io.github.droidkaigi.confsched.feature.doodle.DoodleEditControlSpacing
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.Res
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_clear
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_undo
import org.jetbrains.compose.resources.stringResource

/** Undo and Clear for one canvas, both idle while [canEdit] says that canvas has nothing on it. */
@Composable
internal fun DoodleStrokeControlsRow(
    canEdit: Boolean,
    onUndoClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DoodleEditControlSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KaigiOutlinedButton(
            onClick = onUndoClick,
            seed = UNDO_BUTTON_SEED,
            modifier = Modifier.weight(1f),
            enabled = canEdit,
        ) {
            Text(stringResource(Res.string.doodle_undo), style = KaigiButtonDefaults.labelStyle)
        }
        KaigiOutlinedButton(
            onClick = onClearClick,
            seed = CLEAR_BUTTON_SEED,
            modifier = Modifier.weight(1f),
            enabled = canEdit,
        ) {
            Text(stringResource(Res.string.doodle_clear), style = KaigiButtonDefaults.labelStyle)
        }
    }
}

private const val UNDO_BUTTON_SEED = 4311
private const val CLEAR_BUTTON_SEED = 4312

@LocalePreviews
@Composable
private fun DoodleStrokeControlsRowPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleStrokeControlsRow(
            canEdit = true,
            onUndoClick = {},
            onClearClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

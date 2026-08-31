package io.github.droidkaigi.confsched.feature.doodle.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.DoodleCanvasView
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.feature.doodle.DoodleCanvasMaxScale
import io.github.droidkaigi.confsched.feature.doodle.DoodleEditContentPadding
import io.github.droidkaigi.confsched.feature.doodle.DoodleEditControlSpacing
import io.github.droidkaigi.confsched.feature.doodle.DoodleEditSectionSpacing
import io.github.droidkaigi.confsched.feature.doodle.DoodleScreenUiState
import io.github.droidkaigi.confsched.feature.doodle.doodleOrigin
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.Res
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_save
import io.github.droidkaigi.confsched.feature.doodle.haloColor
import io.github.droidkaigi.confsched.feature.doodle.inkColor
import io.github.droidkaigi.confsched.feature.doodle.referenceSize
import org.jetbrains.compose.resources.stringResource

/**
 * The About hero's wall, which carries a single drawing: the strokes being edited are transient and
 * only Save hands them to the data layer, so an edit always starts from the saved doodle again.
 */
@Composable
internal fun DoodleWallEditorView(
    uiState: DoodleScreenUiState.Wall,
    onSaveClick: (Doodle) -> Unit,
    modifier: Modifier = Modifier,
) {
    val savedStrokes = uiState.savedDoodle.strokes
    val strokes = remember(savedStrokes, savedStrokes::toMutableStateList)
    val target = DoodleTarget.AboutWall
    Column(
        modifier = modifier.padding(DoodleEditContentPadding),
        verticalArrangement = Arrangement.spacedBy(DoodleEditSectionSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DoodleCanvasView(
            doodle = Doodle(strokes = strokes.toList()),
            referenceSize = target.referenceSize,
            maxScale = DoodleCanvasMaxScale,
            origin = target.doodleOrigin,
            inkColor = target.inkColor,
            haloColor = target.haloColor,
            onStrokeAdd = { strokes += it },
            modifier = Modifier.fillMaxWidth().weight(1f),
            background = { scale ->
                DoodleUnderlayView(
                    target = target,
                    card = null,
                    scale = scale,
                    modifier = Modifier.matchParentSize(),
                )
            },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DoodleEditControlSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DoodleStrokeControlsRow(
                canEdit = strokes.isNotEmpty(),
                onUndoClick = { strokes.removeAt(strokes.lastIndex) },
                onClearClick = strokes::clear,
                modifier = Modifier.weight(2f),
            )
            KaigiButton(
                onClick = { onSaveClick(Doodle(strokes = strokes.toList())) },
                seed = SAVE_BUTTON_SEED,
                modifier = Modifier.weight(1f),
                enabled = !uiState.isSaving,
            ) {
                Text(stringResource(Res.string.doodle_save), style = KaigiButtonDefaults.labelStyle)
            }
        }
    }
}

private const val SAVE_BUTTON_SEED = 4313

@LocalePreviews
@Composable
private fun DoodleWallEditorViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleWallEditorView(
            uiState = DoodleScreenUiState.Wall(savedDoodle = Doodle.fake(), isSaving = false),
            onSaveClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

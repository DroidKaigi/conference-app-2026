package io.github.droidkaigi.confsched.feature.about.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.Check
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodlePenSize
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.AboutHeroHeight
import io.github.droidkaigi.confsched.core.ui.AboutHeroSize
import io.github.droidkaigi.confsched.core.ui.AboutHeroStageTopInset
import io.github.droidkaigi.confsched.core.ui.AboutHeroStageWidth
import io.github.droidkaigi.confsched.core.ui.DoodleCanvasView
import io.github.droidkaigi.confsched.core.ui.DoodleOrigin
import io.github.droidkaigi.confsched.core.ui.DoodlePenSizeRow
import io.github.droidkaigi.confsched.core.ui.DoodleStrokeControlsRow
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.core.ui.rememberAboutHeroStage
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.doodle_done
import org.jetbrains.compose.resources.stringResource

/**
 * The wall in the hero's place, turned into a drawing surface with its controls under it. The
 * strokes being edited are transient: only Done hands them over, so an edit always starts from
 * [savedDoodle] again.
 */
@Composable
internal fun AboutWallDoodleEditorView(
    savedDoodle: Doodle,
    onDoneClick: (Doodle) -> Unit,
    modifier: Modifier = Modifier,
) {
    var penSize by rememberSerializable { mutableStateOf(DoodlePenSize.Normal) }
    var draft by rememberSerializable(savedDoodle) { mutableStateOf(savedDoodle) }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AboutWallDoodleEditorDefaults.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DoodleCanvasView(
            doodle = draft,
            referenceSize = AboutHeroSize,
            maxScale = AboutWallDoodleEditorDefaults.maxScale,
            origin = DoodleOrigin.TopCenter,
            inkColor = MaterialTheme.colorScheme.onPrimary,
            haloColor = null,
            penSize = penSize,
            onStrokeAdd = { draft = Doodle(strokes = draft.strokes + it) },
            modifier = Modifier.fillMaxWidth().height(AboutHeroHeight),
            background = { scale -> AboutWallHintView(scale = scale, modifier = Modifier.matchParentSize()) },
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AboutWallDoodleEditorDefaults.inset),
            verticalArrangement = Arrangement.spacedBy(AboutWallDoodleEditorDefaults.spacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DoodlePenSizeRow(selectedPenSize = penSize, onPenSizeClick = { penSize = it })
            DoodleStrokeControlsRow(
                canEdit = draft.strokes.isNotEmpty(),
                onUndoClick = { draft = Doodle(strokes = draft.strokes.dropLast(1)) },
                onClearClick = { draft = Doodle.Empty },
                modifier = Modifier.fillMaxWidth(),
            )
            KaigiButton(
                onClick = { onDoneClick(draft) },
                seed = AboutWallDoodleEditorDefaults.doneButtonSeed,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = KaigiIcons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(KaigiButtonDefaults.iconSize),
                )
                Text(stringResource(Res.string.doodle_done), style = KaigiButtonDefaults.labelStyle)
            }
        }
    }
}

/** The wall behind the strokes, with the stage art faded to read as a hint rather than as content. */
@Composable
private fun AboutWallHintView(scale: Float, modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.primary)) {
        Image(
            imageVector = rememberAboutHeroStage(),
            contentDescription = null,
            alpha = AboutWallDoodleEditorDefaults.stageHintAlpha,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = AboutHeroStageTopInset * scale)
                .fillMaxWidth()
                .widthIn(max = AboutHeroStageWidth * scale),
        )
    }
}

private object AboutWallDoodleEditorDefaults {
    val spacing = 12.dp
    val inset = 24.dp
    val stageHintAlpha = 0.35f
    val doneButtonSeed = 5575

    /** The wall never enlarges past what the hero itself is drawn at plus a half. */
    val maxScale = 1.5f
}

@LocalePreviews
@Composable
private fun AboutWallDoodleEditorViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutWallDoodleEditorView(savedDoodle = Doodle.fake(), onDoneClick = {})
    }
}

@LocalePreviews
@Composable
private fun AboutWallDoodleEditorViewEmptyPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutWallDoodleEditorView(savedDoodle = Doodle.Empty, onDoneClick = {})
    }
}

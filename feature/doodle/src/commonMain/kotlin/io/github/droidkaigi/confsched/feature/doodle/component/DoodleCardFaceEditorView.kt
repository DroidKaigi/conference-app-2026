package io.github.droidkaigi.confsched.feature.doodle.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleStroke
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.DoodleCanvasView
import io.github.droidkaigi.confsched.feature.doodle.DoodleCanvasMaxScale
import io.github.droidkaigi.confsched.feature.doodle.DoodleCardFace
import io.github.droidkaigi.confsched.feature.doodle.DoodleEditControlSpacing
import io.github.droidkaigi.confsched.feature.doodle.PlaceholderProfileCard
import io.github.droidkaigi.confsched.feature.doodle.doodleOrigin
import io.github.droidkaigi.confsched.feature.doodle.haloColor
import io.github.droidkaigi.confsched.feature.doodle.inkColor
import io.github.droidkaigi.confsched.feature.doodle.referenceSize

/** One card face's canvas with the Undo and Clear that act on that face alone. */
@Composable
internal fun DoodleCardFaceEditorView(
    face: DoodleCardFace,
    doodle: Doodle,
    card: ProfileCard?,
    onStrokeAdd: (DoodleStroke) -> Unit,
    onUndoClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val target = face.target
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DoodleEditControlSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DoodleCanvasView(
            doodle = doodle,
            referenceSize = target.referenceSize,
            maxScale = DoodleCanvasMaxScale,
            origin = target.doodleOrigin,
            inkColor = target.inkColor,
            haloColor = target.haloColor,
            onStrokeAdd = onStrokeAdd,
            modifier = Modifier.fillMaxWidth().weight(1f),
            background = { scale ->
                DoodleUnderlayView(
                    target = target,
                    card = card,
                    scale = scale,
                    modifier = Modifier.matchParentSize(),
                )
            },
        ) { scale ->
            DoodleOverlayView(
                target = target,
                card = card,
                scale = scale,
                modifier = Modifier.matchParentSize(),
            )
        }
        DoodleStrokeControlsRow(
            canEdit = doodle.strokes.isNotEmpty(),
            onUndoClick = onUndoClick,
            onClearClick = onClearClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@LocalePreviews
@Composable
private fun DoodleCardFaceEditorViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleCardFaceEditorView(
            face = DoodleCardFace.Back,
            doodle = Doodle.fakeOnCardFace(),
            card = PlaceholderProfileCard,
            onStrokeAdd = {},
            onUndoClick = {},
            onClearClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

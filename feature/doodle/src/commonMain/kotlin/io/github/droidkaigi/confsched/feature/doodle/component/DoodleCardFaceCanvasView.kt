package io.github.droidkaigi.confsched.feature.doodle.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodlePenSize
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
import io.github.droidkaigi.confsched.feature.doodle.PlaceholderProfileCard
import io.github.droidkaigi.confsched.feature.doodle.doodleOrigin
import io.github.droidkaigi.confsched.feature.doodle.haloColor
import io.github.droidkaigi.confsched.feature.doodle.inkColor
import io.github.droidkaigi.confsched.feature.doodle.referenceSize

/** One card face's canvas, drawn over what that face shows the doodle against. */
@Composable
internal fun DoodleCardFaceCanvasView(
    face: DoodleCardFace,
    doodle: Doodle,
    card: ProfileCard?,
    penSize: DoodlePenSize,
    onStrokeAdd: (DoodleStroke) -> Unit,
    modifier: Modifier = Modifier,
) {
    val target = face.target
    DoodleCanvasView(
        doodle = doodle,
        referenceSize = target.referenceSize,
        maxScale = DoodleCanvasMaxScale,
        origin = target.doodleOrigin,
        inkColor = target.inkColor,
        haloColor = target.haloColor,
        penSize = penSize,
        onStrokeAdd = onStrokeAdd,
        modifier = modifier,
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
}

@LocalePreviews
@Composable
private fun DoodleCardFaceCanvasViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleCardFaceCanvasView(
            face = DoodleCardFace.Back,
            doodle = Doodle.fakeOnCardFace(),
            card = PlaceholderProfileCard,
            penSize = DoodlePenSize.Normal,
            onStrokeAdd = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

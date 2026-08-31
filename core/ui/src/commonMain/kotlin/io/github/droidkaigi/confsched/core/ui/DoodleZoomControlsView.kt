package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.ZoomIn
import io.github.droidkaigi.confsched.core.designsystem.icon.ZoomOut
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.reset_zoom
import io.github.droidkaigi.confsched.core.ui.generated.resources.zoom_in
import io.github.droidkaigi.confsched.core.ui.generated.resources.zoom_out
import org.jetbrains.compose.resources.stringResource

/**
 * The zoom a [DoodleCanvasView] offers where a pinch is unavailable, reading [zoom] to disable the
 * control that would do nothing: zooming in at the far end, and zooming out or returning to actual
 * size while already there.
 */
@Composable
internal fun DoodleZoomControlsView(
    zoom: Float,
    onZoomInClick: () -> Unit,
    onZoomOutClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DoodleZoomControlsSpacing),
    ) {
        KaigiIconButton(
            seed = ZOOM_IN_BUTTON_SEED,
            onClick = onZoomInClick,
            enabled = zoom < DoodleCanvasTransform.MAX_ZOOM,
        ) {
            Icon(KaigiIcons.Default.ZoomIn, contentDescription = stringResource(Res.string.zoom_in))
        }
        KaigiIconButton(
            seed = ZOOM_OUT_BUTTON_SEED,
            onClick = onZoomOutClick,
            enabled = zoom > DoodleCanvasTransform.MIN_ZOOM,
        ) {
            Icon(KaigiIcons.Default.ZoomOut, contentDescription = stringResource(Res.string.zoom_out))
        }
        KaigiIconButton(
            seed = RESET_ZOOM_BUTTON_SEED,
            onClick = onResetClick,
            enabled = zoom > DoodleCanvasTransform.MIN_ZOOM,
        ) {
            Icon(DoodleZoomResetIcon, contentDescription = stringResource(Res.string.reset_zoom))
        }
    }
}

internal val DoodleZoomControlsSpacing = 6.dp

private const val ZOOM_IN_BUTTON_SEED = 4321
private const val ZOOM_OUT_BUTTON_SEED = 4322
private const val RESET_ZOOM_BUTTON_SEED = 4323

// The hand-drawn set is exported from the design file and holds no return-to-actual-size glyph, so
// this one is drawn here: a frame with the smaller frame it collapses back to, at the stroke weight
// and cap the exported icons carry.
private val DoodleZoomResetIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "DoodleZoomReset",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        addPath(
            pathData = PathParser().parsePathString(
                "M3.6 4.5C3.45 8.1 3.5 15.4 3.75 19.2 " +
                    "C7.5 19.5 15.9 19.45 20.3 19.15 " +
                    "C20.6 15.3 20.55 8.2 20.25 4.7 " +
                    "C16.4 4.4 7.8 4.4 3.6 4.5Z",
            ).toNodes(),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        addPath(
            pathData = PathParser().parsePathString(
                "M8.7 9.2C8.6 10.7 8.65 13.1 8.8 14.7 " +
                    "C10.5 14.9 13.4 14.85 15.2 14.65 " +
                    "C15.4 13.05 15.35 10.6 15.15 9.3 " +
                    "C13.4 9.1 10.4 9.1 8.7 9.2Z",
            ).toNodes(),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }.build()
}

@LocalePreviews
@Composable
private fun DoodleZoomControlsViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
        ) {
            DoodleZoomControlsView(
                zoom = 2f,
                onZoomInClick = {},
                onZoomOutClick = {},
                onResetClick = {},
            )
        }
    }
}

@LocalePreviews
@Composable
private fun DoodleZoomControlsViewActualSizePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
        ) {
            DoodleZoomControlsView(
                zoom = DoodleCanvasTransform.MIN_ZOOM,
                onZoomInClick = {},
                onZoomOutClick = {},
                onResetClick = {},
            )
        }
    }
}

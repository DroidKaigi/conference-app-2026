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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.ZoomIn
import io.github.droidkaigi.confsched.core.designsystem.icon.ZoomOut
import io.github.droidkaigi.confsched.core.designsystem.icon.ZoomReset
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
            Icon(KaigiIcons.Default.ZoomReset, contentDescription = stringResource(Res.string.reset_zoom))
        }
    }
}

internal val DoodleZoomControlsSpacing = 6.dp

private const val ZOOM_IN_BUTTON_SEED = 4321
private const val ZOOM_OUT_BUTTON_SEED = 4322
private const val RESET_ZOOM_BUTTON_SEED = 4323

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

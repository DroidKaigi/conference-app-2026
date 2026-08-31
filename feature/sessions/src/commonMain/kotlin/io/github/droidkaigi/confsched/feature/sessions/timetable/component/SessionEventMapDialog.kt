package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.ScreenshotTestExclude
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchCard
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.event_map_1f
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.event_map_b1f
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.event_map_content_description
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SessionEventMapDialog(floor: Floor, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        FloorMapCard(floor)
    }
}

@Composable
private fun FloorMapCard(
    selectedFloor: Floor,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = selectedFloor,
        modifier = modifier,
    ) { targetFloor ->
        SketchCard(
            color = Color.White,
        ) {
            Image(
                painter = painterResource(targetFloor.mapImage()),
                contentDescription = stringResource(Res.string.event_map_content_description, targetFloor.label),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

private fun Floor.mapImage(): DrawableResource = when (this) {
    Floor.Ground -> Res.drawable.event_map_1f
    Floor.Basement -> Res.drawable.event_map_b1f
}

// The Dialog opens a composition root of its own, which the screenshot capture cannot reach.
@ScreenshotTestExclude
@LocalePreviews
@Composable
private fun SessionEventMapDialogPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SessionEventMapDialog(
            floor = Floor.Basement,
            onDismiss = {},
        )
    }
}

@LocalePreviews
@Composable
private fun FloorMapCardPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FloorMapCard(
            selectedFloor = Floor.Ground,
        )
    }
}

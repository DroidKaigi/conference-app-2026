package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import io.github.droidkaigi.confsched.core.ui.mapPainter
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.event_map_content_description
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SessionEventMapDialog(floor: Floor, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Row {
            Spacer(Modifier.size(20.dp))
            Text(
                text = floor.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        FloorMapCard(floor)
    }
}

@Composable
private fun FloorMapCard(selectedFloor: Floor) {
    SketchCard(color = MaterialTheme.colorScheme.primary) {
        Image(
            painter = selectedFloor.mapPainter(),
            contentDescription = stringResource(Res.string.event_map_content_description, selectedFloor.label),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        )
    }
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

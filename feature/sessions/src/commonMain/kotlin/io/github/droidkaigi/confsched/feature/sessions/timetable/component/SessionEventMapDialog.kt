package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.droidkaigi.confsched.core.designsystem.icon.Close
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.ScreenshotTestExclude
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiIconButton
import io.github.droidkaigi.confsched.core.ui.KaigiIconButtonDefaults
import io.github.droidkaigi.confsched.core.ui.SketchCard
import io.github.droidkaigi.confsched.core.ui.mapPainter
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.close_event_map
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.event_map_content_description
import org.jetbrains.compose.resources.stringResource

internal fun sessionEventMapImageTestTag(floor: Floor): String = "SessionEventMapImageTestTag:${floor.name}"

@Composable
internal fun SessionEventMapDialog(floor: Floor, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        FloorMapCard(floor, onDismiss)
    }
}

@Composable
private fun FloorMapCard(floor: Floor, onDismiss: () -> Unit) {
    SketchCard(color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp),
            ) {
                Spacer(Modifier.size(KaigiIconButtonDefaults.size))
                Text(
                    text = floor.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
                KaigiIconButton(seed = 0, onClick = onDismiss) {
                    Icon(
                        imageVector = KaigiIcons.Default.Close,
                        contentDescription = stringResource(Res.string.close_event_map),
                    )
                }
            }
            Image(
                painter = floor.mapPainter(),
                contentDescription = stringResource(Res.string.event_map_content_description, floor.label),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(sessionEventMapImageTestTag(floor)),
            )
        }
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
            floor = Floor.Ground,
            onDismiss = {},
        )
    }
}

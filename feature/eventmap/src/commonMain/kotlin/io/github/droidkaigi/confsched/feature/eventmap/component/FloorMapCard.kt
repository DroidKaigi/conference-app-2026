package io.github.droidkaigi.confsched.feature.eventmap.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchCard
import io.github.droidkaigi.confsched.core.ui.mapPainter
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.Res
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_content_description
import org.jetbrains.compose.resources.stringResource
import kotlin.math.absoluteValue

private val FloorToggleThreshold = 100.dp

@Composable
internal fun FloorMapCard(
    selectedFloor: Floor,
    onFloorToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnFloorToggle by rememberUpdatedState(onFloorToggle)
    Crossfade(
        targetState = selectedFloor,
        modifier = modifier.pointerInput(Unit) {
            var dragAmount = 0F
            detectHorizontalDragGestures(
                onDragStart = { dragAmount = 0F },
                onHorizontalDrag = { _, amount -> dragAmount += amount },
                onDragEnd = {
                    if (dragAmount.absoluteValue > FloorToggleThreshold.toPx()) {
                        currentOnFloorToggle()
                    }
                },
            )
        },
    ) { targetFloor ->
        SketchCard(
            color = Color.White,
        ) {
            Image(
                painter = targetFloor.mapPainter(),
                contentDescription = stringResource(Res.string.event_map_content_description, targetFloor.label),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            )
        }
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
            onFloorToggle = {},
        )
    }
}

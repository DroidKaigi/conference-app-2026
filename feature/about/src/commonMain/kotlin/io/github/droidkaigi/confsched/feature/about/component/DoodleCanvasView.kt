package io.github.droidkaigi.confsched.feature.about.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodlePoint
import io.github.droidkaigi.confsched.core.model.DoodleStroke
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.androidSystemGestureExclusion
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.sketchBorder

/**
 * The wall as a drawing surface: [doodle] is what the user has drawn so far, and each finished
 * drag arrives at [onStrokeAdd] in the hero's own dp space.
 */
@Composable
internal fun DoodleCanvasView(
    doodle: Doodle,
    onStrokeAdd: (DoodleStroke) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        // The hero draws the doodle at scale 1, so a taller canvas holds that scale rather than
        // enlarging strokes the hero will then show smaller.
        val scale = (maxHeight / AboutHeroHeight).coerceAtMost(1f)
        val points = remember { mutableStateListOf<DoodlePoint>() }
        val currentOnStrokeAdd by rememberUpdatedState(onStrokeAdd)
        val commitStroke: () -> Unit = {
            if (points.isNotEmpty()) {
                currentOnStrokeAdd(DoodleStroke(points.toList()))
                points.clear()
            }
        }
        val shape = SketchRoundRectShape(seed = combineSketchSeed(CANVAS_SEED), borderThickness = 1.5.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AboutHeroHeight * scale)
                .clip(shape)
                .background(MaterialTheme.colorScheme.primary)
                .sketchBorder(shape, MaterialTheme.colorScheme.onPrimary)
                .androidSystemGestureExclusion()
                .pointerInput(scale) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            points.clear()
                            points.add(offset.toDoodlePoint(size.width, density * scale))
                        },
                        onDrag = { change, _ -> points.add(change.position.toDoodlePoint(size.width, density * scale)) },
                        onDragEnd = commitStroke,
                        onDragCancel = commitStroke,
                    )
                },
        ) {
            Image(
                imageVector = rememberAboutHeroStage(),
                contentDescription = null,
                alpha = STAGE_HINT_ALPHA,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = AboutHeroStageTopInset * scale)
                    .fillMaxWidth()
                    .widthIn(max = AboutHeroStageWidth * scale),
            )
            DoodleLayerView(
                doodle = doodle,
                color = MaterialTheme.colorScheme.onPrimary,
                scale = scale,
                modifier = Modifier.matchParentSize(),
            )
            DoodleLayerView(
                doodle = Doodle(strokes = listOf(DoodleStroke(points.toList()))),
                color = MaterialTheme.colorScheme.onPrimary,
                scale = scale,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}

private fun Offset.toDoodlePoint(widthPx: Int, unit: Float): DoodlePoint =
    DoodlePoint(x = (x - widthPx / 2f) / unit, y = y / unit)

private const val CANVAS_SEED = 4213
private const val STAGE_HINT_ALPHA = 0.35f

@Preview
@Composable
private fun DoodleCanvasViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleCanvasView(
            doodle = Doodle.fake(),
            onStrokeAdd = {},
            modifier = Modifier.fillMaxWidth().height(AboutHeroHeight),
        )
    }
}

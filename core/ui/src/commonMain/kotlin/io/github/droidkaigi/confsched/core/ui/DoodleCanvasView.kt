package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodlePoint
import io.github.droidkaigi.confsched.core.model.DoodleStroke
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

/**
 * A drawing surface standing in for the surface a doodle ends up on: [doodle] is what the user has
 * drawn so far, and each finished drag arrives at [onStrokeAdd] in [referenceSize]'s dp space,
 * anchored at [origin]. The surface is laid out at [referenceSize] scaled to fit, up to [maxScale],
 * and a stroke is stored back in the target's own dp space. [background] renders the underlay the
 * user draws over and [overlay] whatever must stay above the strokes; both receive that scale.
 */
@Composable
fun DoodleCanvasView(
    doodle: Doodle,
    referenceSize: DpSize,
    maxScale: Float,
    origin: DoodleOrigin,
    inkColor: Color,
    haloColor: Color?,
    onStrokeAdd: (DoodleStroke) -> Unit,
    modifier: Modifier = Modifier,
    background: @Composable BoxScope.(scale: Float) -> Unit = {},
    overlay: @Composable BoxScope.(scale: Float) -> Unit = {},
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val scale = minOf(maxWidth / referenceSize.width, maxHeight / referenceSize.height).coerceAtMost(maxScale)
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
                .size(referenceSize * scale)
                .clip(shape)
                .sketchBorder(shape, inkColor)
                .androidSystemGestureExclusion()
                .pointerInput(scale, origin) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            points.clear()
                            points.add(offset.toDoodlePoint(origin, size.width, density * scale))
                        },
                        onDrag = { change, _ ->
                            points.add(change.position.toDoodlePoint(origin, size.width, density * scale))
                        },
                        onDragEnd = commitStroke,
                        onDragCancel = commitStroke,
                    )
                },
        ) {
            background(scale)
            DoodleLayerView(
                doodle = doodle,
                color = inkColor,
                haloColor = haloColor,
                origin = origin,
                scale = scale,
                modifier = Modifier.matchParentSize(),
            )
            DoodleLayerView(
                doodle = Doodle(strokes = listOf(DoodleStroke(points.toList()))),
                color = inkColor,
                haloColor = haloColor,
                origin = origin,
                scale = scale,
                modifier = Modifier.matchParentSize(),
            )
            overlay(scale)
        }
    }
}

private fun Offset.toDoodlePoint(origin: DoodleOrigin, widthPx: Int, unit: Float): DoodlePoint =
    DoodlePoint(x = (x - origin.originX(widthPx.toFloat())) / unit, y = y / unit)

private const val CANVAS_SEED = 4213

@Preview
@Composable
private fun DoodleCanvasViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleCanvasView(
            doodle = Doodle.fake(),
            referenceSize = AboutHeroSize,
            maxScale = 1f,
            origin = DoodleOrigin.TopCenter,
            inkColor = MaterialTheme.colorScheme.onPrimary,
            haloColor = null,
            onStrokeAdd = {},
            modifier = Modifier.size(AboutHeroSize),
            background = { Box(modifier = Modifier.matchParentSize().background(MaterialTheme.colorScheme.primary)) },
        )
    }
}

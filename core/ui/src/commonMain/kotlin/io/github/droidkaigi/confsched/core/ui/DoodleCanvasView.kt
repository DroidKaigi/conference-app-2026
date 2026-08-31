package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleInk
import io.github.droidkaigi.confsched.core.model.DoodlePenSize
import io.github.droidkaigi.confsched.core.model.DoodlePoint
import io.github.droidkaigi.confsched.core.model.DoodleStroke
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import kotlin.math.pow

/**
 * A drawing surface standing in for the surface a doodle ends up on: [doodle] is what the user has
 * drawn so far, and each finished drag arrives at [onStrokeAdd] in [referenceSize]'s dp space,
 * anchored at [origin]. The surface is laid out at [referenceSize] scaled to fit, up to [maxScale],
 * and a stroke is stored back in the target's own dp space. [background] renders the underlay the
 * user draws over and [overlay] whatever must stay above the strokes; both receive that scale.
 *
 * [transform] magnifies what the frame shows without resizing the frame, so the frame and its
 * sketched border stay where they were laid out. One finger draws, or leaves a dot where it is
 * lifted without travelling; a second finger arriving before the first has travelled the touch slop
 * turns the gesture into a pinch and records no stroke, as does a wheel turned with Ctrl or Meta
 * held. Every stroke carries the width [penSize] gives it and the ink [selectedInk] names, drawn as
 * [palette] resolves that ink.
 */
@Composable
fun DoodleCanvasView(
    doodle: Doodle,
    referenceSize: DpSize,
    maxScale: Float,
    origin: DoodleOrigin,
    palette: DoodleInkPalette,
    penSize: DoodlePenSize,
    selectedInk: DoodleInk,
    onStrokeAdd: (DoodleStroke) -> Unit,
    modifier: Modifier = Modifier,
    transform: DoodleCanvasTransform = rememberDoodleCanvasTransform(),
    background: @Composable BoxScope.(scale: Float) -> Unit = {},
    overlay: @Composable BoxScope.(scale: Float) -> Unit = {},
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val scale = minOf(maxWidth / referenceSize.width, maxHeight / referenceSize.height).coerceAtMost(maxScale)
        val points = remember { mutableStateListOf<DoodlePoint>() }
        val currentOnStrokeAdd by rememberUpdatedState(onStrokeAdd)
        // The gesture detector outlives a pen change, so the pen is read when the stroke lands.
        val currentPenSize by rememberUpdatedState(penSize)
        val currentInk by rememberUpdatedState(selectedInk)
        val commitStroke: () -> Unit = {
            if (points.isNotEmpty()) {
                currentOnStrokeAdd(
                    DoodleStroke(points = points.toList(), width = currentPenSize.width, ink = currentInk),
                )
                points.clear()
            }
        }
        val shape = SketchRoundRectShape(seed = combineSketchSeed(CANVAS_SEED), borderThickness = 1.5.dp)
        Box(modifier = Modifier.size(referenceSize * scale)) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .testTag(DOODLE_CANVAS_FRAME_TEST_TAG)
                    .clip(shape)
                    .androidSystemGestureExclusion()
                    .pointerInput(scale, origin, transform) {
                        detectDoodleStrokes(
                            transform = transform,
                            origin = origin,
                            unit = density * scale,
                            onStrokeStart = { point ->
                                points.clear()
                                points.add(point)
                            },
                            onStrokePoint = points::add,
                            onStrokeEnd = commitStroke,
                        )
                    }
                    .pointerInput(transform) { detectDoodleWheelZoom(transform) },
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            scaleX = transform.zoom
                            scaleY = transform.zoom
                            translationX = transform.offset.x * size.width
                            translationY = transform.offset.y * size.height
                        },
                ) {
                    background(scale)
                    DoodleLayerView(
                        doodle = doodle,
                        palette = palette,
                        origin = origin,
                        scale = scale,
                        modifier = Modifier.matchParentSize(),
                    )
                    val inProgress = DoodleStroke(
                        points = points.toList(),
                        width = penSize.width,
                        ink = selectedInk,
                    )
                    DoodleLayerView(
                        doodle = Doodle(strokes = listOf(inProgress)),
                        palette = palette,
                        origin = origin,
                        scale = scale,
                        modifier = Modifier.matchParentSize(),
                    )
                    overlay(scale)
                }
                // Above the content: magnified content reaches the frame's edges, and the border
                // marks where the drawing surface ends whatever is showing inside it.
                Box(Modifier.matchParentSize().sketchBorder(shape, palette.default.color))
            }
            DoodleZoomControlsView(
                zoom = transform.zoom,
                onZoomInClick = transform::zoomIn,
                onZoomOutClick = transform::zoomOut,
                onResetClick = transform::reset,
                modifier = Modifier.align(Alignment.TopEnd).padding(DoodleZoomControlsInset),
            )
        }
    }
}

/** Identifies the frame a stroke is drawn in, which carries no semantics of its own to find it by. */
const val DOODLE_CANVAS_FRAME_TEST_TAG = "DoodleCanvasFrameTestTag"

/**
 * Splits one gesture between drawing and transforming: the first finger draws once it has travelled
 * the touch slop, and a second finger arriving before that turns the whole gesture into a pinch. A
 * gesture never changes its mind, so a pinch leaves no stroke behind, while a finger lifted before
 * it travelled leaves a stroke holding the single point it was pressed at.
 */
private suspend fun PointerInputScope.detectDoodleStrokes(
    transform: DoodleCanvasTransform,
    origin: DoodleOrigin,
    unit: Float,
    onStrokeStart: (DoodlePoint) -> Unit,
    onStrokePoint: (DoodlePoint) -> Unit,
    onStrokeEnd: () -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown()
        var drawing = false
        var transforming = false
        while (true) {
            val event = awaitPointerEvent()
            if (event.changes.none { it.pressed }) break
            when {
                transforming -> {
                    val zoomChange = event.calculateZoom()
                    if (zoomChange != 1f) {
                        transform.zoomBy(zoomChange, event.calculateCentroid().toFrameFraction(size))
                    }
                    transform.panBy(event.calculatePan().toFrameFraction(size))
                    event.changes.forEach(PointerInputChange::consume)
                }

                drawing -> {
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (!change.pressed) break
                    onStrokePoint(transform.toDoodlePoint(change.position, size, origin, unit))
                    change.consume()
                }

                event.changes.count { it.pressed } > 1 -> transforming = true

                else -> {
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if ((change.position - down.position).getDistance() > viewConfiguration.touchSlop) {
                        drawing = true
                        onStrokeStart(transform.toDoodlePoint(down.position, size, origin, unit))
                        onStrokePoint(transform.toDoodlePoint(change.position, size, origin, unit))
                        change.consume()
                    }
                }
            }
        }
        when {
            drawing -> onStrokeEnd()

            !transforming -> {
                onStrokeStart(transform.toDoodlePoint(down.position, size, origin, unit))
                onStrokeEnd()
            }
        }
    }
}

/**
 * Zooms around the pointer on a wheel turned with Ctrl or Meta held, which is how a pointer device
 * reaches what a pinch does. A wheel turned bare is left to whatever scrolls the screen.
 */
private suspend fun PointerInputScope.detectDoodleWheelZoom(transform: DoodleCanvasTransform) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            if (event.type != PointerEventType.Scroll) continue
            if (!event.keyboardModifiers.isCtrlPressed && !event.keyboardModifiers.isMetaPressed) continue
            val scrolled = event.changes.fold(0f) { total, change -> total + change.scrollDelta.y }
            if (scrolled == 0f) continue
            val pivot = event.changes.firstOrNull()?.position ?: continue
            transform.zoomBy(2f.pow(-scrolled * WHEEL_ZOOM_EXPONENT), pivot.toFrameFraction(size))
            event.changes.forEach(PointerInputChange::consume)
        }
    }
}

private fun DoodleCanvasTransform.toDoodlePoint(
    position: Offset,
    size: IntSize,
    origin: DoodleOrigin,
    unit: Float,
): DoodlePoint {
    val content = toContentPosition(position.toFrameFraction(size))
    return DoodlePoint(
        x = (content.x * size.width - origin.originX(size.width.toFloat())) / unit,
        y = content.y * size.height / unit,
    )
}

// Both a position within the frame and a displacement across it are held as fractions of the
// frame's size, which is the space DoodleCanvasTransform works in.
private fun Offset.toFrameFraction(size: IntSize): Offset = Offset(
    x = if (size.width == 0) 0f else x / size.width,
    y = if (size.height == 0) 0f else y / size.height,
)

private val DoodleZoomControlsInset = 8.dp

// One wheel notch is about one unit of scroll delta, which this turns into a fifth of a doubling.
private const val WHEEL_ZOOM_EXPONENT = 0.2f

private const val CANVAS_SEED = 4213

@LocalePreviews
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
            palette = aboutWallDoodleInkPalette(),
            penSize = DoodlePenSize.Normal,
            selectedInk = DoodleInk.Default,
            onStrokeAdd = {},
            modifier = Modifier.size(AboutHeroSize),
            background = { Box(modifier = Modifier.matchParentSize().background(MaterialTheme.colorScheme.primary)) },
        )
    }
}

@LocalePreviews
@Composable
private fun DoodleCanvasViewMagnifiedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleCanvasView(
            doodle = Doodle.fake(),
            referenceSize = AboutHeroSize,
            maxScale = 1f,
            origin = DoodleOrigin.TopCenter,
            palette = aboutWallDoodleInkPalette(),
            penSize = DoodlePenSize.Normal,
            selectedInk = DoodleInk.Default,
            onStrokeAdd = {},
            modifier = Modifier.size(AboutHeroSize),
            transform = rememberDoodleCanvasTransform(initialZoom = 2f, initialOffset = Offset(x = 0.2f, y = -0.3f)),
            background = { Box(modifier = Modifier.matchParentSize().background(MaterialTheme.colorScheme.primary)) },
        )
    }
}

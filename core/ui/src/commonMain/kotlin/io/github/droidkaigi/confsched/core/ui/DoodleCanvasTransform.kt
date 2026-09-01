package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

/**
 * How far into its own frame a [DoodleCanvasView] is magnified. [zoom] multiplies the scale the
 * canvas already fits its target at, and [offset] is the frame's centre displaced by a fraction of
 * the frame's own size — a fraction rather than a length, so a rotation that resizes the frame
 * keeps the same part of the drawing in view.
 *
 * Every mutation clamps [offset] to the range that keeps the magnified content covering the frame,
 * which leaves [Offset.Zero] as the only value at [MIN_ZOOM].
 */
@Stable
class DoodleCanvasTransform internal constructor(zoom: Float, offset: Offset) {

    var zoom: Float by mutableStateOf(zoom.coerceIn(MIN_ZOOM, MAX_ZOOM))
        private set

    var offset: Offset by mutableStateOf(clampOffset(offset, zoom))
        private set

    /** Magnifies by one step about the frame's centre, which is where a control acts from. */
    fun zoomIn() {
        zoomBy(ZOOM_STEP, FrameCenter)
    }

    fun zoomOut() {
        zoomBy(1f / ZOOM_STEP, FrameCenter)
    }

    /**
     * Multiplies the zoom by [factor], holding the content under [pivot] in place. [pivot] is a
     * position within the frame as a fraction of its size, so its centre is `Offset(0.5f, 0.5f)`.
     */
    fun zoomBy(factor: Float, pivot: Offset) {
        val target = (zoom * factor).coerceIn(MIN_ZOOM, MAX_ZOOM)
        val fromCenter = pivot - FrameCenter
        offset = clampOffset(fromCenter - (fromCenter - offset) * (target / zoom), target)
        zoom = target
    }

    /** Moves the content by [delta], a displacement expressed as a fraction of the frame's size. */
    fun panBy(delta: Offset) {
        offset = clampOffset(offset + delta, zoom)
    }

    fun reset() {
        zoom = MIN_ZOOM
        offset = Offset.Zero
    }

    /**
     * The position [framePosition] points at in the content's own space, both as a fraction of the
     * frame's size — the inverse of what the canvas renders, so a stroke lands where it was drawn.
     */
    fun toContentPosition(framePosition: Offset): Offset =
        FrameCenter + (framePosition - FrameCenter - offset) / zoom

    companion object {
        const val MIN_ZOOM = 1f
        const val MAX_ZOOM = 4f

        /** The factor one press of a zoom control applies. */
        private const val ZOOM_STEP = 1.5f

        private val FrameCenter = Offset(0.5f, 0.5f)

        private fun clampOffset(offset: Offset, zoom: Float): Offset {
            val bound = (zoom.coerceIn(MIN_ZOOM, MAX_ZOOM) - MIN_ZOOM) / 2f
            return Offset(x = offset.x.coerceIn(-bound, bound), y = offset.y.coerceIn(-bound, bound))
        }
    }
}

/**
 * Remembers a [DoodleCanvasTransform] across configuration changes and process death, starting at
 * [initialZoom] and [initialOffset].
 */
@Composable
fun rememberDoodleCanvasTransform(
    initialZoom: Float = DoodleCanvasTransform.MIN_ZOOM,
    initialOffset: Offset = Offset.Zero,
): DoodleCanvasTransform = rememberSaveable(saver = DoodleCanvasTransformSaver) {
    DoodleCanvasTransform(zoom = initialZoom, offset = initialOffset)
}

private val DoodleCanvasTransformSaver = listSaver<DoodleCanvasTransform, Float>(
    save = { listOf(it.zoom, it.offset.x, it.offset.y) },
    restore = { DoodleCanvasTransform(zoom = it[0], offset = Offset(x = it[1], y = it[2])) },
)

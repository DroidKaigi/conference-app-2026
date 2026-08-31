package io.github.droidkaigi.confsched.core.model

import kotlinx.serialization.Serializable

/**
 * A freehand drawing over one [DoodleTarget], in that target's own dp space, so a wider surface
 * widens the margins around the drawing rather than stretching it. [DoodlePoint.y] always runs
 * down from the surface's top edge; [DoodlePoint.x] runs out from the origin the target anchors
 * its drawing to.
 */
@Serializable
data class Doodle(val strokes: List<DoodleStroke>) {
    companion object {
        val Empty = Doodle(emptyList())
    }
}

/** One drawn line, [width] dp wide in the same space [points] are measured in. */
@Serializable
data class DoodleStroke(val points: List<DoodlePoint>, val width: Float)

@Serializable
data class DoodlePoint(val x: Float, val y: Float)

/** The widths a stroke can be drawn at, offered as the pens a doodle is drawn with. */
enum class DoodlePenSize(val width: Float) {
    Thin(1.5f),
    Normal(2.5f),
    Thick(4.5f),
}

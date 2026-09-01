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

/**
 * One drawn line, [width] dp wide in the same space [points] are measured in, laid down in [ink].
 * An [outlined] stroke is drawn with a rim around it, which is what carries it over a surface its
 * own ink matches.
 */
@Serializable
data class DoodleStroke(
    val points: List<DoodlePoint>,
    val width: Float,
    val ink: DoodleInk,
    val outlined: Boolean,
)

@Serializable
data class DoodlePoint(val x: Float, val y: Float)

/**
 * The colors a stroke can be drawn in: the four a profile card's own design is painted with, each
 * resolved against the surface the stroke lands on.
 */
@Serializable
enum class DoodleInk {
    /** The surface's own default ink. */
    Ink,

    /** The card's dusk band. */
    Band,

    /** The card's plate. */
    Paper,

    /** The gold of the card's scan banner. */
    Banner,
}

/** The widths a stroke can be drawn at, offered as the pens a doodle is drawn with. */
enum class DoodlePenSize(val width: Float) {
    Thin(1.5f),
    Normal(2.5f),
    Thick(4.5f),
}

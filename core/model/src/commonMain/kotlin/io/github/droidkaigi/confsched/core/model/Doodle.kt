package io.github.droidkaigi.confsched.core.model

import kotlinx.serialization.Serializable

/**
 * A freehand drawing over the About screen's hero, in the hero's own dp space: [DoodlePoint.y]
 * runs down from the hero's top edge and [DoodlePoint.x] out from its horizontal center, so a
 * wider hero widens the margins around the drawing rather than stretching it.
 */
@Serializable
data class Doodle(val strokes: List<DoodleStroke>) {
    companion object {
        val Empty = Doodle(emptyList())
    }
}

@Serializable
data class DoodleStroke(val points: List<DoodlePoint>)

@Serializable
data class DoodlePoint(val x: Float, val y: Float)

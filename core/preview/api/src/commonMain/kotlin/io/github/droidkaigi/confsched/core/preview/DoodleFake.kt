package io.github.droidkaigi.confsched.core.preview

import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodlePoint
import io.github.droidkaigi.confsched.core.model.DoodleStroke

fun Doodle.Companion.fake(): Doodle = Doodle(
    strokes = listOf(
        DoodleStroke(points = wave(startX = -70f, y = 150f, amplitude = 14f)),
        DoodleStroke(points = wave(startX = 10f, y = 190f, amplitude = -8f)),
    ),
)

private fun wave(startX: Float, y: Float, amplitude: Float): List<DoodlePoint> = (0..12).map { step ->
    val progress = step / 12f
    DoodlePoint(x = startX + progress * 60f, y = y + amplitude * progress * (1f - progress) * 4f)
}

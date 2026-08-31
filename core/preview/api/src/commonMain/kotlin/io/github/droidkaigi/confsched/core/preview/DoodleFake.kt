package io.github.droidkaigi.confsched.core.preview

import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleInk
import io.github.droidkaigi.confsched.core.model.DoodlePenSize
import io.github.droidkaigi.confsched.core.model.DoodlePoint
import io.github.droidkaigi.confsched.core.model.DoodleStroke

/** A drawing in the About hero's space, where x runs out from the hero's horizontal center. */
fun Doodle.Companion.fake(): Doodle = Doodle(
    strokes = listOf(
        DoodleStroke(
            points = wave(startX = -70f, y = 150f, amplitude = 14f),
            width = NormalWidth,
            ink = DoodleInk.Default,
        ),
        DoodleStroke(
            points = wave(startX = 10f, y = 190f, amplitude = -8f),
            width = NormalWidth,
            ink = DoodleInk.Accent,
        ),
    ),
)

/** A drawing in a profile card face's space, where the origin is the face's top-start corner. */
fun Doodle.Companion.fakeOnCardFace(): Doodle = Doodle(
    strokes = listOf(
        DoodleStroke(
            points = wave(startX = 236f, y = 62f, amplitude = 16f),
            width = NormalWidth,
            ink = DoodleInk.Default,
        ),
        DoodleStroke(
            points = wave(startX = 40f, y = 250f, amplitude = 18f),
            width = NormalWidth,
            ink = DoodleInk.Accent,
        ),
        DoodleStroke(
            points = wave(startX = 170f, y = 430f, amplitude = -12f),
            width = NormalWidth,
            ink = DoodleInk.Default,
        ),
    ),
)

private val NormalWidth = DoodlePenSize.Normal.width

private fun wave(startX: Float, y: Float, amplitude: Float): List<DoodlePoint> = (0..12).map { step ->
    val progress = step / 12f
    DoodlePoint(x = startX + progress * 60f, y = y + amplitude * progress * (1f - progress) * 4f)
}

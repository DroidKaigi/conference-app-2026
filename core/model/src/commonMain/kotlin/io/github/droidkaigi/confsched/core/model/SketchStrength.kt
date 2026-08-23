package io.github.droidkaigi.confsched.core.model

/**
 * How far a hand-drawn line departs from the shape it traces.
 *
 * [amplitudeScale] multiplies every sketch amplitude, so an element keeps the proportions its
 * own defaults give it. The design file states the levels as E = 0.5 / 1.6 / 3.4; the figures
 * here are those taken relative to [Normal].
 */
enum class SketchStrength(val amplitudeScale: Float) {
    Subtle(0.3125f),
    Normal(1f),
    Playful(2.125f),
}

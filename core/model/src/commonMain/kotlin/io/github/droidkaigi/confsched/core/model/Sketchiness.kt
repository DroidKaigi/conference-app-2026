package io.github.droidkaigi.confsched.core.model

/**
 * How far the profile card's hand-sketched outlines wobble from a straight line, expressed as the
 * multiplier applied to the amplitude the card's own size derives.
 */
enum class Sketchiness(val amplitudeMultiplier: Float) {
    Subtle(0.5f),
    Normal(1.6f),
    Playful(3.4f),
}

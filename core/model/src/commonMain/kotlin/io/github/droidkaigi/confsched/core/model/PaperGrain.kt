package io.github.droidkaigi.confsched.core.model

/** How coarsely the profile card's paper is textured, as the opacity of the grain laid over it. */
enum class PaperGrain(val grainAlpha: Float) {
    Smooth(0f),
    Grained(0.12f),
    Rough(0.22f),
}

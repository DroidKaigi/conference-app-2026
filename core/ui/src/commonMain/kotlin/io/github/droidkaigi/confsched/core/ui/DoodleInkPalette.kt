package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import io.github.droidkaigi.confsched.core.model.DoodleInk

/**
 * How one ink lands on a surface: the stroke is drawn in [color], rimmed by [haloColor] where the
 * surface sits too close to the ink to tell them apart. A null halo draws the ink bare.
 */
@Immutable
data class DoodleInkStyle(val color: Color, val haloColor: Color?)

/** Every [DoodleInk] resolved against one surface. */
@Immutable
data class DoodleInkPalette(
    val default: DoodleInkStyle,
    val accent: DoodleInkStyle,
    val pink: DoodleInkStyle,
    val chalk: DoodleInkStyle,
) {
    fun style(ink: DoodleInk): DoodleInkStyle = when (ink) {
        DoodleInk.Default -> default
        DoodleInk.Accent -> accent
        DoodleInk.Pink -> pink
        DoodleInk.Chalk -> chalk
    }
}

/** Physical crayons: the same pigment in every theme, so only the surface under them changes. */
object DoodleCrayonColors {
    val Orange = Color(0xFFE04A1E)
    val Pink = Color(0xFFE88AB0)
    val Chalk = Color(0xFFFAF6EE)
}

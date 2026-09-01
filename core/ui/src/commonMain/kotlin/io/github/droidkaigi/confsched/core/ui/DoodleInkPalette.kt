package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import io.github.droidkaigi.confsched.core.model.DoodleInk

/**
 * How one ink lands on a surface: the stroke is drawn in [color], and an outlined stroke takes a rim
 * of [rimColor] around it. A rim contrasts the stroke rather than the surface, so it reads wherever
 * the stroke lands.
 */
@Immutable
data class DoodleInkStyle(val color: Color, val rimColor: Color)

/** Every [DoodleInk] resolved against one surface. */
@Immutable
data class DoodleInkPalette(
    val ink: DoodleInkStyle,
    val band: DoodleInkStyle,
    val paper: DoodleInkStyle,
    val banner: DoodleInkStyle,
) {
    fun style(ink: DoodleInk): DoodleInkStyle = when (ink) {
        DoodleInk.Ink -> this.ink
        DoodleInk.Band -> band
        DoodleInk.Paper -> paper
        DoodleInk.Banner -> banner
    }

    /**
     * The inks worth offering as choices. A scheme may paint two of the roles the same color, and
     * the later of the two would then be a swatch the user cannot tell from the earlier one; a
     * stroke already drawn in it still resolves through [style].
     */
    fun distinctInks(): List<DoodleInk> = DoodleInk.entries.distinctBy { style(it).color }
}

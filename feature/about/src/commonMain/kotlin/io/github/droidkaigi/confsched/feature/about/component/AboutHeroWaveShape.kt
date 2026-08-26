package io.github.droidkaigi.confsched.feature.about.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.max

/**
 * A rectangle whose bottom edge undulates in a run of hand-drawn waves while the top and sides
 * stay straight, so the hero band can end in a wavy line without notching the screen edges.
 *
 * [amplitude] is the nominal reach of a crest past the trough line and [wavelength] the pitch of
 * one full wave. Each crest's reach and each trough's depth are nudged by a deterministic hash of
 * their position — the same [seed] always draws the same edge — so the line reads as drawn by hand
 * rather than as a mechanical sine. The trough line sits far enough above the band's bottom edge
 * that the deepest jittered crest lands exactly on it, so no crest is clipped flat.
 */
@Immutable
internal data class AboutHeroWaveShape(
    private val amplitude: Dp,
    private val wavelength: Dp,
    private val seed: Int = 0,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val amp = with(density) { amplitude.toPx() }
        val halfWave = with(density) { wavelength.toPx() } / 2f
        val baseTroughY = size.height - amp * DEEPEST_CREST
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, baseTroughY)
            // Walk the bottom right-to-left in half-wave steps. Each half-wave is a quadratic whose
            // control sits twice the (jittered) amplitude past the (jittered) trough line, so the arc
            // peaks one amplitude away, alternating down and up.
            var x = size.width
            var crestDown = true
            var index = 0
            while (x > 0f) {
                val nextX = max(0f, x - halfWave)
                val reach = amp * jitter(index, lowest = REACH_LOWEST, span = REACH_SPAN)
                val endTrough = baseTroughY + amp * jitter(index + 51, lowest = TROUGH_LOWEST, span = TROUGH_SPAN)
                val controlY = if (crestDown) endTrough + reach * 2f else endTrough - reach * 2f
                quadraticBezierTo((x + nextX) / 2f, controlY, nextX, endTrough)
                x = nextX
                crestDown = !crestDown
                index++
            }
            close()
        }
        return Outline.Generic(path)
    }

    private companion object {
        const val REACH_LOWEST = 0.78f
        const val REACH_SPAN = 0.38f
        const val TROUGH_LOWEST = -0.14f
        const val TROUGH_SPAN = 0.28f

        /** How far past the trough line, in amplitudes, the deepest crest can reach. */
        const val DEEPEST_CREST = REACH_LOWEST + REACH_SPAN + TROUGH_LOWEST + TROUGH_SPAN
    }

    /** A stable value in `[lowest, lowest + span)` for [index], mixed with [seed]. */
    private fun jitter(index: Int, lowest: Float, span: Float): Float {
        var h = (seed * 73856093) xor (index * 19349663)
        h = (h xor (h ushr 13)) * 1274126177
        h = h xor (h ushr 16)
        return lowest + (h ushr 8) / 16777216f * span
    }
}

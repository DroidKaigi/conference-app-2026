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
 * A rectangle whose bottom edge scallops in a run of rounded waves while the top and sides
 * stay straight, so the hero band can end in a wavy line without notching the screen edges.
 *
 * [amplitude] is how far each crest reaches below the trough line, and [wavelength] the pitch
 * of one full wave. The band has to reserve `2 * amplitude` of height below its content for
 * the wave to occupy.
 */
@Immutable
internal data class AboutHeroWaveShape(
    private val amplitude: Dp,
    private val wavelength: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val amp = with(density) { amplitude.toPx() }
        val halfWave = with(density) { wavelength.toPx() } / 2f
        val troughY = size.height - amp * 2f
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, troughY)
            // Each half-wave is a quadratic whose control sits twice the amplitude past the
            // trough line, so the arc peaks exactly one amplitude away, alternating down and up.
            var x = size.width
            var crestDown = true
            while (x > 0f) {
                val nextX = max(0f, x - halfWave)
                val controlY = if (crestDown) troughY + amp * 2f else troughY - amp * 2f
                quadraticBezierTo((x + nextX) / 2f, controlY, nextX, troughY)
                x = nextX
                crestDown = !crestDown
            }
            close()
        }
        return Outline.Generic(path)
    }
}

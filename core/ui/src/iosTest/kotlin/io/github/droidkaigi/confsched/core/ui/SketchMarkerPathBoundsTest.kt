package io.github.droidkaigi.confsched.core.ui

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertTrue

class SketchMarkerPathBoundsTest {
    @Test
    fun marker_stays_within_its_height_and_allowed_bleed() {
        val widths = listOf(1f, 2.9f, 3f, 6f, 50f, 120f)
        val heights = listOf(0f, 2f, 4f, 17.2f)
        val bleeds = listOf(0f to 0f, 0.5f to 0.5f, 3f to 3f, 8f to 8f, 0f to 8f, 8f to 0f)

        for (seed in 700..992) {
            for (width in widths) {
                for (height in heights) {
                    for ((startBleed, endBleed) in bleeds) {
                        val bounds = with(Density(1f)) {
                            sketchMarkerPath(
                                width = width,
                                height = height,
                                startBleed = startBleed,
                                endBleed = endBleed,
                                seed = seed,
                                roughness = 1.15.dp,
                                tremor = 0.32.dp,
                            ).getBounds()
                        }
                        val context = "seed=$seed, width=$width, height=$height, " +
                            "startBleed=$startBleed, endBleed=$endBleed, bounds=$bounds"

                        assertTrue(bounds.left >= -startBleed, context)
                        assertTrue(bounds.right <= width + endBleed, context)
                        assertTrue(bounds.top >= -height / 2f, context)
                        assertTrue(bounds.bottom <= height / 2f, context)
                    }
                }
            }
        }
    }
}

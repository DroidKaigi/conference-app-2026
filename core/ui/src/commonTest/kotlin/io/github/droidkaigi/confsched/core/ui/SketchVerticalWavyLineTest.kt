package io.github.droidkaigi.confsched.core.ui

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SketchVerticalWavyLineTest {
    private val density = Density(2f)
    private val amplitude = 3.dp
    private val wavelength = 10.dp
    private val noiseAmount = 0.8f
    private val seed = 20
    private val cycle = with(density) { wavelength.toPx() } * WAVY_NOISE_CELLS

    private fun xAt(y: Float, phase: Float): Float = with(density) {
        sketchVerticalWavyLineXAt(
            y = y,
            centerX = 0f,
            amplitude = amplitude,
            wavelength = wavelength,
            noiseAmount = noiseAmount,
            phase = phase,
            seed = seed,
        )
    }

    @Test
    fun a_phase_of_one_cycle_draws_the_line_a_phase_of_zero_draws() {
        for (step in 0..80) {
            val y = step.toFloat()
            assertEquals(
                expected = xAt(y = y, phase = 0f),
                actual = xAt(y = y, phase = cycle),
                absoluteTolerance = 0.01f,
                message = "y=$y",
            )
        }
    }

    @Test
    fun a_phase_slides_the_line_along_itself() {
        val phase = cycle / 4f
        for (step in 0..80) {
            val y = step.toFloat()
            assertEquals(
                expected = xAt(y = y + phase, phase = 0f),
                actual = xAt(y = y, phase = phase),
                absoluteTolerance = 0.01f,
                message = "y=$y",
            )
        }
    }

    @Test
    fun half_a_cycle_moves_the_line_off_where_it_started() {
        val moved = (0..80).any { step ->
            val y = step.toFloat()
            abs(xAt(y = y, phase = 0f) - xAt(y = y, phase = cycle / 2f)) > 1f
        }
        assertTrue(moved)
    }
}

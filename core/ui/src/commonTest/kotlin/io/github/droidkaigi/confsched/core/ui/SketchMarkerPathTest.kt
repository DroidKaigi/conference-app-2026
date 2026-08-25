package io.github.droidkaigi.confsched.core.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class SketchMarkerPathTest {
    @Test
    fun the_band_is_built_to_reach_the_height_it_is_given() {
        assertEquals(
            expected = 5.704f,
            actual = markerHalfHeight(height = 17.2f, wobble = 1.47f),
            absoluteTolerance = 0.001f,
        )
        assertEquals(
            expected = 0.424f,
            actual = markerHalfHeight(height = 4f, wobble = 1.47f),
            absoluteTolerance = 0.001f,
        )
    }

    @Test
    fun a_band_with_no_room_for_the_wobble_collapses_rather_than_inverting() {
        assertEquals(
            expected = 0f,
            actual = markerHalfHeight(height = 2f, wobble = 1.47f),
        )
    }
}

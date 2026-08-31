package io.github.droidkaigi.confsched.feature.profilecard

import io.github.droidkaigi.confsched.core.ui.DeviceTilt
import io.github.droidkaigi.confsched.feature.profilecard.component.ProfileCardLean
import io.github.droidkaigi.confsched.feature.profilecard.component.profileCardLean
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProfileCardLeanTest {
    private val tolerance = 0.01f

    private fun assertLean(expected: ProfileCardLean, actual: ProfileCardLean) {
        assertEquals(expected.pitchDegrees, actual.pitchDegrees, absoluteTolerance = tolerance)
        assertEquals(expected.rollDegrees, actual.rollDegrees, absoluteTolerance = tolerance)
    }

    @Test
    fun the_card_is_level_at_the_pose_it_started_from() {
        val baseline = DeviceTilt(pitchDegrees = -34f, rollDegrees = 12f)
        assertLean(ProfileCardLean.Level, profileCardLean(baseline, baseline))
    }

    @Test
    fun the_lean_follows_the_tilt_away_from_the_baseline() {
        val baseline = DeviceTilt(pitchDegrees = -30f, rollDegrees = 10f)
        val measured = DeviceTilt(pitchDegrees = -25f, rollDegrees = 4f)
        assertLean(ProfileCardLean(pitchDegrees = 5f, rollDegrees = -6f), profileCardLean(baseline, measured))
    }

    @Test
    fun a_large_tilt_is_clamped_in_both_axes() {
        val lean = profileCardLean(
            baseline = DeviceTilt.Level,
            measured = DeviceTilt(pitchDegrees = 45f, rollDegrees = -70f),
        )
        assertTrue(abs(lean.pitchDegrees) <= 12f)
        assertTrue(abs(lean.rollDegrees) <= 12f)
        assertEquals(12f, lean.pitchDegrees, absoluteTolerance = tolerance)
        assertEquals(-12f, lean.rollDegrees, absoluteTolerance = tolerance)
    }

    @Test
    fun a_roll_across_the_half_turn_takes_the_shorter_way_round() {
        val lean = profileCardLean(
            baseline = DeviceTilt(pitchDegrees = 0f, rollDegrees = 175f),
            measured = DeviceTilt(pitchDegrees = 0f, rollDegrees = -179f),
        )
        assertEquals(6f, lean.rollDegrees, absoluteTolerance = tolerance)
    }

    @Test
    fun roll_has_no_authority_where_the_device_stands_upright() {
        val lean = profileCardLean(
            baseline = DeviceTilt(pitchDegrees = -90f, rollDegrees = 0f),
            measured = DeviceTilt(pitchDegrees = -90f, rollDegrees = 40f),
        )
        assertEquals(0f, lean.rollDegrees, absoluteTolerance = tolerance)
    }

    @Test
    fun roll_authority_fades_in_as_the_device_leaves_the_vertical() {
        val baseline = DeviceTilt(pitchDegrees = -72.5f, rollDegrees = 0f)
        val upright = profileCardLean(baseline, DeviceTilt(pitchDegrees = -72.5f, rollDegrees = 8f))
        val tipped = profileCardLean(baseline, DeviceTilt(pitchDegrees = -50f, rollDegrees = 8f))
        assertEquals(4f, upright.rollDegrees, absoluteTolerance = tolerance)
        assertEquals(8f, tipped.rollDegrees, absoluteTolerance = tolerance)
    }

    @Test
    fun the_pitch_lean_survives_where_roll_authority_is_gone() {
        val lean = profileCardLean(
            baseline = DeviceTilt(pitchDegrees = -90f, rollDegrees = 0f),
            measured = DeviceTilt(pitchDegrees = -86f, rollDegrees = 30f),
        )
        assertEquals(4f, lean.pitchDegrees, absoluteTolerance = tolerance)
        assertEquals(0f, lean.rollDegrees, absoluteTolerance = tolerance)
    }
}

package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Immutable

/**
 * The device's absolute tilt in screen coordinates, in degrees.
 *
 * The axes follow the rendered screen rather than the physical device: each platform source remaps
 * the sensor by the current display or interface orientation, so landscape does not skew them.
 *
 * The signs are the ones `SensorManager.getOrientation` defines, which is the opposite of the
 * aviation sense the words carry elsewhere: a device stood upright reads a pitch of -90°, not +90°.
 * `CMAttitude` measures the aviation way, so the iOS source negates both angles to match.
 *
 * A consumer that wants the tilt relative to some moment, such as when a screen opened, keeps that
 * baseline itself.
 *
 * These are Euler angles: where [pitchDegrees] reaches ±90°, the screen held vertical, [rollDegrees]
 * is no longer determined by the screen's own orientation and swings across a wide range.
 *
 * @property pitchDegrees rotation about the screen's horizontal axis, positive as the top edge tips toward the ground. Runs -90 to 90, folding back past the vertical.
 * @property rollDegrees rotation about the screen's vertical axis, positive as the left edge tips toward the ground. Runs -180 to 180: ±90 is the screen standing on edge, and ±180 is the screen facing the ground.
 */
@Immutable
data class DeviceTilt(
    val pitchDegrees: Float,
    val rollDegrees: Float,
) {
    companion object {
        /** No tilt in either axis. */
        val Level: DeviceTilt = DeviceTilt(pitchDegrees = 0f, rollDegrees = 0f)
    }
}

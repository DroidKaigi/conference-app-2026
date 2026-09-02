package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import io.github.droidkaigi.confsched.core.ui.DeviceTilt
import io.github.droidkaigi.confsched.core.ui.LocalDeviceTiltSource
import io.github.droidkaigi.confsched.core.ui.rememberReducedMotion
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs

/** How far the card leans out of the screen plane, in degrees. */
@Immutable
internal data class ProfileCardLean(
    val pitchDegrees: Float,
    val rollDegrees: Float,
) {
    companion object {
        val Level: ProfileCardLean = ProfileCardLean(pitchDegrees = 0f, rollDegrees = 0f)

        val VectorConverter: TwoWayConverter<ProfileCardLean, AnimationVector2D> = TwoWayConverter(
            convertToVector = { AnimationVector2D(it.pitchDegrees, it.rollDegrees) },
            convertFromVector = { ProfileCardLean(pitchDegrees = it.v1, rollDegrees = it.v2) },
        )
    }
}

/**
 * The lean the card is drawn at, springing towards the device's tilt away from where it was held
 * when the card appeared. It stays [ProfileCardLean.Level] under reduced motion and on a platform
 * that reports no tilt.
 *
 * Read the returned value inside `graphicsLayer`: the sensor ticks at tens of hertz, and a read in
 * composition recomposes the card at that rate.
 */
@Composable
internal fun rememberProfileCardLean(): Animatable<ProfileCardLean, AnimationVector2D> {
    val lean = remember { Animatable(ProfileCardLean.Level, ProfileCardLean.VectorConverter) }
    if (rememberReducedMotion()) {
        LaunchedEffect(lean) { lean.snapTo(ProfileCardLean.Level) }
        return lean
    }

    val source = LocalDeviceTiltSource.current
    val tilt = source.tiltAsState()
    val pinned = source.pinnedAsState()
    LaunchedEffect(lean, tilt, pinned) {
        var baseline: DeviceTilt? = null
        // The source holds DeviceTilt.Level until its first reading, which a measured tilt never
        // lands on exactly, so the baseline waits for a measured one rather than starting the card
        // leaned on a device that was already tilted.
        snapshotFlow { tilt.value to pinned.value }.collectLatest { (measured, isPinned) ->
            if (isPinned) {
                // A pinned tilt is absolute: baselining it against itself would cancel the pin out.
                lean.animateTo(profileCardLean(DeviceTilt.Level, measured), LeanSpring)
                return@collectLatest
            }
            if (measured == DeviceTilt.Level && baseline == null) return@collectLatest
            val origin = baseline ?: measured.also { baseline = it }
            // collectLatest cancels the running animation, which the spring resumes from at its
            // current velocity, so a stream of readings damps into one continuous motion.
            lean.animateTo(profileCardLean(origin, measured), LeanSpring)
        }
    }
    return lean
}

/**
 * The lean for a [measured] tilt taken against the [baseline] the card started from, clamped so the
 * card never turns far enough to read as a fold.
 *
 * [DeviceTilt.rollDegrees] stops describing the screen's own orientation as the device approaches
 * vertical, where it swings across a wide range for a small movement of the hand. Roll authority
 * therefore fades out over the last stretch before the vertical, leaving a card held upright to
 * answer pitch alone rather than rattling side to side.
 */
internal fun profileCardLean(baseline: DeviceTilt, measured: DeviceTilt): ProfileCardLean {
    val pitch = measured.pitchDegrees - baseline.pitchDegrees
    val roll = wrapDegrees(measured.rollDegrees - baseline.rollDegrees) * rollAuthority(measured.pitchDegrees)
    return ProfileCardLean(
        pitchDegrees = pitch.coerceIn(-MAX_LEAN_DEGREES, MAX_LEAN_DEGREES),
        rollDegrees = roll.coerceIn(-MAX_LEAN_DEGREES, MAX_LEAN_DEGREES),
    )
}

/** The share of the roll delta that survives at [pitchDegrees], falling to none at the vertical. */
private fun rollAuthority(pitchDegrees: Float): Float {
    val fold = (abs(pitchDegrees) - ROLL_FADE_START_DEGREES) / (ROLL_FADE_END_DEGREES - ROLL_FADE_START_DEGREES)
    return 1f - fold.coerceIn(0f, 1f)
}

/** The roll delta as the shorter way round, so a turn across ±180° does not read as a full sweep. */
private fun wrapDegrees(degrees: Float): Float = when {
    degrees > 180f -> degrees - 360f
    degrees < -180f -> degrees + 360f
    else -> degrees
}

private val LeanSpring = spring<ProfileCardLean>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessLow,
)

private const val MAX_LEAN_DEGREES = 12f
private const val ROLL_FADE_START_DEGREES = 60f
private const val ROLL_FADE_END_DEGREES = 85f

package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.abs

/**
 * A light catching the leaning card: a soft highlight that slides across the face as the lean
 * changes and fades out entirely at level, so a resting card shows no glare.
 *
 * Read [lean] inside the draw phase only; the sensor drives it at tens of hertz.
 */
internal fun Modifier.profileCardSheen(lean: () -> ProfileCardLean): Modifier = drawWithContent {
    drawContent()
    val current = lean()
    val pitch = current.pitchDegrees / MAX_LEAN_DEGREES
    val roll = current.rollDegrees / MAX_LEAN_DEGREES
    val strength = maxOf(abs(pitch), abs(roll)).coerceIn(0f, 1f)
    if (strength < SHEEN_VISIBLE_THRESHOLD) return@drawWithContent
    val center = Offset(
        x = size.width * (0.5f + roll * SHEEN_TRAVEL),
        y = size.height * (0.5f - pitch * SHEEN_TRAVEL),
    )
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = SHEEN_MAX_ALPHA * strength), Color.Transparent),
            center = center,
            radius = size.width * SHEEN_RADIUS_FACTOR,
        ),
        blendMode = BlendMode.Screen,
    )
}

private const val SHEEN_MAX_ALPHA = 0.4f
private const val SHEEN_TRAVEL = 0.35f
private const val SHEEN_RADIUS_FACTOR = 0.65f
private const val SHEEN_VISIBLE_THRESHOLD = 0.02f

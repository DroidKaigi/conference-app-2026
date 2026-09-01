package io.github.droidkaigi.confsched.core.ui

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

enum class ErrorScene {
    UnpluggedCable,
    Rain,
    Backstage,
}

// The design fixes one scene per launch: the app shell draws one at random and provides it here,
// the way the sketch seed travels, so previews and screenshot tests see the same scene every run.
val LocalErrorSceneOfLaunch = staticCompositionLocalOf { ErrorScene.UnpluggedCable }

@Composable
internal fun rememberSceneColors(): SceneColors {
    val colorScheme = MaterialTheme.colorScheme
    return remember(colorScheme) {
        SceneColors(
            primary = colorScheme.primary,
            onPrimary = colorScheme.onPrimary,
            onSurface = colorScheme.onSurface,
            primaryContainer = colorScheme.primaryContainer,
        )
    }
}

// The motion values are the design's: the plug travels 3dp over a 2.4s cycle, a raindrop falls
// 24dp and fades over 1.8s in three offset phases, and the lamp swings 3 degrees each way over
// a 4s cycle — all eased, never sprung.

@Composable
internal fun InfiniteTransition.animatePlugBob(): State<Float> = animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 1_200, easing = EaseInOut),
        repeatMode = RepeatMode.Reverse,
    ),
    label = "PlugBob",
)

@Composable
internal fun InfiniteTransition.animateRainFall(phase: Int): State<Float> = animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 1_800, easing = LinearEasing),
        repeatMode = RepeatMode.Restart,
        initialStartOffset = StartOffset(offsetMillis = phase * 600),
    ),
    label = "RainFall$phase",
)

@Composable
internal fun InfiniteTransition.animateLampSwing(): State<Float> = animateFloat(
    initialValue = -LAMP_SWING_DEGREES,
    targetValue = LAMP_SWING_DEGREES,
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 2_000, easing = EaseInOut),
        repeatMode = RepeatMode.Reverse,
    ),
    label = "LampSwing",
)

internal const val PLUG_TRAVEL_FRACTION = 3f / SCENE_FRAME_HEIGHT
internal const val DROP_TRAVEL_FRACTION = 24f / SCENE_FRAME_HEIGHT
private const val LAMP_SWING_DEGREES = 3f

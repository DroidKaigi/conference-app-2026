package io.github.droidkaigi.confsched.core.ui

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale

enum class ErrorScene {
    UnpluggedCable,
    Rain,
    Backstage,
}

object ErrorSceneDefaults {
    // The design fixes one scene per launch: picked at random on first use, then kept.
    val sceneOfLaunch: ErrorScene by lazy(ErrorScene.entries::random)
}

@Composable
internal fun ErrorSceneArt(
    scene: ErrorScene,
    modifier: Modifier = Modifier,
) {
    when (scene) {
        ErrorScene.UnpluggedCable -> UnpluggedCableSceneArt(modifier)
        ErrorScene.Rain -> RainSceneArt(modifier)
        ErrorScene.Backstage -> BackstageSceneArt(modifier)
    }
}

// The design draws every scene in theme tokens, so the colors arrive from the theme instead of
// being baked into the path data. The motion values below are the design's: the plug travels
// 3dp over a 2.4s cycle, a raindrop falls 24dp and fades over 1.8s in three offset phases, and
// the lamp swings 3 degrees each way over a 4s cycle — all eased, never sprung.

@Composable
private fun UnpluggedCableSceneArt(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    val background = remember(colorScheme) {
        unpluggedCableBackgroundVector(
            primary = colorScheme.primary,
            onPrimary = colorScheme.onPrimary,
        )
    }
    val plug = remember(colorScheme) {
        unpluggedCablePlugVector(onPrimary = colorScheme.onPrimary)
    }
    val transition = rememberInfiniteTransition(label = "UnpluggedCableScene")
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "PlugBob",
    )
    Box(modifier = modifier) {
        SceneLayer(background)
        SceneLayer(
            vector = plug,
            modifier = Modifier.graphicsLayer {
                translationY = bob * PLUG_TRAVEL_FRACTION * size.height
            },
        )
    }
}

@Composable
private fun RainSceneArt(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    val background = remember(colorScheme) {
        rainBackgroundVector(
            primary = colorScheme.primary,
            onSurface = colorScheme.onSurface,
            primaryContainer = colorScheme.primaryContainer,
        )
    }
    val drops = remember(colorScheme) {
        listOf(
            rainDropsVector1(primary = colorScheme.primary),
            rainDropsVector2(primary = colorScheme.primary),
            rainDropsVector3(primary = colorScheme.primary),
        )
    }
    val transition = rememberInfiniteTransition(label = "RainScene")
    val falls = drops.indices.map { phase ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1_800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset(offsetMillis = phase * 600),
            ),
            label = "RainFall$phase",
        )
    }
    Box(modifier = modifier) {
        SceneLayer(background)
        drops.forEachIndexed { index, vector ->
            val fall by falls[index]
            SceneLayer(
                vector = vector,
                modifier = Modifier.graphicsLayer {
                    translationY = fall * DROP_TRAVEL_FRACTION * size.height
                    alpha = 1f - fall
                },
            )
        }
    }
}

@Composable
private fun BackstageSceneArt(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    val background = remember(colorScheme) {
        backstageBackgroundVector(
            primary = colorScheme.primary,
            onSurface = colorScheme.onSurface,
            primaryContainer = colorScheme.primaryContainer,
            onPrimary = colorScheme.onPrimary,
        )
    }
    val lamp = remember(colorScheme) {
        backstageLampVector(onSurface = colorScheme.onSurface)
    }
    val transition = rememberInfiniteTransition(label = "BackstageScene")
    val swing by transition.animateFloat(
        initialValue = -LAMP_SWING_DEGREES,
        targetValue = LAMP_SWING_DEGREES,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "LampSwing",
    )
    Box(modifier = modifier) {
        SceneLayer(background)
        SceneLayer(
            vector = lamp,
            modifier = Modifier.graphicsLayer {
                rotationZ = swing
                transformOrigin = TransformOrigin(
                    pivotFractionX = BACKSTAGE_LAMP_PIVOT_X,
                    pivotFractionY = BACKSTAGE_LAMP_PIVOT_Y,
                )
            },
        )
    }
}

// Every layer shares the full design frame, so stacked layers stay registered under any
// screen size as long as each fills the same bounds.
@Composable
private fun SceneLayer(
    vector: ImageVector,
    modifier: Modifier = Modifier,
) {
    Image(
        imageVector = vector,
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = modifier.fillMaxSize(),
    )
}

private const val PLUG_TRAVEL_FRACTION = 3f / 892f
private const val DROP_TRAVEL_FRACTION = 24f / 892f
private const val LAMP_SWING_DEGREES = 3f

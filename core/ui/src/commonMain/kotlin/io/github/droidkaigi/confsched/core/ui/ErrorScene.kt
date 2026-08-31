package io.github.droidkaigi.confsched.core.ui

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

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
    BoxWithConstraints(modifier = modifier) {
        if (maxWidth < WIDE_SCENE_MIN_WIDTH) {
            when (scene) {
                ErrorScene.UnpluggedCable -> UnpluggedCableSceneArt(Modifier.fillMaxSize())
                ErrorScene.Rain -> RainSceneArt(Modifier.fillMaxSize())
                ErrorScene.Backstage -> BackstageSceneArt(Modifier.fillMaxSize())
            }
        } else {
            WideErrorSceneArt(scene, Modifier.fillMaxSize())
        }
    }
}

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

@Composable
private fun UnpluggedCableSceneArt(modifier: Modifier = Modifier) {
    val colors = rememberSceneColors()
    val background = remember(colors) {
        sceneVector(
            name = "ErrorSceneUnpluggedCableBackground",
            groups = listOf(unpluggedCablePanelPaths, unpluggedCableSubjectPaths),
            colors = colors,
        )
    }
    val plug = remember(colors) {
        sceneVector(
            name = "ErrorSceneUnpluggedCablePlug",
            groups = listOf(unpluggedCablePlugPaths),
            colors = colors,
        )
    }
    val transition = rememberInfiniteTransition(label = "UnpluggedCableScene")
    val bob by transition.animatePlugBob()
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
    val colors = rememberSceneColors()
    val background = remember(colors) {
        sceneVector(
            name = "ErrorSceneRainBackground",
            groups = listOf(rainPanelPaths, rainSubjectPaths),
            colors = colors,
        )
    }
    val drops = remember(colors) {
        rainDropsPhasePaths.mapIndexed { index, paths ->
            sceneVector(
                name = "ErrorSceneRainDrops$index",
                groups = listOf(paths),
                colors = colors,
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "RainScene")
    val falls = listOf(
        transition.animateRainFall(0),
        transition.animateRainFall(1),
        transition.animateRainFall(2),
    )
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
    val colors = rememberSceneColors()
    val background = remember(colors) {
        sceneVector(
            name = "ErrorSceneBackstageBackground",
            groups = listOf(
                backstageLeftLegPaths,
                backstageRightLegPaths,
                backstageValancePaths,
                backstageStagePaths,
            ),
            colors = colors,
        )
    }
    val lamp = remember(colors) {
        sceneVector(
            name = "ErrorSceneBackstageLamp",
            groups = listOf(backstageLampPaths),
            colors = colors,
        )
    }
    val transition = rememberInfiniteTransition(label = "BackstageScene")
    val swing by transition.animateLampSwing()
    Box(modifier = modifier) {
        SceneLayer(background)
        SceneLayer(
            vector = lamp,
            modifier = Modifier.graphicsLayer {
                rotationZ = swing
                transformOrigin = TransformOrigin(
                    pivotFractionX = BACKSTAGE_LAMP_PIVOT_FRAME_X / SCENE_FRAME_WIDTH,
                    pivotFractionY = BACKSTAGE_LAMP_PIVOT_FRAME_Y / SCENE_FRAME_HEIGHT,
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

internal const val PLUG_TRAVEL_FRACTION = 3f / SCENE_FRAME_HEIGHT
internal const val DROP_TRAVEL_FRACTION = 24f / SCENE_FRAME_HEIGHT
private const val LAMP_SWING_DEGREES = 3f

// The design switches to the full-bleed scenery on expanded layouts.
private val WIDE_SCENE_MIN_WIDTH = 600.dp

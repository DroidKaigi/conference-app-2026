package io.github.droidkaigi.confsched.core.ui

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

// The full-bleed scenery for expanded layouts. The frame's vertical skeleton is the phone's: a
// uniform scale maps the 892-tall frame onto the layer, and the width that scale uncovers is
// composed rather than stretched — the subjects stay centered at their authored spacing,
// periodic elements tile at their authored density, and the wave edges are drawn at runtime
// from the design's cell recipe, so no vector is stretched mechanically.

@Composable
internal fun WideErrorSceneArt(
    scene: ErrorScene,
    modifier: Modifier = Modifier,
) {
    when (scene) {
        ErrorScene.UnpluggedCable -> WideUnpluggedCableSceneArt(modifier)
        ErrorScene.Rain -> WideRainSceneArt(modifier)
        ErrorScene.Backstage -> WideBackstageSceneArt(modifier)
    }
}

@Composable
private fun WideUnpluggedCableSceneArt(modifier: Modifier = Modifier) {
    val colors = rememberSceneColors()
    val subjects = remember(unpluggedCableSubjectPaths::render)
    val plug = remember(unpluggedCablePlugPaths::render)
    val transition = rememberInfiniteTransition(label = "WideUnpluggedCableScene")
    val bob by transition.animatePlugBob()
    Box(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    withSceneFrame { frameWidth ->
                        drawEdgedPanel(CABLE_EDGE, frameWidth, colors.primary)
                        translate(left = subjectShift(frameWidth)) {
                            drawScenePaths(subjects, colors)
                        }
                    }
                },
        )
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = bob * PLUG_TRAVEL_FRACTION * size.height
                }
                .drawBehind {
                    withSceneFrame { frameWidth ->
                        translate(left = subjectShift(frameWidth)) {
                            drawScenePaths(plug, colors)
                        }
                    }
                },
        )
    }
}

@Composable
private fun WideRainSceneArt(modifier: Modifier = Modifier) {
    val colors = rememberSceneColors()
    val subjects = remember(rainSubjectPaths::render)
    val dropPhases = remember { rainDropsPhasePaths.map(List<ScenePath>::render) }
    val transition = rememberInfiniteTransition(label = "WideRainScene")
    val falls = listOf(
        transition.animateRainFall(0),
        transition.animateRainFall(1),
        transition.animateRainFall(2),
    )
    Box(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    withSceneFrame { frameWidth ->
                        drawEdgedPanel(RAIN_EDGE, frameWidth, colors.primary)
                        translate(left = subjectShift(frameWidth)) {
                            drawScenePaths(subjects, colors)
                        }
                    }
                },
        )
        dropPhases.forEachIndexed { index, drops ->
            val fall by falls[index]
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = fall * DROP_TRAVEL_FRACTION * size.height
                        alpha = 1f - fall
                    }
                    .drawBehind {
                        withSceneFrame { frameWidth ->
                            forEachRainTile(frameWidth) { offset ->
                                translate(left = offset) {
                                    drawScenePaths(drops, colors)
                                }
                            }
                        }
                    },
            )
        }
    }
}

@Composable
private fun WideBackstageSceneArt(modifier: Modifier = Modifier) {
    val colors = rememberSceneColors()
    val leftLeg = remember(backstageLeftLegPaths::render)
    val rightLeg = remember(backstageRightLegPaths::render)
    val stage = remember(backstageStagePaths::render)
    val lamp = remember(backstageLampPaths::render)
    val transition = rememberInfiniteTransition(label = "WideBackstageScene")
    val swing by transition.animateLampSwing()
    Box(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    withSceneFrame { frameWidth ->
                        drawScenePaths(leftLeg, colors)
                        translate(left = frameWidth - SCENE_FRAME_WIDTH) {
                            drawScenePaths(rightLeg, colors)
                        }
                        drawEdgedPanel(VALANCE_EDGE, frameWidth, colors.primary)
                        translate(left = subjectShift(frameWidth)) {
                            drawScenePaths(stage, colors)
                        }
                    }
                },
        )
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val frameScale = size.height / SCENE_FRAME_HEIGHT
                    val frameWidth = size.width / frameScale
                    rotationZ = swing
                    transformOrigin = TransformOrigin(
                        pivotFractionX =
                        (BACKSTAGE_LAMP_PIVOT_FRAME_X + subjectShift(frameWidth)) / frameWidth,
                        pivotFractionY = BACKSTAGE_LAMP_PIVOT_FRAME_Y / SCENE_FRAME_HEIGHT,
                    )
                }
                .drawBehind {
                    withSceneFrame { frameWidth ->
                        translate(left = subjectShift(frameWidth)) {
                            drawScenePaths(lamp, colors)
                        }
                    }
                },
        )
    }
}

// A uniform scale maps the frame's height onto the layer; the frame then widens to whatever
// the layer's width needs, and every layer derives the same frame so they stay registered.
private inline fun DrawScope.withSceneFrame(block: DrawScope.(frameWidth: Float) -> Unit) {
    val frameScale = size.height / SCENE_FRAME_HEIGHT
    val frameWidth = size.width / frameScale
    scale(scaleX = frameScale, scaleY = frameScale, pivot = Offset.Zero) {
        block(frameWidth)
    }
}

private fun subjectShift(frameWidth: Float): Float = (frameWidth - SCENE_FRAME_WIDTH) / 2f

// The rain field repeats at the design's authored period, so a wider frame gains drops at the
// same density instead of spreading the authored ones out.
private inline fun DrawScope.forEachRainTile(frameWidth: Float, block: DrawScope.(offset: Float) -> Unit) {
    val center = subjectShift(frameWidth)
    val first = floor((-EDGE_BLEED - center - SCENE_FRAME_WIDTH) / RAIN_TILE_PERIOD).toInt() + 1
    val last = floor((frameWidth + EDGE_BLEED - center) / RAIN_TILE_PERIOD).toInt()
    for (tile in first..last) {
        block(center + tile * RAIN_TILE_PERIOD)
    }
}

// One wave edge as the design's cell recipe states it: the cell count comes from the width,
// never from stretching a vector, and the curve returns to its baseline at every cell boundary.
private class SceneEdge(
    val baseline: Float,
    val amplitude: Float,
    val amplitudeJitter: Float,
    val wavelength: Float,
    val samplesPerCell: Int,
    val seed: Int,
    val scallop: Boolean,
)

private val CABLE_EDGE = SceneEdge(
    baseline = 596f,
    amplitude = 5.95f,
    amplitudeJitter = 0f,
    wavelength = 68f,
    samplesPerCell = 17,
    seed = 2601,
    scallop = false,
)

private val RAIN_EDGE = SceneEdge(
    baseline = 222f,
    amplitude = 34.6f,
    amplitudeJitter = 2.3f,
    wavelength = 68f,
    samplesPerCell = 14,
    seed = 3101,
    scallop = true,
)

private val VALANCE_EDGE = SceneEdge(
    baseline = 127f,
    amplitude = 21.9f,
    amplitudeJitter = 1.0f,
    wavelength = 58f,
    samplesPerCell = 18,
    seed = 4101,
    scallop = true,
)

private fun DrawScope.drawEdgedPanel(edge: SceneEdge, frameWidth: Float, color: Color) {
    val cells = (frameWidth / edge.wavelength).roundToInt().coerceAtLeast(1)
    val cellWidth = frameWidth / cells
    val path = Path()
    path.moveTo(-EDGE_BLEED, -EDGE_BLEED)
    path.lineTo(-EDGE_BLEED, edge.baseline)
    for (cell in 0 until cells) {
        val depth = if (edge.scallop) {
            edge.amplitude + edge.amplitudeJitter * hashNoise(edge.seed, cell)
        } else {
            edge.amplitude * (0.75f + 0.25f * hashNoise(edge.seed, cell))
        }
        for (sample in 1..edge.samplesPerCell) {
            val fraction = sample.toFloat() / edge.samplesPerCell
            val x = (cell + fraction) * cellWidth
            val y = if (edge.scallop) {
                edge.baseline + depth / 2f * (1f - cos(2f * PI.toFloat() * fraction))
            } else {
                edge.baseline + depth * sin(2f * PI.toFloat() * fraction)
            }
            path.lineTo(x, y)
        }
    }
    path.lineTo(frameWidth + EDGE_BLEED, edge.baseline)
    path.lineTo(frameWidth + EDGE_BLEED, -EDGE_BLEED)
    path.close()
    drawPath(path, color)
}

private const val EDGE_BLEED = 8f
private const val RAIN_TILE_PERIOD = 376f

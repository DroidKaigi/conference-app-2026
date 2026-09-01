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
import kotlin.math.floor

// The scenery, composed at whatever size the layer has: a uniform scale maps the 892-tall frame
// onto the layer, and the width that scale uncovers is composed rather than stretched — the
// subjects stay centered at their authored spacing, and the periodic parts (the rain field, the
// wave edges) tile at their authored shape and density, so the drawing never distorts.

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

@Composable
private fun UnpluggedCableSceneArt(modifier: Modifier = Modifier) {
    val colors = rememberSceneColors()
    val subjects = remember(unpluggedCableSubjectPaths::render)
    val plug = remember(unpluggedCablePlugPaths::render)
    val transition = rememberInfiniteTransition(label = "UnpluggedCableScene")
    val bob by transition.animatePlugBob()
    Box(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    withSceneFrame { frameWidth ->
                        drawEdgedPanel(unpluggedCableEdgeHeights, frameWidth, colors.primary)
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
private fun RainSceneArt(modifier: Modifier = Modifier) {
    val colors = rememberSceneColors()
    val subjects = remember(rainSubjectPaths::render)
    val dropPhases = remember { rainDropsPhasePaths.map(List<ScenePath>::render) }
    val transition = rememberInfiniteTransition(label = "RainScene")
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
                        drawEdgedPanel(rainEdgeHeights, frameWidth, colors.primary)
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
private fun BackstageSceneArt(modifier: Modifier = Modifier) {
    val colors = rememberSceneColors()
    val leftLeg = remember(backstageLeftLegPaths::render)
    val rightLeg = remember(backstageRightLegPaths::render)
    val stage = remember(backstageStagePaths::render)
    val lamp = remember(backstageLampPaths::render)
    val transition = rememberInfiniteTransition(label = "BackstageScene")
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
                        drawEdgedPanel(backstageEdgeHeights, frameWidth, colors.primary)
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

// The panel edges repeat the authored 412-frame wave — its tail blended into its head at
// generation time — so a wider frame keeps the hand-drawn shape instead of a synthesized one.
private fun DrawScope.drawEdgedPanel(edge: FloatArray, frameWidth: Float, color: Color) {
    val path = Path()
    path.moveTo(-EDGE_BLEED, -EDGE_BLEED)
    path.lineTo(-EDGE_BLEED, edgeHeight(edge, -EDGE_BLEED))
    var x = -EDGE_BLEED
    while (x <= frameWidth + EDGE_BLEED) {
        path.lineTo(x, edgeHeight(edge, x))
        x += 1f
    }
    path.lineTo(frameWidth + EDGE_BLEED, -EDGE_BLEED)
    path.close()
    drawPath(path, color)
}

private fun edgeHeight(edge: FloatArray, x: Float): Float {
    val period = edge.size.toFloat()
    val positionInTile = ((x % period) + period) % period
    val index = positionInTile.toInt() % edge.size
    val next = edge[(index + 1) % edge.size]
    return edge[index] + (next - edge[index]) * (positionInTile - index)
}

private const val EDGE_BLEED = 8f
private const val RAIN_TILE_PERIOD = 376f

package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.sketchBorder

/**
 * The wobbly rounded-rect plate every face of the finished card is drawn on: a fixed-size,
 * fixed-palette card that a user's chosen [sketchiness] wobbles by, with two washi-tape corners
 * pinning it in place the way both faces in the design share.
 */
@Composable
fun ProfileCardFace(
    sketchiness: Sketchiness,
    seed: Int,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier) {
        val shortSide = minOf(ProfileCardFaceDefaults.size.width, ProfileCardFaceDefaults.size.height)
        val shape = SketchRoundRectShape(
            seed = seed,
            roughness = profileCardRoughness(shortSide, sketchiness),
            tremor = profileCardTremor(shortSide, sketchiness),
            sweepWavelength = ProfileCardSweepWavelength,
            cornerRadius = ProfileCardFaceDefaults.cornerRadius,
            borderThickness = ProfileCardFaceDefaults.borderThickness,
            referenceSize = ProfileCardFaceDefaults.size,
        )
        Box(
            modifier = Modifier
                .size(ProfileCardFaceDefaults.size)
                .clip(shape)
                .background(ProfileCardColors.plate)
                .sketchBorder(shape, ProfileCardColors.ink),
            content = content,
        )
        // Figma's Rotation field is the mirror of Modifier.rotate's sign (its positive turns the
        // layer counterclockwise); these negate the source's -14°/9° to match the render.
        WashiTape(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-30).dp, y = (-17).dp)
                .rotate(14f),
        )
        WashiTape(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 15.dp, y = (-8).dp)
                .rotate(-9f),
        )
    }
}

@Composable
private fun WashiTape(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = ProfileCardFaceDefaults.tapeWidth, height = ProfileCardFaceDefaults.tapeHeight)
            .background(ProfileCardColors.banner.copy(alpha = 0.8f)),
    )
}

/** A small hand-drawn "+" mark, scattered around a face as a decorative flourish. */
@Composable
fun SketchSparkle(
    modifier: Modifier = Modifier,
    color: Color = ProfileCardColors.ink,
    markSize: Dp = ProfileCardFaceDefaults.sparkleSize,
) {
    Canvas(modifier = modifier.size(markSize)) {
        val half = size.minDimension / 2f
        val strokeWidth = size.minDimension * 0.12f
        drawLine(color, Offset(half, 0f), Offset(half, size.height), strokeWidth, StrokeCap.Round)
        drawLine(color, Offset(0f, half), Offset(size.width, half), strokeWidth, StrokeCap.Round)
    }
}

/**
 * One hand-drawn "+" mark's placement: [x]/[y] are the mark's *centre*, in dp from its face's
 * top-start corner — callers must offset [SketchSparkle] by half of [size] in both axes, since
 * `Modifier.offset` positions a composable's top-start corner, not its centre.
 */
internal data class SparklePlacement(val x: Dp, val y: Dp, val size: Dp, val rotationDegrees: Float)

/**
 * A region from the top of its bounds down to a hand-drawn horizontal edge — the paper-cut "torn
 * edge" traced from the Figma source's own vector path (cubic-bezier segments between fixed
 * points, not an algorithmic wave). [sourceWidth]/[sourceHeight] are that vector's own bounds;
 * callers size the composable to the same aspect ratio so the trace isn't stretched.
 */
internal class TracedEdgeShape(
    private val sourceWidth: Float,
    private val sourceHeight: Float,
    private val edgeStartY: Float,
    private val edge: List<EdgeSegment>,
) : Shape {
    internal data class EdgeSegment(val c1x: Float, val c1y: Float, val c2x: Float, val c2y: Float, val x: Float, val y: Float)

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val scaleX = size.width / sourceWidth
        val scaleY = size.height / sourceHeight
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, edgeStartY * scaleY)
            for (segment in edge) {
                cubicTo(
                    segment.c1x * scaleX,
                    segment.c1y * scaleY,
                    segment.c2x * scaleX,
                    segment.c2y * scaleY,
                    segment.x * scaleX,
                    segment.y * scaleY,
                )
            }
            close()
        }
        return Outline.Generic(path)
    }
}

object ProfileCardFaceDefaults {
    val size = DpSize(320.dp, 480.dp)
    val cornerRadius = 16.dp
    val borderThickness = 2.5.dp
    val tapeWidth = 88.dp
    val tapeHeight = 25.dp
    val sparkleSize = 12.dp
}

/**
 * The roughness/tremor split of the hand-drawn amplitude a shape with [shortSide] draws its
 * wobble at: `amplitude = clamp(shortSide × 0.027, 0.3, 1.6) × E`, where `E` is
 * [Sketchiness.amplitudeMultiplier]; filled shapes (as opposed to stroked outlines) use 0.35 of
 * that. The split itself mirrors the sketchiness picker's fixed roughness (0.4dp) to tremor
 * (0.15dp) ratio.
 */
private fun profileCardAmplitude(shortSide: Dp, sketchiness: Sketchiness, filled: Boolean = false): Dp {
    val base = (shortSide.value * 0.027f).coerceIn(0.3f, 1.6f) * sketchiness.amplitudeMultiplier
    return (if (filled) base * 0.35f else base).dp
}

internal fun profileCardRoughness(shortSide: Dp, sketchiness: Sketchiness, filled: Boolean = false): Dp =
    profileCardAmplitude(shortSide, sketchiness, filled) * 0.73f

internal fun profileCardTremor(shortSide: Dp, sketchiness: Sketchiness, filled: Boolean = false): Dp =
    profileCardAmplitude(shortSide, sketchiness, filled) * 0.27f

/** The wobble control-point spacing the design spec's hand-drawn border rule calls for. */
internal val ProfileCardSweepWavelength = 90.dp

@Preview
@Composable
private fun ProfileCardFacePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Box(modifier = Modifier.padding(32.dp)) {
            ProfileCardFace(sketchiness = Sketchiness.Normal, seed = 900) {
                SketchSparkle(modifier = Modifier.align(Alignment.TopEnd).padding(24.dp))
                Text("droidkaigi", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

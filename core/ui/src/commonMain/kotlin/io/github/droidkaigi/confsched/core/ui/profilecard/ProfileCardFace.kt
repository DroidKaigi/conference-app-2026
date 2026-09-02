package io.github.droidkaigi.confsched.core.ui.profilecard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.droidkaigi.confsched.core.designsystem.KaigiTextStyles
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.PaperGrain
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.MirroredSketchShape
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.paperGrain
import io.github.droidkaigi.confsched.core.ui.sketchBorder

/**
 * The wobbly rounded-rect plate every face of the finished card is drawn on: a fixed-size,
 * fixed-palette card that a user's chosen [sketchiness] wobbles by, pinned in place by a
 * washi-tape corner when [bottomEndTape] is set; [topStartTape] adds the second, top-start piece
 * the front face carries and the back face does not. The card a reader turns over in the app
 * carries no tape at all, only the one pasted onto the share image does. [mirrored] flips the outline left-to-right for the back face, which is
 * the front turned over and so must show the same edge.
 */
@Composable
fun ProfileCardFace(
    sketchiness: Sketchiness,
    paperGrain: PaperGrain,
    outlineSeed: Int,
    topStartTape: Boolean,
    bottomEndTape: Boolean,
    mirrored: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val scale = if (constraints.hasBoundedWidth && constraints.hasBoundedHeight) {
            minOf(
                maxWidth / ProfileCardFaceDefaults.size.width,
                maxHeight / ProfileCardFaceDefaults.size.height,
            ).coerceAtMost(ProfileCardFaceDefaults.maxScale)
        } else {
            1f
        }
        val density = LocalDensity.current
        // Every face is laid out against the fixed card, so the window scales the density the
        // card is laid out in rather than reflowing what is on it; unlike a graphics-layer scale,
        // that keeps text and vector art rasterized at their final pixel size.
        CompositionLocalProvider(LocalDensity provides Density(density.density * scale, density.fontScale)) {
            Box(modifier = Modifier.size(ProfileCardFaceDefaults.size)) {
                ProfileCardFaceContent(
                    sketchiness = sketchiness,
                    paperGrain = paperGrain,
                    outlineSeed = outlineSeed,
                    topStartTape = topStartTape,
                    bottomEndTape = bottomEndTape,
                    mirrored = mirrored,
                    content = content,
                )
            }
        }
    }
}

@Composable
private fun ProfileCardFaceContent(
    sketchiness: Sketchiness,
    paperGrain: PaperGrain,
    outlineSeed: Int,
    topStartTape: Boolean,
    bottomEndTape: Boolean,
    mirrored: Boolean,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = Modifier.requiredSize(ProfileCardFaceDefaults.size)) {
        val shortSide = minOf(ProfileCardFaceDefaults.size.width, ProfileCardFaceDefaults.size.height)
        val outline = SketchRoundRectShape(
            seed = outlineSeed,
            roughness = profileCardRoughness(shortSide, sketchiness),
            tremor = profileCardTremor(shortSide, sketchiness),
            sweepWavelength = ProfileCardSweepWavelength,
            cornerRadius = ProfileCardFaceDefaults.cornerRadius,
            borderThickness = ProfileCardFaceDefaults.borderThickness,
            referenceSize = ProfileCardFaceDefaults.size,
        )
        val shape = if (mirrored) MirroredSketchShape(outline) else outline
        Box(
            modifier = Modifier
                .size(ProfileCardFaceDefaults.size)
                .clip(shape)
                .background(ProfileCardColors.plate),
        ) {
            content()
            // The outline goes on last so a band filling an edge cannot paint over it.
            Box(modifier = Modifier.matchParentSize().sketchBorder(shape, ProfileCardColors.ink))
            if (paperGrain.grainAlpha > 0f) {
                // Grain over the ink as well: print sits under the paper fibre, not on top of it.
                Box(modifier = Modifier.matchParentSize().paperGrain(alpha = paperGrain.grainAlpha))
            }
        }
        if (topStartTape) {
            WashiTape(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(
                        x = ProfileCardFaceDefaults.topStartTapeOffset.x,
                        y = ProfileCardFaceDefaults.topStartTapeOffset.y,
                    )
                    .rotate(ProfileCardFaceDefaults.topStartTapeRotationDegrees),
            )
        }
        if (bottomEndTape) {
            WashiTape(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(
                        x = ProfileCardFaceDefaults.bottomEndTapeOffset.x,
                        y = ProfileCardFaceDefaults.bottomEndTapeOffset.y,
                    )
                    .rotate(ProfileCardFaceDefaults.bottomEndTapeRotationDegrees),
            )
        }
    }
}

/** Lays [content] out in the card's own fixed dp space, scaled by [scale] the way [ProfileCardFace] does. */
@Composable
internal fun ProfileCardFaceSpace(
    scale: Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current
    Box(modifier = modifier) {
        CompositionLocalProvider(LocalDensity provides Density(density.density * scale, density.fontScale)) {
            Box(modifier = Modifier.size(ProfileCardFaceDefaults.size), content = content)
        }
    }
}

@Composable
private fun WashiTape(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = ProfileCardFaceDefaults.tapeWidth, height = ProfileCardFaceDefaults.tapeHeight)
            .background(ProfileCardColors.banner.copy(alpha = ProfileCardFaceDefaults.tapeAlpha)),
    )
}

/**
 * The corner marks and event label heading both faces, in [color] against whichever band that
 * face fills its top with. [centeredLabel] centres the label across the card instead of setting
 * it against the start bracket.
 */
@Composable
internal fun BoxScope.EventLabelHeader(text: String, color: Color, centeredLabel: Boolean) {
    val offset = ProfileCardFaceDefaults.bracketOffset
    CornerBracket(
        mirrored = false,
        color = color,
        modifier = Modifier.align(Alignment.TopStart).offset(x = offset.x, y = offset.y),
    )
    CornerBracket(
        mirrored = true,
        color = color,
        modifier = Modifier.align(Alignment.TopEnd).offset(x = -offset.x, y = offset.y),
    )
    val labelOffset = ProfileCardFaceDefaults.eventLabelOffset
    Text(
        text = text,
        color = color,
        style = ProfileCardTextStyles.eventLabel,
        textAlign = if (centeredLabel) TextAlign.Center else TextAlign.Start,
        modifier = Modifier
            .align(Alignment.TopStart)
            .then(if (centeredLabel) Modifier.fillMaxWidth() else Modifier)
            .offset(x = if (centeredLabel) 0.dp else labelOffset.x, y = labelOffset.y),
    )
}

/** A camera-viewfinder-style corner mark; [mirrored] opens it to the left instead of the right. */
@Composable
private fun CornerBracket(mirrored: Boolean, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(ProfileCardFaceDefaults.bracketSize)) {
        val strokeWidth = size.minDimension * 0.16f
        val inset = strokeWidth / 2f
        val radius = size.minDimension * 0.3f
        val cornerX = if (mirrored) size.width - inset else inset
        val armEndX = if (mirrored) inset else size.width - inset
        val towardArm = if (mirrored) -radius else radius
        val path = Path().apply {
            moveTo(armEndX, inset)
            lineTo(cornerX + towardArm, inset)
            quadraticTo(cornerX, inset, cornerX, inset + radius)
            lineTo(cornerX, size.height - inset)
        }
        drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** Positions a face's child at [offset] from the card's top-start corner, the way the design file lays every face out. */
internal fun Modifier.cardOffset(offset: DpOffset): Modifier = offset(x = offset.x, y = offset.y)

/** Draws each of [placements] centred on its own point, in [color]. */
@Composable
internal fun BoxScope.Sparkles(placements: List<SparklePlacement>, color: Color) {
    placements.forEach { placement ->
        SketchSparkle(
            color = color,
            markSize = placement.size,
            modifier = Modifier
                .cardOffset(DpOffset(placement.x - placement.size / 2, placement.y - placement.size / 2))
                .rotate(placement.rotationDegrees),
        )
    }
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
    val maxScale = 1.5f
    val cornerRadius = 16.dp
    val borderThickness = 2.5.dp
    val tapeWidth = 88.dp
    val tapeHeight = 25.dp
    val tapeAlpha = 0.9f
    val topStartTapeOffset = DpOffset((-30.5).dp, (-9).dp)
    val bottomEndTapeOffset = DpOffset(16.dp, (-1.5).dp)

    // Figma's Rotation field is the mirror of Modifier.rotate's sign (its positive turns the
    // layer counterclockwise); these negate the source's -14°/9° to match the render.
    val topStartTapeRotationDegrees = 14f
    val bottomEndTapeRotationDegrees = -9f
    val sparkleSize = 12.dp

    /** The corner marks framing each face's event label. */
    val bracketSize = 10.dp
    val bracketOffset = DpOffset(13.dp, 21.dp)

    /** The event label's own top-start corner, inset from the bracket it sits beside. */
    val eventLabelOffset = DpOffset(22.5.dp, 26.dp)
}

/**
 * The type roles the card's faces set text in, named after the styles the design file binds:
 * "display/small", "title/small - accent", and "body/small". The accent role is the monospace
 * face at the body scale, which no Material role pairs on its own.
 */
object ProfileCardTextStyles {
    val display: TextStyle
        @Composable get() = MaterialTheme.typography.displaySmall

    val accent: TextStyle
        @Composable get() = KaigiTextStyles.titleSmallAccent

    /** [accent] on the tighter line the event label is set on. */
    val eventLabel: TextStyle
        @Composable get() = accent.copy(lineHeight = 16.sp)

    val caption: TextStyle
        @Composable get() = MaterialTheme.typography.bodySmall
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

fun profileCardRoughness(shortSide: Dp, sketchiness: Sketchiness, filled: Boolean = false): Dp =
    profileCardAmplitude(shortSide, sketchiness, filled) * 0.73f

fun profileCardTremor(shortSide: Dp, sketchiness: Sketchiness, filled: Boolean = false): Dp =
    profileCardAmplitude(shortSide, sketchiness, filled) * 0.27f

/** The wobble control-point spacing the design spec's hand-drawn border rule calls for. */
val ProfileCardSweepWavelength = 90.dp

@Preview
@Composable
private fun ProfileCardFacePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Box(modifier = Modifier.padding(32.dp)) {
            ProfileCardFace(sketchiness = Sketchiness.Normal, paperGrain = PaperGrain.Rough, outlineSeed = 900, topStartTape = true, bottomEndTape = true, mirrored = false) {
                EventLabelHeader(text = "DROIDKAIGI 2026", color = ProfileCardColors.ink, centeredLabel = false)
                Sparkles(
                    placements = listOf(SparklePlacement(x = 280.dp, y = 40.dp, size = 12.dp, rotationDegrees = 12f)),
                    color = ProfileCardColors.ink,
                )
                Text("droidkaigi", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

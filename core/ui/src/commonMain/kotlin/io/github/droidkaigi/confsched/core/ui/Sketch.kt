package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.LocalSketchBaseSeed
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import kotlin.math.roundToInt

/** Combines the app-wide sketch seed with an element's stable seed. */
@Composable
fun combineSketchSeed(seed: Int): Int = LocalSketchBaseSeed.current + seed

/** How finely the tremor octave ripples: shorter is a faster, tighter shake. */
private val DefaultTremorWavelength = 42.dp

/**
 * How far apart the broad sweep turns.
 *
 * A closed outline wraps its noise lattice onto the perimeter and so rounds the wavelength
 * to a whole number of cells. At this figure the components in the design spec land on four
 * to six cells, which is where the parameter starts governing the result instead of being
 * overridden by the floor that keeps a rectangle from reading as a skewed quadrilateral.
 */
private val DefaultSweepWavelength = 140.dp

/**
 * A wavelength divides, so zero would put the noise lookup at infinity, and a
 * negative amplitude reverses the swing and can drive a computed height below zero,
 * which the layout rejects at draw time rather than at the call site.
 */
private fun requireWobble(roughness: Dp, tremor: Dp, sweepWavelength: Dp, tremorWavelength: Dp) {
    require(roughness >= 0.dp) { "roughness must not be negative, was $roughness" }
    require(tremor >= 0.dp) { "tremor must not be negative, was $tremor" }
    require(sweepWavelength > 0.dp) { "sweepWavelength must be positive, was $sweepWavelength" }
    require(tremorWavelength > 0.dp) { "tremorWavelength must be positive, was $tremorWavelength" }
}
private val DefaultRoughness = 1.dp
private val DefaultTremor = 0.3.dp

// Swept as the horizontal axis of both taste grids, so the divider grid and the
// border grid can be read against each other.
private val TREMOR_STEPS = listOf(0.dp, 0.3.dp, 1.dp)

/**
 * A horizontal rule drawn as a single hand-sketched stroke.
 *
 * The same [seed] always produces the same line, so the stroke stays stable
 * across recompositions; use a different [seed] per call site to avoid two
 * neighbouring dividers sharing an identical wobble.
 *
 * [roughness] sets how far the broad sweep departs from a straight line, [tremor]
 * the swing of the finer wobble laid over it, and [tremorWavelength] how tightly
 * that wobble ripples.
 */
@Composable
fun SketchHorizontalDivider(
    seed: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline,
    thickness: Dp = 2.dp,
    roughness: Dp = DefaultRoughness,
    tremor: Dp = DefaultTremor,
    sweepWavelength: Dp = DefaultSweepWavelength,
    tremorWavelength: Dp = DefaultTremorWavelength,
) {
    requireWobble(roughness, tremor, sweepWavelength, tremorWavelength)
    require(thickness >= 0.dp) { "thickness must not be negative, was $thickness" }
    val combinedSeed = combineSketchSeed(seed)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness + (roughness + tremor) * 2)
            .drawWithCache {
                val path = sketchHorizontalLinePath(
                    width = size.width,
                    centerY = size.height / 2f,
                    roughness = roughness,
                    tremor = tremor,
                    sweepWavelength = sweepWavelength,
                    tremorWavelength = tremorWavelength,
                    seed = combinedSeed,
                )
                val stroke = Stroke(width = thickness.toPx(), cap = StrokeCap.Round)
                onDrawBehind {
                    drawPath(path = path, color = color, style = stroke)
                }
            },
    )
}

/**
 * A vertical rule drawn as a single hand-sketched stroke.
 *
 * The line [SketchHorizontalDivider] draws, running down instead of across: a given
 * [seed] describes the same wobble either way.
 */
@Composable
fun SketchVerticalDivider(
    seed: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline,
    thickness: Dp = 2.dp,
    roughness: Dp = DefaultRoughness,
    tremor: Dp = DefaultTremor,
    sweepWavelength: Dp = DefaultSweepWavelength,
    tremorWavelength: Dp = DefaultTremorWavelength,
) {
    requireWobble(roughness, tremor, sweepWavelength, tremorWavelength)
    require(thickness >= 0.dp) { "thickness must not be negative, was $thickness" }
    val combinedSeed = combineSketchSeed(seed)
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(thickness + (roughness + tremor) * 2)
            .drawWithCache {
                val path = sketchVerticalLinePath(
                    height = size.height,
                    centerX = size.width / 2f,
                    roughness = roughness,
                    tremor = tremor,
                    sweepWavelength = sweepWavelength,
                    tremorWavelength = tremorWavelength,
                    seed = combinedSeed,
                )
                val stroke = Stroke(width = thickness.toPx(), cap = StrokeCap.Round)
                onDrawBehind {
                    drawPath(path = path, color = color, style = stroke)
                }
            },
    )
}

/**
 * A vertical wavy line, for a timeline running down a column.
 *
 * The caller sets the height; the width follows from the reach of the widest crest.
 * [noiseAmount] decides how mechanical the ripple looks, from an even sine at `0`
 * to visibly uneven crests at `1`, and it swells [amplitude] as well as shrinking it,
 * so the line needs `amplitude * (1 + noiseAmount)` either side of centre.
 */
@Composable
fun SketchVerticalWavyLine(
    seed: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline,
    thickness: Dp = 1.dp,
    amplitude: Dp = 3.dp,
    wavelength: Dp = 10.dp,
    noiseAmount: Float = 0.8f,
) {
    require(thickness >= 0.dp) { "thickness must not be negative, was $thickness" }
    require(amplitude >= 0.dp) { "amplitude must not be negative, was $amplitude" }
    require(wavelength > 0.dp) { "wavelength must be positive, was $wavelength" }
    require(noiseAmount >= 0f) { "noiseAmount must not be negative, was $noiseAmount" }
    val combinedSeed = combineSketchSeed(seed)
    Box(
        modifier = modifier
            .width(amplitude * (1f + noiseAmount) * 2 + thickness)
            .drawWithCache {
                val path = sketchVerticalWavyLinePath(
                    height = size.height,
                    centerX = size.width / 2f,
                    amplitude = amplitude,
                    wavelength = wavelength,
                    noiseAmount = noiseAmount,
                    seed = combinedSeed,
                )
                val stroke = Stroke(width = thickness.toPx(), cap = StrokeCap.Round)
                onDrawBehind {
                    drawPath(path = path, color = color, style = stroke)
                }
            },
    )
}

/**
 * Outlines the content along the very outline [shape] reports, stroked at the
 * [SketchOutlineShape.borderThickness] the shape reserves its inset for, so content clipped to
 * the same instance meets the stroke down its centre.
 *
 * Compose's `Modifier.border` is not a substitute: handed an [Outline.Generic] it strokes
 * into an offscreen mask whose clear pass misses fractional path bounds, leaving a stray
 * line along the right and bottom edges of the layout.
 */
fun Modifier.sketchBorder(shape: SketchOutlineShape, color: Color): Modifier {
    require(shape.borderThickness > 0.dp) {
        "sketchBorder needs a shape with a positive borderThickness, was ${shape.borderThickness}"
    }
    return drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        val stroke = Stroke(
            width = shape.borderThickness.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        onDrawBehind {
            drawOutline(outline = outline, color = color, style = stroke)
        }
    }
}

/**
 * A hand-sketched closed outline that reserves room for the stroke [Modifier.sketchBorder]
 * draws it at, so content clipped to the same instance meets that stroke down its centre.
 */
interface SketchOutlineShape : Shape {
    val borderThickness: Dp
}

/**
 * The hand-sketched rectangle as a [Shape], for `Modifier.clip`, `Modifier.background`,
 * any `shape` parameter, and [Modifier.sketchBorder].
 *
 * The outline is inset by the swing plus half of [borderThickness], so both the wobble
 * and the stroke [Modifier.sketchBorder] draws over this same instance stay inside the
 * bounds, with the fill meeting that stroke down its centre. Leave [borderThickness] at
 * zero for a fill no border accompanies.
 *
 * On a box too small to hold the requested swing, both octaves shrink together rather
 * than letting the outline fold onto itself.
 *
 * Set [referenceSize] on anything whose size animates. The noise wraps onto the outline in
 * a whole number of cells, so a box left to derive that count from its own perimeter swaps
 * the wobble for an unrelated one each time the count steps — a flicker along the edge as
 * the box grows. Pinning the count to one size holds a single stroke still and lets it
 * stretch. What the size actually is barely matters; that it stops changing is the point.
 */
@Immutable
data class SketchRoundRectShape(
    val seed: Int,
    val roughness: Dp = DefaultRoughness,
    val tremor: Dp = DefaultTremor,
    val sweepWavelength: Dp = DefaultSweepWavelength,
    val tremorWavelength: Dp = DefaultTremorWavelength,
    val cornerRadius: Dp = 0.dp,
    override val borderThickness: Dp = 0.dp,
    val referenceSize: DpSize? = null,
) : SketchOutlineShape {
    init {
        requireWobble(roughness, tremor, sweepWavelength, tremorWavelength)
        require(cornerRadius >= 0.dp) { "cornerRadius must not be negative, was $cornerRadius" }
        require(borderThickness >= 0.dp) { "borderThickness must not be negative, was $borderThickness" }
        require(referenceSize == null || (referenceSize.width > 0.dp && referenceSize.height > 0.dp)) {
            "referenceSize must be positive in both dimensions, was $referenceSize"
        }
    }

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline = with(density) {
        val ratio = swingCapRatio(size.width, size.height, roughness, tremor, borderThickness)
        val effectiveRoughness = roughness * ratio
        val effectiveTremor = tremor * ratio
        val inset = (effectiveRoughness + effectiveTremor).toPx() + borderThickness.toPx() / 2f
        val width = size.width - inset * 2f
        val height = size.height - inset * 2f
        if (width <= 0f || height <= 0f) return Outline.Rectangle(size.toRect())
        val lattice = latticeOutline()

        val path = sketchRoundRectPath(
            width = width,
            height = height,
            cornerRadius = cornerRadius,
            roughness = effectiveRoughness,
            tremor = effectiveTremor,
            sweepWavelength = sweepWavelength,
            tremorWavelength = tremorWavelength,
            seed = seed,
            latticeWidth = lattice?.width ?: width,
            latticeHeight = lattice?.height ?: height,
        )
        path.translate(Offset(inset, inset))
        Outline.Generic(path)
    }

    /** The inset outline of [referenceSize], or `null` when no reference size was given. */
    private fun Density.latticeOutline(): Size? {
        val reference = referenceSize ?: return null
        val width = reference.width.toPx()
        val height = reference.height.toPx()
        val ratio = swingCapRatio(width, height, roughness, tremor, borderThickness)
        val inset = ((roughness + tremor) * ratio).toPx() + borderThickness.toPx() / 2f
        val insetWidth = width - inset * 2f
        val insetHeight = height - inset * 2f
        return if (insetWidth > 0f && insetHeight > 0f) Size(insetWidth, insetHeight) else null
    }
}

@Preview
@Composable
private fun SketchHorizontalDividerPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PreviewSurface {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(4) { index ->
                    SketchHorizontalDivider(seed = index)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SketchHorizontalDivider(seed = 10, modifier = Modifier.width(80.dp))
                    SketchHorizontalDivider(seed = 11, modifier = Modifier.width(160.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun SketchHorizontalDividerTastePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PreviewSurface {
            DividerTasteGrid()
        }
    }
}

@Composable
private fun DividerTasteGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        DividerTasteRow(roughness = 0.5.dp)
        DividerTasteRow(roughness = 1.dp)
        DividerTasteRow(roughness = 3.dp)
    }
}

@Composable
private fun DividerTasteRow(roughness: Dp) {
    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
        TREMOR_STEPS.forEach { tremor ->
            LabelledSample(label = "${roughness.value.toInt()}dp / $tremor") {
                SketchHorizontalDivider(
                    seed = 7,
                    modifier = Modifier.width(150.dp),
                    roughness = roughness,
                    tremor = tremor,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SketchHorizontalDividerFinenessPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PreviewSurface {
            FinenessSamples()
        }
    }
}

@Composable
private fun FinenessSamples() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(80.dp, 42.dp, 20.dp, 10.dp).forEach { wavelength ->
            LabelledSample(label = "${wavelength.value.toInt()}dp") {
                SketchHorizontalDivider(
                    seed = 4,
                    modifier = Modifier.width(320.dp),
                    tremor = 1.dp,
                    tremorWavelength = wavelength,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SketchHorizontalDividerWeightPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PreviewSurface {
            DividerWeightSamples()
        }
    }
}

@Composable
private fun DividerWeightSamples() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(0.75.dp, 1.5.dp, 3.dp, 6.dp).forEach { thickness ->
            LabelledSample(label = "${thickness.value}dp") {
                SketchHorizontalDivider(
                    seed = 12,
                    modifier = Modifier.width(320.dp),
                    thickness = thickness,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SketchVerticalWavyLinePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PreviewSurface {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                WavyNoiseSamples()
                WavyWavelengthSamples()
            }
        }
    }
}

@Composable
private fun WavyNoiseSamples() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        listOf(0f, 0.4f, 0.8f, 1.2f).forEach { amount ->
            LabelledSample(label = "n=$amount") {
                SketchVerticalWavyLine(
                    seed = 3,
                    modifier = Modifier.height(60.dp),
                    thickness = 2.dp,
                    noiseAmount = amount,
                )
            }
        }
    }
}

@Composable
private fun WavyWavelengthSamples() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        listOf(6.dp, 10.dp, 16.dp, 24.dp).forEach { wavelength ->
            LabelledSample(label = "${wavelength.value.toInt()}dp") {
                SketchVerticalWavyLine(
                    seed = 3,
                    modifier = Modifier.height(60.dp),
                    thickness = 2.dp,
                    wavelength = wavelength,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SketchBorderCornerRadiusPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PreviewSurface {
            CornerRadiusSamples()
        }
    }
}

@Composable
private fun CornerRadiusSamples() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(0.dp, 8.dp, 16.dp, 28.dp).forEachIndexed { index, radius ->
            LabelledSample(label = "r=${radius.value.toInt()}") {
                Box(
                    Modifier
                        .size(90.dp, 64.dp)
                        .sketchBorder(
                            shape = SketchRoundRectShape(
                                seed = combineSketchSeed(20 + index),
                                cornerRadius = radius,
                                borderThickness = 2.dp,
                            ),
                            color = MaterialTheme.colorScheme.outline,
                        ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun SketchBorderTastePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PreviewSurface {
            BorderTasteGrid()
        }
    }
}

@Composable
private fun BorderTasteGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BorderTasteRow(roughness = 0.5.dp)
        BorderTasteRow(roughness = 1.dp)
        BorderTasteRow(roughness = 3.dp)
    }
}

@Composable
private fun BorderTasteRow(roughness: Dp) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        TREMOR_STEPS.forEach { tremor ->
            LabelledSample(label = "${roughness.value.toInt()}dp / $tremor") {
                Box(
                    Modifier
                        .size(132.dp, 84.dp)
                        .sketchBorder(
                            shape = SketchRoundRectShape(
                                seed = combineSketchSeed(9),
                                roughness = roughness,
                                tremor = tremor,
                                cornerRadius = 10.dp,
                                borderThickness = 2.dp,
                            ),
                            color = MaterialTheme.colorScheme.outline,
                        ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun SketchShapePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PreviewSurface {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LabelledSample(label = "clip") {
                    Box(
                        Modifier
                            .size(90.dp, 64.dp)
                            .clip(SketchRoundRectShape(seed = combineSketchSeed(50), cornerRadius = 12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                    )
                }
                LabelledSample(label = "clip + border") {
                    val shape = SketchRoundRectShape(
                        seed = combineSketchSeed(51),
                        cornerRadius = 12.dp,
                        borderThickness = 2.dp,
                    )
                    Box(
                        Modifier
                            .size(90.dp, 64.dp)
                            .clip(shape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .sketchBorder(shape, MaterialTheme.colorScheme.outline),
                    )
                }
                LabelledSample(label = "Surface") {
                    Surface(
                        modifier = Modifier.size(90.dp, 64.dp),
                        shape = SketchRoundRectShape(seed = combineSketchSeed(52), cornerRadius = 20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {}
                }
                LabelledSample(label = "tremor=1") {
                    Box(
                        Modifier
                            .size(90.dp, 64.dp)
                            .clip(
                                SketchRoundRectShape(
                                    seed = combineSketchSeed(53),
                                    tremor = 1.dp,
                                    cornerRadius = 12.dp,
                                ),
                            )
                            .background(MaterialTheme.colorScheme.primaryContainer),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SketchSegmentedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PreviewSurface {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SegmentedSample(selectedFirst = true)
                SegmentedSample(selectedFirst = false)
            }
        }
    }
}

@Composable
private fun SegmentedSample(selectedFirst: Boolean) {
    val shape = SketchRoundRectShape(
        seed = combineSketchSeed(900),
        roughness = 0.4.dp,
        tremor = 0.15.dp,
        cornerRadius = 16.dp,
        borderThickness = 2.dp,
    )
    Box(Modifier.size(208.dp, 40.dp)) {
        // Clipping the fill to the same shape the border strokes is what keeps its edge
        // under the middle of the line instead of beside it.
        Box(Modifier.matchParentSize().clip(shape)) {
            Box(
                Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
                    .align(if (selectedFirst) Alignment.CenterStart else Alignment.CenterEnd)
                    .background(MaterialTheme.colorScheme.primaryContainer),
            )
            // Inside the clip, so the rule stops at the outline instead of running the
            // full height and poking out past the top and bottom of the border.
            SketchVerticalDivider(
                seed = 902,
                modifier = Modifier.align(Alignment.Center),
                thickness = 1.dp,
                roughness = 0.3.dp,
            )
        }
        Box(Modifier.matchParentSize().sketchBorder(shape, MaterialTheme.colorScheme.outline))
    }
}

/**
 * Drag the slider in an interactive preview: the upper outline swaps its wobble out as the
 * lattice steps, the lower one holds a single stroke and stretches it.
 */
@Preview
@Composable
private fun SketchResizePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PreviewSurface {
            ResizeSample()
        }
    }
}

@Composable
private fun ResizeSample() {
    var width by remember { mutableFloatStateOf(296f) }
    val height = 72.dp
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "width = ${width.roundToInt()}dp",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Slider(
            value = width,
            onValueChange = { width = it },
            valueRange = 200f..360f,
            modifier = Modifier.width(320.dp),
        )
        LabelledSample(label = "no referenceSize") {
            Box(
                Modifier
                    .size(width.dp, height)
                    .sketchBorder(
                        shape = SketchRoundRectShape(
                            seed = combineSketchSeed(5),
                            cornerRadius = 16.dp,
                            borderThickness = 2.dp,
                        ),
                        color = MaterialTheme.colorScheme.outline,
                    ),
            )
        }
        LabelledSample(label = "referenceSize = 292×72") {
            Box(
                Modifier
                    .size(width.dp, height)
                    .sketchBorder(
                        shape = SketchRoundRectShape(
                            seed = combineSketchSeed(5),
                            cornerRadius = 16.dp,
                            borderThickness = 2.dp,
                            referenceSize = DpSize(292.dp, height),
                        ),
                        color = MaterialTheme.colorScheme.outline,
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun SketchCardPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PreviewSurface {
            Column(
                Modifier
                    .width(260.dp)
                    .sketchBorder(
                        shape = SketchRoundRectShape(
                            seed = combineSketchSeed(40),
                            cornerRadius = 16.dp,
                            borderThickness = 2.dp,
                        ),
                        color = MaterialTheme.colorScheme.outline,
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "スタンプラリー",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                SketchHorizontalDivider(seed = 41, thickness = 1.5.dp)
                Text(
                    text = "会場をめぐってスタンプを集めましょう",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PreviewSurface(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
    ) {
        content()
    }
}

@Composable
private fun LabelledSample(label: String, content: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        content()
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * The hand-sketched ellipse as a [Shape], for `Modifier.clip`, `Modifier.background`,
 * any `shape` parameter, and [Modifier.sketchBorder].
 *
 * [SketchRoundRectShape] reaches a pill once its corner radius meets the shorter side, which keeps a
 * straight run between the two arcs. This curves through the whole turn instead, so a wide
 * option reads as one drawn loop rather than as a rectangle with rounded ends.
 *
 * @param seed the value the outline is drawn from. The same seed always produces the same
 *   outline.
 * @param roughness the amplitude of the broad swing away from the true ellipse.
 * @param tremor the amplitude of the fine waver carried on top of that swing.
 * @param sweepWavelength the wavelength of the broad swing.
 * @param tremorWavelength the wavelength of the fine waver.
 * @param borderThickness the stroke [Modifier.sketchBorder] draws this at. The outline is
 *   inset by half of it, so the stroke stays inside the bounds.
 * @param referenceSize the size the noise lattice and the anchor count are taken from. Give
 *   it to hold the wobble still while the shape resizes; leave it null to fit both to the
 *   drawn size.
 */
@Immutable
data class SketchEllipseShape(
    val seed: Int,
    val roughness: Dp = DefaultRoughness,
    val tremor: Dp = DefaultTremor,
    val sweepWavelength: Dp = DefaultSweepWavelength,
    val tremorWavelength: Dp = DefaultTremorWavelength,
    override val borderThickness: Dp = 0.dp,
    val referenceSize: DpSize? = null,
) : SketchOutlineShape {
    init {
        requireWobble(roughness, tremor, sweepWavelength, tremorWavelength)
        require(borderThickness >= 0.dp) { "borderThickness must not be negative, was $borderThickness" }
        require(referenceSize == null || (referenceSize.width > 0.dp && referenceSize.height > 0.dp)) {
            "referenceSize must be positive in both dimensions, was $referenceSize"
        }
    }

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline = with(density) {
        val ratio = swingCapRatio(size.width, size.height, roughness, tremor, borderThickness)
        val effectiveRoughness = roughness * ratio
        val effectiveTremor = tremor * ratio
        val inset = (effectiveRoughness + effectiveTremor).toPx() + borderThickness.toPx() / 2f
        val width = size.width - inset * 2f
        val height = size.height - inset * 2f
        if (width <= 0f || height <= 0f) return Outline.Rectangle(size.toRect())
        val lattice = latticeOutline()

        val path = sketchEllipsePath(
            width = width,
            height = height,
            roughness = effectiveRoughness,
            tremor = effectiveTremor,
            sweepWavelength = sweepWavelength,
            tremorWavelength = tremorWavelength,
            seed = seed,
            latticeWidth = lattice?.width ?: width,
            latticeHeight = lattice?.height ?: height,
        )
        path.translate(Offset(inset, inset))
        Outline.Generic(path)
    }

    /** The inset outline of [referenceSize], or `null` when no reference size was given. */
    private fun Density.latticeOutline(): Size? {
        val reference = referenceSize ?: return null
        val width = reference.width.toPx()
        val height = reference.height.toPx()
        val ratio = swingCapRatio(width, height, roughness, tremor, borderThickness)
        val inset = ((roughness + tremor) * ratio).toPx() + borderThickness.toPx() / 2f
        val insetWidth = width - inset * 2f
        val insetHeight = height - inset * 2f
        return if (insetWidth > 0f && insetHeight > 0f) Size(insetWidth, insetHeight) else null
    }
}

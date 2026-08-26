package io.github.droidkaigi.confsched.core.ui

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

// Anchors carry the tremor, so they have to be dense enough to resolve it: roughly four
// to a wavelength. The ceiling keeps a long wavelength from thinning them out until the
// broad sweep itself turns polygonal.
private const val ANCHORS_PER_WAVE = 4f
private val MaxAnchorSpacing = 12.dp

private fun anchorSpacingFor(tremorWavelength: Dp): Dp =
    minOf(tremorWavelength / ANCHORS_PER_WAVE, MaxAnchorSpacing)

// Offsets the tremor octave onto its own sequence, so the two bands stay uncorrelated.
private const val TREMOR_SEED_OFFSET = 100

// Constants of the specified hash: odd multipliers with well-spread bits, and the scale
// that maps its top 24 bits onto [0, 1).
private const val SEED_MULTIPLIER = 374761393
private const val INDEX_MULTIPLIER = 668265263
private const val MIX_MULTIPLIER = 1274126177
private const val HASH_SCALE = 16777216f
private const val QUARTER_TURN = PI.toFloat() / 2f
private const val TWO_PI = PI.toFloat() * 2f

// Anchors per ripple of the wavy line: enough for a Catmull-Rom fit to read as a
// sine rather than a zigzag.
private const val POINTS_PER_WAVE = 6

// Below about four cells the sweep offsets whole edges by a near-constant amount and the
// rectangle tilts into a skewed quadrilateral. Four is where that stops showing at a
// roughness well past anything in use; two is unmistakable.
private const val MIN_SWEEP_CELLS = 4

// The tremor lattice only has to stay a lattice. At one cell the interpolation reads the
// same value at both ends and the octave flattens to a constant offset.
private const val MIN_TREMOR_CELLS = 2

// How far a Catmull-Rom tangent may reach, as a fraction of the segment it belongs to.
// A circular arc needs a shade over a third of its chord however finely it is subdivided,
// so a clamp at or below 1/3 facets every corner it passes through; a small radius drawn
// from few anchors then reads as a polygon. Both values sit above that, leaving the clamp
// to catch only the overshoot a sharp turn in the noise produces.
private const val OUTLINE_TANGENT_CLAMP = 0.36f
private const val WAVY_TANGENT_CLAMP = 0.4f

/**
 * A horizontal line of [width], wobbling around [centerY].
 *
 * [roughness] is the half-span of the broad sweep and [tremor] that of the finer
 * wobble layered on top of it, both in dp, so the line reaches `roughness + tremor`
 * away from [centerY] at most.
 */
internal fun Density.sketchHorizontalLinePath(
    width: Float,
    centerY: Float,
    roughness: Dp,
    tremor: Dp,
    sweepWavelength: Dp,
    tremorWavelength: Dp,
    seed: Int,
): Path {
    val positions = sketchLinePositions(width, tremorWavelength)
    val offsets = sketchLineOffsets(
        positions = positions,
        center = centerY,
        roughness = roughness,
        tremor = tremor,
        sweepWavelength = sweepWavelength,
        tremorWavelength = tremorWavelength,
        seed = seed,
    )
    return openCurveThrough(positions, offsets, OUTLINE_TANGENT_CLAMP)
}

/**
 * A vertical line of [height], wobbling around [centerX].
 *
 * The stroke is the one [sketchHorizontalLinePath] draws laid on the other axis, so a
 * given [seed] describes the same wobble whichever direction the line runs.
 */
internal fun Density.sketchVerticalLinePath(
    height: Float,
    centerX: Float,
    roughness: Dp,
    tremor: Dp,
    sweepWavelength: Dp,
    tremorWavelength: Dp,
    seed: Int,
): Path {
    val positions = sketchLinePositions(height, tremorWavelength)
    val offsets = sketchLineOffsets(
        positions = positions,
        center = centerX,
        roughness = roughness,
        tremor = tremor,
        sweepWavelength = sweepWavelength,
        tremorWavelength = tremorWavelength,
        seed = seed,
    )
    return openCurveThrough(offsets, positions, OUTLINE_TANGENT_CLAMP)
}

/**
 * A vertical wavy line of [height], rippling either side of [centerX].
 *
 * Unlike the other two, this one is a sine wave rather than pure noise:
 * [wavelength] sets the pitch of the ripple and [amplitude] its reach.
 * [noiseAmount] modulates that reach point by point, from a mechanical wave
 * at `0` to one whose crests vary widely at `1`.
 */
internal fun Density.sketchVerticalWavyLinePath(
    height: Float,
    centerX: Float,
    amplitude: Dp,
    wavelength: Dp,
    noiseAmount: Float,
    seed: Int,
): Path {
    val reach = amplitude.toPx()
    val pitch = wavelength.toPx()
    val steps = max(2, (height / pitch * POINTS_PER_WAVE).roundToInt())
    val ys = FloatArray(steps + 1) { height * it / steps }
    val xs = FloatArray(steps + 1) { index ->
        val turns = ys[index] / pitch
        val swell = 1f + coherentNoise(seed, ys[index], pitch) * noiseAmount
        centerX + sin(turns * TWO_PI) * reach * swell
    }

    return openCurveThrough(xs, ys, WAVY_TANGENT_CLAMP)
}

/**
 * The eight lengths a round rect outline is made of: four edges alternating with four arcs,
 * starting at the top edge and running clockwise.
 */
private fun Density.outlineSegments(width: Float, height: Float, cornerRadius: Dp): FloatArray {
    val radius = cornerRadius.toPx().coerceIn(0f, min(width, height) / 2f)
    val straightWidth = width - radius * 2f
    val straightHeight = height - radius * 2f
    val arcLength = QUARTER_TURN * radius
    return floatArrayOf(
        straightWidth,
        arcLength,
        straightHeight,
        arcLength,
        straightWidth,
        arcLength,
        straightHeight,
        arcLength,
    )
}

/**
 * A closed round rect of [width] by [height], wobbling around its outline.
 *
 * [latticeWidth] and [latticeHeight] give the outline that the noise lattice and the anchor
 * counts are taken from. Passing the drawn size fits both to the shape at hand; passing a
 * fixed size holds the wobble still while the shape resizes, since those counts are whole
 * numbers and stepping one swaps the noise for an unrelated field.
 *
 * Each anchor is displaced along the outward normal by the same two octaves
 * [sketchHorizontalLinePath] uses, so the outline reaches `roughness + tremor` beyond
 * the nominal rectangle at most. Pass a [roughness] already scaled by
 * [swingCapRatio] and inset by the same amount, so the swing stays inside the
 * bounds and cannot fold the outline onto itself.
 */
internal fun Density.sketchRoundRectPath(
    width: Float,
    height: Float,
    cornerRadius: Dp,
    roughness: Dp,
    tremor: Dp,
    sweepWavelength: Dp,
    tremorWavelength: Dp,
    seed: Int,
    latticeWidth: Float,
    latticeHeight: Float,
): Path {
    val sweepAmplitude = roughness.toPx()
    val tremorAmplitude = tremor.toPx()
    val radius = cornerRadius.toPx().coerceIn(0f, min(width, height) / 2f)
    val straightWidth = width - radius * 2f
    val straightHeight = height - radius * 2f
    val lengths = outlineSegments(width, height, cornerRadius)
    val perimeter = lengths.sum()

    val latticeLengths = outlineSegments(latticeWidth, latticeHeight, cornerRadius)
    val latticePerimeter = latticeLengths.sum()
    val sweepCells = cellsFor(latticePerimeter, sweepWavelength.toPx(), MIN_SWEEP_CELLS)
    val tremorCells = cellsFor(latticePerimeter, tremorWavelength.toPx(), MIN_TREMOR_CELLS)
    // Anchors have to resolve the lattice the closed path ends up with, not the one the
    // nominal wavelength describes. Wrapping rounds the lattice to a whole number of cells,
    // and on a short perimeter that lands far finer than the wavelength asked for; spacing
    // the anchors by the nominal figure would drop the tremor between them.
    val anchorSpacing = min(latticePerimeter / tremorCells / ANCHORS_PER_WAVE, MaxAnchorSpacing.toPx())
    val distances = anchorDistances(lengths, latticeLengths, anchorSpacing)
    val offsets = FloatArray(distances.size) { coherentNoiseCyclic(seed, distances[it] / perimeter, sweepCells) }
    scaleToLatticeRange(offsets, seed, sweepCells, sweepAmplitude)
    for (index in offsets.indices) {
        val t = distances[index] / perimeter
        offsets[index] += coherentNoiseCyclic(seed + TREMOR_SEED_OFFSET, t, tremorCells) * tremorAmplitude
    }

    val xs = FloatArray(distances.size)
    val ys = FloatArray(distances.size)
    for (index in distances.indices) {
        val point = outlinePoint(distances[index], width, height, radius, straightWidth, straightHeight)
        xs[index] = point.x + point.normalX * offsets[index]
        ys[index] = point.y + point.normalY * offsets[index]
    }

    val count = xs.size
    return Path().apply {
        moveTo(xs[0], ys[0])
        val controls = FloatArray(4)
        for (index in 0 until count) {
            val previous = (index - 1).mod(count)
            val current = index
            val next = (index + 1).mod(count)
            val following = (index + 2).mod(count)
            // A duplicated corner anchor would emit a zero-length curve, which a round
            // join renders as a visible dot. Skipping it keeps the corner sharp, since
            // the duplicate still steers the tangents of the segments either side.
            if (xs[next] == xs[current] && ys[next] == ys[current]) continue
            controlPointsFor(
                p0x = xs[previous], p0y = ys[previous],
                p1x = xs[current], p1y = ys[current],
                p2x = xs[next], p2y = ys[next],
                p3x = xs[following], p3y = ys[following],
                tangentClamp = OUTLINE_TANGENT_CLAMP,
                out = controls,
            )
            cubicTo(controls[0], controls[1], controls[2], controls[3], xs[next], ys[next])
        }
        close()
    }
}

/**
 * How much the requested swing has to shrink to fit a box of [width] by [height].
 *
 * Anchors move along the outward normal, so a swing wider than a quarter of the shorter
 * side of the outline carries opposing edges through each other and folds it into a ribbon.
 * Both octaves are scaled by this one ratio, which caps the swing while keeping their
 * proportion, so a shape small enough to reach the cap draws calmer rather than
 * differently.
 *
 * The outline is drawn inside a box already inset by the swing itself plus half of
 * [strokeWidth], so the bound is solved rather than read off the box: with `a` the swing,
 * the inner side measures `min(width, height) - 2a - strokeWidth`, and holding `a` within a
 * quarter of that gives `a <= (min(width, height) - strokeWidth) / 6`. Measuring against the
 * full box instead lets a large swing shrink the outline faster than the cap tightens, and
 * the shape folds anyway.
 */
internal fun Density.swingCapRatio(
    width: Float,
    height: Float,
    roughness: Dp,
    tremor: Dp,
    strokeWidth: Dp,
): Float {
    val requested = roughness.toPx() + tremor.toPx()
    if (requested <= 0f) return 1f
    val cap = (min(width, height) - strokeWidth.toPx()) / 6f
    if (cap <= 0f) return 0f
    return min(1f, cap / requested)
}

/** Anchor positions spread evenly along a line of [length], dense enough to carry the tremor. */
private fun Density.sketchLinePositions(length: Float, tremorWavelength: Dp): FloatArray {
    val segments = max(2, ceil(length / anchorSpacingFor(tremorWavelength).toPx()).toInt())
    return FloatArray(segments + 1) { length * it / segments }
}

/** How far each of [positions] departs from [center], as the two octaves displace it. */
private fun Density.sketchLineOffsets(
    positions: FloatArray,
    center: Float,
    roughness: Dp,
    tremor: Dp,
    sweepWavelength: Dp,
    tremorWavelength: Dp,
    seed: Int,
): FloatArray {
    val sweepWavelengthPx = sweepWavelength.toPx()
    val tremorWavelengthPx = tremorWavelength.toPx()
    val tremorAmplitude = tremor.toPx()

    val offsets = FloatArray(positions.size) { coherentNoise(seed, positions[it], sweepWavelengthPx) }
    normalizeToPeak(offsets, roughness.toPx())
    // The tremor octave is added raw rather than normalized: normalizing it would
    // scale every fine wave down to the single largest one, flattening the effect.
    for (index in offsets.indices) {
        val tremorNoise = coherentNoise(seed + TREMOR_SEED_OFFSET, positions[index], tremorWavelengthPx)
        offsets[index] += center + tremorNoise * tremorAmplitude
    }
    return offsets
}

/**
 * The open curve through every `(x, y)` of [xs] and [ys].
 *
 * The two anchors at either end have no neighbour to take a tangent from, so the index is
 * clamped there rather than wrapped: the curve leaves and arrives straight along itself.
 */
private fun openCurveThrough(xs: FloatArray, ys: FloatArray, tangentClamp: Float): Path {
    val segments = xs.size - 1
    return Path().apply {
        moveTo(xs[0], ys[0])
        val controls = FloatArray(4)
        for (index in 0 until segments) {
            val previous = max(index - 1, 0)
            val next = min(index + 2, segments)
            controlPointsFor(
                p0x = xs[previous], p0y = ys[previous],
                p1x = xs[index], p1y = ys[index],
                p2x = xs[index + 1], p2y = ys[index + 1],
                p3x = xs[next], p3y = ys[next],
                tangentClamp = tangentClamp,
                out = controls,
            )
            cubicTo(controls[0], controls[1], controls[2], controls[3], xs[index + 1], ys[index + 1])
        }
    }
}

/**
 * Control points of the cubic that reproduces the centripetal Catmull-Rom segment from
 * `p1` to `p2`, written into [out] as `c1x, c1y, c2x, c2y`.
 *
 * Centripetal parameterization (α = 0.5) weights each tangent by the square root of the
 * distance to its neighbour, which keeps the curve from looping back on itself where the
 * anchors are unevenly spaced — the uniform form overshoots badly around a short segment
 * next to a long one. A duplicated anchor collapses one weight to zero, so the tangent
 * there degenerates to the anchor itself, which is what leaves a corner sharp.
 */
private fun controlPointsFor(
    p0x: Float,
    p0y: Float,
    p1x: Float,
    p1y: Float,
    p2x: Float,
    p2y: Float,
    p3x: Float,
    p3y: Float,
    tangentClamp: Float,
    out: FloatArray,
) {
    val d1 = knotSpacing(p0x, p0y, p1x, p1y)
    val d2 = knotSpacing(p1x, p1y, p2x, p2y)
    val d3 = knotSpacing(p2x, p2y, p3x, p3y)

    var c1x = p1x
    var c1y = p1y
    if (d1 > 0f && d2 > 0f) {
        val scale = 3f * d1 * (d1 + d2)
        val weight = 2f * d1 * d1 + 3f * d1 * d2 + d2 * d2
        c1x = (d1 * d1 * p2x - d2 * d2 * p0x + weight * p1x) / scale
        c1y = (d1 * d1 * p2y - d2 * d2 * p0y + weight * p1y) / scale
    }

    var c2x = p2x
    var c2y = p2y
    if (d3 > 0f && d2 > 0f) {
        val scale = 3f * d3 * (d3 + d2)
        val weight = 2f * d3 * d3 + 3f * d3 * d2 + d2 * d2
        c2x = (d3 * d3 * p1x - d2 * d2 * p3x + weight * p2x) / scale
        c2y = (d3 * d3 * p1y - d2 * d2 * p3y + weight * p2y) / scale
    }

    val reach = hypot(p2x - p1x, p2y - p1y) * tangentClamp
    clampToReach(p1x, p1y, c1x, c1y, reach, out, 0)
    clampToReach(p2x, p2y, c2x, c2y, reach, out, 2)
}

/** Knot spacing under centripetal parameterization: the distance raised to α = 0.5. */
private fun knotSpacing(ax: Float, ay: Float, bx: Float, by: Float): Float =
    sqrt(hypot(bx - ax, by - ay))

/**
 * Pulls a control point back towards its anchor until it is at most [reach] away.
 *
 * Even under centripetal weighting a tangent can run far past the segment it belongs to
 * when the noise turns sharply; capping it in proportion to the segment length keeps the
 * curve inside the band the anchors describe.
 */
private fun clampToReach(
    anchorX: Float,
    anchorY: Float,
    controlX: Float,
    controlY: Float,
    reach: Float,
    out: FloatArray,
    offset: Int,
) {
    val dx = controlX - anchorX
    val dy = controlY - anchorY
    val distance = hypot(dx, dy)
    if (distance <= reach || distance == 0f) {
        out[offset] = controlX
        out[offset + 1] = controlY
        return
    }
    val scale = reach / distance
    out[offset] = anchorX + dx * scale
    out[offset + 1] = anchorY + dy * scale
}

/**
 * Anchor positions along the perimeter, pinned to every segment boundary.
 *
 * A zero-length arc contributes a duplicated anchor, and that duplicate is what
 * lets a sharp corner survive the spline that is otherwise smooth everywhere.
 *
 * The result is rotated so the path starts, and therefore closes, away from any corner.
 */
private fun anchorDistances(
    lengths: FloatArray,
    countLengths: FloatArray,
    anchorSpacing: Float,
): FloatArray {
    val distances = ArrayList<Float>(lengths.size * 4)
    var start = 0f
    lengths.forEachIndexed { index, length ->
        val isArc = index % 2 == 1
        if (length <= 0f) {
            // A zero-length arc is a square corner, and the duplicated anchor it leaves
            // behind is what breaks the tangent there. A zero-length straight edge means
            // something else: the radius has grown to meet the side, and the outline runs
            // smoothly across that point. A cusp there facets a pill or a circle.
            if (isArc) distances += start
        } else {
            // How many anchors a segment gets comes from [countLengths] while where they sit
            // comes from [lengths]. Given the lattice geometry for the former, a resizing box
            // keeps the same anchors and simply stretches them, instead of gaining one and
            // redistributing the lot.
            val steps = max(if (isArc) 2 else 1, (countLengths[index] / anchorSpacing).roundToInt())
            for (step in 0 until steps) {
                distances += start + length * step / steps
            }
        }
        start += length
    }

    // Start midway along the top edge. The closing seam of the path joins its two ends
    // with a smooth tangent, which is wrong at a corner: a corner is exactly where the
    // tangent has to break, and a seam placed there rounds it off.
    val seam = lengths[0] / 2f
    val rotated = ArrayList<Float>(distances.size)
    distances.filterTo(rotated) { it >= seam }
    distances.filterTo(rotated) { it < seam }
    return rotated.toFloatArray()
}

private fun outlinePoint(
    distance: Float,
    width: Float,
    height: Float,
    radius: Float,
    straightWidth: Float,
    straightHeight: Float,
): OutlinePoint {
    val arcLength = QUARTER_TURN * radius
    // A distance of exactly the perimeter is the start point. Without this it would fall
    // through to the last arc and come back with that arc's normal, so the anchor would
    // be pushed along a different direction than the start point it coincides with.
    val perimeter = (straightWidth + straightHeight) * 2f + arcLength * 4f
    var remaining = if (distance >= perimeter) distance - perimeter else distance

    if (remaining < straightWidth) return OutlinePoint(radius + remaining, 0f, 0f, -1f)
    remaining -= straightWidth
    if (remaining < arcLength) {
        return arcPoint(remaining / arcLength, -QUARTER_TURN, width - radius, radius, radius)
    }
    remaining -= arcLength
    if (remaining < straightHeight) return OutlinePoint(width, radius + remaining, 1f, 0f)
    remaining -= straightHeight
    if (remaining < arcLength) {
        return arcPoint(remaining / arcLength, 0f, width - radius, height - radius, radius)
    }
    remaining -= arcLength
    if (remaining < straightWidth) return OutlinePoint(width - radius - remaining, height, 0f, 1f)
    remaining -= straightWidth
    if (remaining < arcLength) {
        return arcPoint(remaining / arcLength, QUARTER_TURN, radius, height - radius, radius)
    }
    remaining -= arcLength
    if (remaining < straightHeight) return OutlinePoint(0f, height - radius - remaining, -1f, 0f)
    remaining -= straightHeight

    val progress = if (arcLength > 0f) remaining / arcLength else 0f
    return arcPoint(progress, PI.toFloat(), radius, radius, radius)
}

private fun arcPoint(
    progress: Float,
    startAngle: Float,
    centerX: Float,
    centerY: Float,
    radius: Float,
): OutlinePoint {
    val angle = startAngle + progress * QUARTER_TURN
    val normalX = cos(angle)
    val normalY = sin(angle)
    return OutlinePoint(
        x = centerX + normalX * radius,
        y = centerY + normalY * radius,
        normalX = normalX,
        normalY = normalY,
    )
}

private class OutlinePoint(
    val x: Float,
    val y: Float,
    val normalX: Float,
    val normalY: Float,
)

/**
 * Rescales [values] so their peak-to-peak span is exactly `2 * amplitude`.
 *
 * Without this the visible waviness would depend on how many noise lattice
 * cells happen to fit in the drawn length.
 */
private fun normalizeToPeak(values: FloatArray, amplitude: Float) {
    val lowest = values.min()
    val highest = values.max()
    val halfRange = (highest - lowest) / 2f
    if (halfRange == 0f) {
        values.fill(0f)
        return
    }
    val midpoint = (highest + lowest) / 2f
    for (index in values.indices) {
        values[index] = (values[index] - midpoint) / halfRange * amplitude
    }
}

/**
 * Rescales [values] so the wrapped lattice they came from spans exactly `2 * amplitude`.
 *
 * The span is read off the lattice rather than off [values]. Smoothstep interpolates
 * monotonically between neighbouring lattice values, so the extremes sit exactly on the
 * lattice points. Taking them from the anchors instead misses them wherever anchors are
 * sparse enough to fall either side of a peak, and lets the scale breathe every time their
 * count steps, which shows up as the whole outline pulsing while a box resizes.
 */
private fun scaleToLatticeRange(values: FloatArray, seed: Int, cells: Int, amplitude: Float) {
    var lowest = Float.MAX_VALUE
    var highest = -Float.MAX_VALUE
    for (cell in 0 until cells) {
        val value = hashNoise(seed, cell)
        if (value < lowest) lowest = value
        if (value > highest) highest = value
    }
    val halfRange = (highest - lowest) / 2f
    if (halfRange == 0f) {
        values.fill(0f)
        return
    }
    val midpoint = (highest + lowest) / 2f
    for (index in values.indices) {
        values[index] = (values[index] - midpoint) / halfRange * amplitude
    }
}

private fun cellsFor(perimeter: Float, wavelength: Float, minimumCells: Int): Int =
    max(minimumCells, (perimeter / wavelength).roundToInt())

/**
 * A deterministic value in `[-1, 1)` for a lattice point, specified so that a second
 * implementation reproduces it exactly.
 *
 * Every step is 32-bit integer arithmetic that wraps on overflow, which JavaScript
 * reproduces only through `Math.imul`; plain `*` there computes in double and loses the
 * low bits. Shifts are unsigned, and the result is taken from the top 24 bits, a width
 * both `Float` and `double` hold exactly, divided by a power of two so no rounding
 * enters either.
 */
private fun hashNoise(seed: Int, index: Int): Float {
    var h = seed * SEED_MULTIPLIER xor index * INDEX_MULTIPLIER
    h = (h xor (h ushr 13)) * MIX_MULTIPLIER
    h = h xor (h ushr 16)
    return (h ushr 8) / HASH_SCALE * 2f - 1f
}

private fun smoothstep(t: Float): Float = t * t * (3f - 2f * t)

/** Interpolates [hashNoise] over a lattice of [wavelength], for an open curve. */
private fun coherentNoise(seed: Int, position: Float, wavelength: Float): Float {
    val turns = position / wavelength
    val cell = floor(turns).toInt()
    val blend = smoothstep(turns - cell)
    return hashNoise(seed, cell) * (1f - blend) + hashNoise(seed, cell + 1) * blend
}

/**
 * The same over a lattice of [cells] wrapped onto a closed path, where [t] runs `0..1`
 * around it. Wrapping is what makes the value at the seam agree from both sides.
 */
private fun coherentNoiseCyclic(seed: Int, t: Float, cells: Int): Float {
    val turns = t * cells
    val cell = floor(turns).toInt()
    val blend = smoothstep(turns - cell)
    return hashNoise(seed, cell.mod(cells)) * (1f - blend) +
        hashNoise(seed, (cell + 1).mod(cells)) * blend
}

// How finely the ellipse is sampled when its arc length is tabulated. The table only has to
// resolve anchor spacing, and an ellipse is smooth, so a few hundred steps put the error far
// below the tremor it carries.
private const val ARC_LENGTH_SAMPLES = 1024

/**
 * A closed ellipse of [width] by [height], wobbling around its outline.
 *
 * The rounded rect this sits beside reaches a pill when its radius meets the shorter side,
 * which is a different figure: a pill keeps the straight run between its two arcs, while an
 * ellipse curves through the whole turn. [latticeWidth] and [latticeHeight] serve the same
 * purpose as they do for the rect — see [sketchRoundRectPath].
 *
 * Anchors are spaced by arc length rather than by angle. Spacing by angle crowds them at the
 * ends of the long axis and thins them along the flanks, so the tremor would come out uneven
 * around one outline.
 */
internal fun Density.sketchEllipsePath(
    width: Float,
    height: Float,
    roughness: Dp,
    tremor: Dp,
    sweepWavelength: Dp,
    tremorWavelength: Dp,
    seed: Int,
    latticeWidth: Float,
    latticeHeight: Float,
): Path {
    val sweepAmplitude = roughness.toPx()
    val tremorAmplitude = tremor.toPx()

    val table = ellipseArcLengths(width / 2f, height / 2f)
    val perimeter = table.last()
    // Without a reference size the lattice is the drawn size, which is the common case; tabulating
    // it a second time would repeat the whole sweep for nothing.
    val latticePerimeter = if (latticeWidth == width && latticeHeight == height) {
        perimeter
    } else {
        ellipseArcLengths(latticeWidth / 2f, latticeHeight / 2f).last()
    }

    val sweepCells = cellsFor(latticePerimeter, sweepWavelength.toPx(), MIN_SWEEP_CELLS)
    val tremorCells = cellsFor(latticePerimeter, tremorWavelength.toPx(), MIN_TREMOR_CELLS)
    val anchorSpacing = min(latticePerimeter / tremorCells / ANCHORS_PER_WAVE, MaxAnchorSpacing.toPx())
    // Taking the count from the lattice keeps a resizing ellipse stretching one outline rather
    // than gaining an anchor and redrawing itself, the same rule the rect follows.
    val count = max(MIN_ELLIPSE_ANCHORS, (latticePerimeter / anchorSpacing).roundToInt())

    val offsets = FloatArray(count) { coherentNoiseCyclic(seed, it.toFloat() / count, sweepCells) }
    scaleToLatticeRange(offsets, seed, sweepCells, sweepAmplitude)
    for (index in offsets.indices) {
        val t = index.toFloat() / count
        offsets[index] += coherentNoiseCyclic(seed + TREMOR_SEED_OFFSET, t, tremorCells) * tremorAmplitude
    }

    val semiWidth = width / 2f
    val semiHeight = height / 2f
    val xs = FloatArray(count)
    val ys = FloatArray(count)
    for (index in 0 until count) {
        // Start at the top so the closing seam falls where the outline is smoothest, matching
        // where the rect puts it.
        val angle = angleAtArcLength(table, perimeter * index / count) - QUARTER_TURN
        val pointX = semiWidth + semiWidth * cos(angle)
        val pointY = semiHeight + semiHeight * sin(angle)
        // The outward normal of an ellipse is not the radius: it comes from the gradient of
        // (x/a)^2 + (y/b)^2, which swaps the semi-axes.
        val normalX = semiHeight * cos(angle)
        val normalY = semiWidth * sin(angle)
        val length = hypot(normalX, normalY)
        val scale = if (length > 0f) offsets[index] / length else 0f
        xs[index] = pointX + normalX * scale
        ys[index] = pointY + normalY * scale
    }

    return closedCurveThrough(xs, ys)
}

/** The fewest anchors an ellipse is drawn from, so a small one keeps its curvature. */
private const val MIN_ELLIPSE_ANCHORS = 12

/**
 * Cumulative arc length of a quarter-turn-aligned ellipse with semi-axes [semiWidth] and
 * [semiHeight], sampled at [ARC_LENGTH_SAMPLES] equal steps of angle and closing on the
 * perimeter. There is no closed form for it, so it is tabulated and read back by search.
 */
private fun ellipseArcLengths(semiWidth: Float, semiHeight: Float): FloatArray {
    val lengths = FloatArray(ARC_LENGTH_SAMPLES + 1)
    val step = (2f * PI.toFloat()) / ARC_LENGTH_SAMPLES
    var total = 0f
    var previousX = semiWidth
    var previousY = 0f
    for (index in 1..ARC_LENGTH_SAMPLES) {
        val angle = step * index
        val x = semiWidth * cos(angle)
        val y = semiHeight * sin(angle)
        total += hypot(x - previousX, y - previousY)
        lengths[index] = total
        previousX = x
        previousY = y
    }
    return lengths
}

/** The angle at [distance] along the outline [table] describes, interpolated between samples. */
private fun angleAtArcLength(table: FloatArray, distance: Float): Float {
    val step = (2f * PI.toFloat()) / ARC_LENGTH_SAMPLES
    val target = distance.coerceIn(0f, table.last())
    var low = 0
    var high = table.lastIndex
    while (low < high) {
        val middle = (low + high) / 2
        if (table[middle] < target) low = middle + 1 else high = middle
    }
    if (low == 0) return 0f
    val before = table[low - 1]
    val span = table[low] - before
    val fraction = if (span > 0f) (target - before) / span else 0f
    return step * (low - 1 + fraction)
}

/**
 * A closed path enclosing the region to one side of a hand-drawn vertical line.
 *
 * Produces the same curve [sketchVerticalLinePath] strokes at [centerX], then closes it
 * against the left (x = 0) or right (x = [width]) boundary. Pass to a canvas clip so the
 * fill of a selected segment meets the divider down its centre instead of at a straight
 * layout edge.
 *
 * @param fillLeft when true the region extends from the divider to x = 0;
 *   when false it extends from the divider to x = [width].
 */
internal fun Density.sketchVerticalFillPath(
    width: Float,
    height: Float,
    centerX: Float,
    roughness: Dp,
    tremor: Dp,
    sweepWavelength: Dp,
    tremorWavelength: Dp,
    seed: Int,
    fillLeft: Boolean,
): Path {
    val positions = sketchLinePositions(height, tremorWavelength)
    val offsets = sketchLineOffsets(
        positions = positions,
        center = centerX,
        roughness = roughness,
        tremor = tremor,
        sweepWavelength = sweepWavelength,
        tremorWavelength = tremorWavelength,
        seed = seed,
    )
    val boundaryX = if (fillLeft) 0f else width
    return openCurveThrough(offsets, positions, OUTLINE_TANGENT_CLAMP).apply {
        lineTo(boundaryX, height)
        lineTo(boundaryX, 0f)
        close()
    }
}

/** Emits [xs] and [ys] as one closed curve, the same conversion the rect outline uses. */
private fun closedCurveThrough(xs: FloatArray, ys: FloatArray): Path {
    val count = xs.size
    return Path().apply {
        moveTo(xs[0], ys[0])
        val controls = FloatArray(4)
        for (index in 0 until count) {
            val previous = (index - 1).mod(count)
            val next = (index + 1).mod(count)
            val following = (index + 2).mod(count)
            controlPointsFor(
                p0x = xs[previous], p0y = ys[previous],
                p1x = xs[index], p1y = ys[index],
                p2x = xs[next], p2y = ys[next],
                p3x = xs[following], p3y = ys[following],
                tangentClamp = OUTLINE_TANGENT_CLAMP,
                out = controls,
            )
            cubicTo(controls[0], controls[1], controls[2], controls[3], xs[next], ys[next])
        }
        close()
    }
}

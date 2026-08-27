import SwiftUI

/// The hand-drawn widget frame, a port of `SketchRoundRectShape` and `sketchRoundRectPath`
/// (`core/ui/.../Sketch.kt`, `SketchPath.kt`) with the values `SketchBorderBitmap.kt` pins.
/// The two must stay in step; the noise below is specified so both reproduce it exactly.
struct SketchFrame: Shape {
    let medium: Bool

    static let borderThickness: CGFloat = 1.5

    private static let cornerRadius: CGFloat = 16
    private static let roughness: CGFloat = 1
    private static let tremor: CGFloat = 0.3
    private static let sweepWavelength: CGFloat = 140
    private static let tremorWavelength: CGFloat = 42

    private static let smallSeed: Int32 = 2601
    private static let mediumSeed: Int32 = 2602

    // The lattice is pinned to the design's base frame sizes so the wobble does not reshuffle
    // when the widget family makes the actual frame a few points larger or smaller.
    private static let smallReference = CGSize(width: 142, height: 142)
    private static let mediumReference = CGSize(width: 322, height: 142)

    func path(in rect: CGRect) -> Path {
        let seed = medium ? Self.mediumSeed : Self.smallSeed
        let reference = medium ? Self.mediumReference : Self.smallReference
        let ratio = swingCapRatio(width: rect.width, height: rect.height)
        let roughness = Self.roughness * ratio
        let tremor = Self.tremor * ratio
        let inset = roughness + tremor + Self.borderThickness / 2
        let width = rect.width - inset * 2
        let height = rect.height - inset * 2
        guard width > 0, height > 0 else { return Path(rect) }
        let lattice = latticeOutline(reference: reference) ?? CGSize(width: width, height: height)

        var path = sketchRoundRectPath(
            width: width,
            height: height,
            roughness: roughness,
            tremor: tremor,
            seed: seed,
            latticeWidth: lattice.width,
            latticeHeight: lattice.height
        )
        path = path.applying(CGAffineTransform(translationX: rect.minX + inset, y: rect.minY + inset))
        return path
    }

    private func latticeOutline(reference: CGSize) -> CGSize? {
        let ratio = swingCapRatio(width: reference.width, height: reference.height)
        let inset = (Self.roughness + Self.tremor) * ratio + Self.borderThickness / 2
        let width = reference.width - inset * 2
        let height = reference.height - inset * 2
        return width > 0 && height > 0 ? CGSize(width: width, height: height) : nil
    }

    /// How much the requested swing has to shrink to fit a box of [width] by [height].
    private func swingCapRatio(width: CGFloat, height: CGFloat) -> CGFloat {
        let requested = Self.roughness + Self.tremor
        if requested <= 0 { return 1 }
        let cap = (min(width, height) - Self.borderThickness) / 6
        if cap <= 0 { return 0 }
        return min(1, cap / requested)
    }

    private func sketchRoundRectPath(
        width: CGFloat,
        height: CGFloat,
        roughness: CGFloat,
        tremor: CGFloat,
        seed: Int32,
        latticeWidth: CGFloat,
        latticeHeight: CGFloat
    ) -> Path {
        let radius = min(max(Self.cornerRadius, 0), min(width, height) / 2)
        let straightWidth = width - radius * 2
        let straightHeight = height - radius * 2
        let lengths = outlineSegments(width: width, height: height, radius: radius)
        let perimeter = lengths.reduce(0, +)

        let latticeRadius = min(max(Self.cornerRadius, 0), min(latticeWidth, latticeHeight) / 2)
        let latticeLengths = outlineSegments(
            width: latticeWidth,
            height: latticeHeight,
            radius: latticeRadius
        )
        let latticePerimeter = latticeLengths.reduce(0, +)
        let sweepCells = cellsFor(latticePerimeter, Self.sweepWavelength, minimumCells: minSweepCells)
        let tremorCells = cellsFor(latticePerimeter, Self.tremorWavelength, minimumCells: minTremorCells)
        // Anchors have to resolve the lattice the closed path ends up with, not the one the
        // nominal wavelength describes.
        let anchorSpacing = min(
            latticePerimeter / CGFloat(tremorCells) / anchorsPerWave,
            maxAnchorSpacing
        )
        let distances = anchorDistances(
            lengths: lengths,
            countLengths: latticeLengths,
            anchorSpacing: anchorSpacing
        )
        var offsets = distances.map {
            coherentNoiseCyclic(seed: seed, t: Float($0 / perimeter), cells: sweepCells)
        }
        scaleToLatticeRange(&offsets, seed: seed, cells: sweepCells, amplitude: Float(roughness))
        for index in offsets.indices {
            let t = Float(distances[index] / perimeter)
            offsets[index] += coherentNoiseCyclic(
                seed: seed &+ tremorSeedOffset,
                t: t,
                cells: tremorCells
            ) * Float(tremor)
        }

        var xs = [CGFloat](repeating: 0, count: distances.count)
        var ys = [CGFloat](repeating: 0, count: distances.count)
        for index in distances.indices {
            let point = outlinePoint(
                distance: distances[index],
                width: width,
                height: height,
                radius: radius,
                straightWidth: straightWidth,
                straightHeight: straightHeight
            )
            xs[index] = point.x + point.normalX * CGFloat(offsets[index])
            ys[index] = point.y + point.normalY * CGFloat(offsets[index])
        }

        let count = xs.count
        var path = Path()
        path.move(to: CGPoint(x: xs[0], y: ys[0]))
        for index in 0 ..< count {
            let previous = (index - 1).wrapped(count)
            let next = (index + 1).wrapped(count)
            let following = (index + 2).wrapped(count)
            // A duplicated corner anchor would emit a zero-length curve, which a round join
            // renders as a visible dot; the duplicate still steers the tangents either side.
            if xs[next] == xs[index], ys[next] == ys[index] { continue }
            let controls = controlPointsFor(
                p0: CGPoint(x: xs[previous], y: ys[previous]),
                p1: CGPoint(x: xs[index], y: ys[index]),
                p2: CGPoint(x: xs[next], y: ys[next]),
                p3: CGPoint(x: xs[following], y: ys[following]),
                tangentClamp: outlineTangentClamp
            )
            path.addCurve(
                to: CGPoint(x: xs[next], y: ys[next]),
                control1: controls.0,
                control2: controls.1
            )
        }
        path.closeSubpath()
        return path
    }
}

// MARK: - Outline geometry

// Anchors carry the tremor, so they have to be dense enough to resolve it: roughly four
// to a wavelength, with a ceiling so a long wavelength cannot thin them out.
private let anchorsPerWave: CGFloat = 4
private let maxAnchorSpacing: CGFloat = 12

// Offsets the tremor octave onto its own sequence, so the two bands stay uncorrelated.
private let tremorSeedOffset: Int32 = 100

private let minSweepCells = 4
private let minTremorCells = 2

// How far a Catmull-Rom tangent may reach, as a fraction of the segment it belongs to.
private let outlineTangentClamp: CGFloat = 0.36

private let quarterTurn = CGFloat.pi / 2

/// The eight lengths a round rect outline is made of: four edges alternating with four arcs,
/// starting at the top edge and running clockwise.
private func outlineSegments(width: CGFloat, height: CGFloat, radius: CGFloat) -> [CGFloat] {
    let straightWidth = width - radius * 2
    let straightHeight = height - radius * 2
    let arcLength = quarterTurn * radius
    return [
        straightWidth, arcLength,
        straightHeight, arcLength,
        straightWidth, arcLength,
        straightHeight, arcLength,
    ]
}

/// Anchor positions along the perimeter, pinned to every segment boundary, rotated so the path
/// starts — and therefore closes — away from any corner.
private func anchorDistances(
    lengths: [CGFloat],
    countLengths: [CGFloat],
    anchorSpacing: CGFloat
) -> [CGFloat] {
    var distances: [CGFloat] = []
    var start: CGFloat = 0
    for (index, length) in lengths.enumerated() {
        let isArc = index % 2 == 1
        if length <= 0 {
            // A zero-length arc is a square corner, and the duplicated anchor it leaves behind
            // is what breaks the tangent there. A zero-length straight edge means the radius has
            // grown to meet the side, where the outline runs smoothly across.
            if isArc { distances.append(start) }
        } else {
            let steps = max(
                isArc ? 2 : 1,
                Int((countLengths[index] / anchorSpacing).rounded(.toNearestOrAwayFromZero))
            )
            for step in 0 ..< steps {
                distances.append(start + length * CGFloat(step) / CGFloat(steps))
            }
        }
        start += length
    }
    let seam = lengths[0] / 2
    return distances.filter { $0 >= seam } + distances.filter { $0 < seam }
}

private struct OutlinePoint {
    let x: CGFloat
    let y: CGFloat
    let normalX: CGFloat
    let normalY: CGFloat
}

private func outlinePoint(
    distance: CGFloat,
    width: CGFloat,
    height: CGFloat,
    radius: CGFloat,
    straightWidth: CGFloat,
    straightHeight: CGFloat
) -> OutlinePoint {
    let arcLength = quarterTurn * radius
    // A distance of exactly the perimeter is the start point; without this it would fall through
    // to the last arc and come back with that arc's normal.
    let perimeter = (straightWidth + straightHeight) * 2 + arcLength * 4
    var remaining = distance >= perimeter ? distance - perimeter : distance

    if remaining < straightWidth {
        return OutlinePoint(x: radius + remaining, y: 0, normalX: 0, normalY: -1)
    }
    remaining -= straightWidth
    if remaining < arcLength {
        return arcPoint(remaining / arcLength, -quarterTurn, width - radius, radius, radius)
    }
    remaining -= arcLength
    if remaining < straightHeight {
        return OutlinePoint(x: width, y: radius + remaining, normalX: 1, normalY: 0)
    }
    remaining -= straightHeight
    if remaining < arcLength {
        return arcPoint(remaining / arcLength, 0, width - radius, height - radius, radius)
    }
    remaining -= arcLength
    if remaining < straightWidth {
        return OutlinePoint(x: width - radius - remaining, y: height, normalX: 0, normalY: 1)
    }
    remaining -= straightWidth
    if remaining < arcLength {
        return arcPoint(remaining / arcLength, quarterTurn, radius, height - radius, radius)
    }
    remaining -= arcLength
    if remaining < straightHeight {
        return OutlinePoint(x: 0, y: height - radius - remaining, normalX: -1, normalY: 0)
    }
    remaining -= straightHeight

    let progress = arcLength > 0 ? remaining / arcLength : 0
    return arcPoint(progress, .pi, radius, radius, radius)
}

private func arcPoint(
    _ progress: CGFloat,
    _ startAngle: CGFloat,
    _ centerX: CGFloat,
    _ centerY: CGFloat,
    _ radius: CGFloat
) -> OutlinePoint {
    let angle = startAngle + progress * quarterTurn
    let normalX = cos(angle)
    let normalY = sin(angle)
    return OutlinePoint(
        x: centerX + normalX * radius,
        y: centerY + normalY * radius,
        normalX: normalX,
        normalY: normalY
    )
}

// MARK: - Spline

/// Control points of the cubic that reproduces the centripetal Catmull-Rom segment from `p1`
/// to `p2`. Centripetal parameterization weights each tangent by the square root of the distance
/// to its neighbour, which keeps unevenly spaced anchors from looping the curve back on itself.
private func controlPointsFor(
    p0: CGPoint,
    p1: CGPoint,
    p2: CGPoint,
    p3: CGPoint,
    tangentClamp: CGFloat
) -> (CGPoint, CGPoint) {
    let d1 = knotSpacing(p0, p1)
    let d2 = knotSpacing(p1, p2)
    let d3 = knotSpacing(p2, p3)

    var c1 = p1
    if d1 > 0, d2 > 0 {
        let scale = 3 * d1 * (d1 + d2)
        let weight = 2 * d1 * d1 + 3 * d1 * d2 + d2 * d2
        c1 = CGPoint(
            x: (d1 * d1 * p2.x - d2 * d2 * p0.x + weight * p1.x) / scale,
            y: (d1 * d1 * p2.y - d2 * d2 * p0.y + weight * p1.y) / scale
        )
    }

    var c2 = p2
    if d3 > 0, d2 > 0 {
        let scale = 3 * d3 * (d3 + d2)
        let weight = 2 * d3 * d3 + 3 * d3 * d2 + d2 * d2
        c2 = CGPoint(
            x: (d3 * d3 * p1.x - d2 * d2 * p3.x + weight * p2.x) / scale,
            y: (d3 * d3 * p1.y - d2 * d2 * p3.y + weight * p2.y) / scale
        )
    }

    let reach = hypot(p2.x - p1.x, p2.y - p1.y) * tangentClamp
    return (clampToReach(anchor: p1, control: c1, reach: reach),
            clampToReach(anchor: p2, control: c2, reach: reach))
}

/// Knot spacing under centripetal parameterization: the distance raised to α = 0.5.
private func knotSpacing(_ a: CGPoint, _ b: CGPoint) -> CGFloat {
    sqrt(hypot(b.x - a.x, b.y - a.y))
}

/// Pulls a control point back towards its anchor until it is at most [reach] away.
private func clampToReach(anchor: CGPoint, control: CGPoint, reach: CGFloat) -> CGPoint {
    let dx = control.x - anchor.x
    let dy = control.y - anchor.y
    let distance = hypot(dx, dy)
    if distance <= reach || distance == 0 { return control }
    let scale = reach / distance
    return CGPoint(x: anchor.x + dx * scale, y: anchor.y + dy * scale)
}

// MARK: - Noise

// Constants of the specified hash: odd multipliers with well-spread bits, and the scale that
// maps its top 24 bits onto [0, 1).
private let seedMultiplier: Int32 = 374_761_393
private let indexMultiplier: Int32 = 668_265_263
private let mixMultiplier: Int32 = 1_274_126_177
private let hashScale: Float = 16_777_216

/// A deterministic value in `[-1, 1)` for a lattice point. Every step is 32-bit integer
/// arithmetic that wraps on overflow, and the result is taken from the top 24 bits, so a second
/// implementation reproduces it exactly.
private func hashNoise(seed: Int32, index: Int32) -> Float {
    var h = (seed &* seedMultiplier) ^ (index &* indexMultiplier)
    h = Int32(bitPattern: UInt32(bitPattern: h) >> 13) ^ h
    h = h &* mixMultiplier
    h = h ^ Int32(bitPattern: UInt32(bitPattern: h) >> 16)
    return Float(UInt32(bitPattern: h) >> 8) / hashScale * 2 - 1
}

private func smoothstep(_ t: Float) -> Float { t * t * (3 - 2 * t) }

/// [hashNoise] interpolated over a lattice of [cells] wrapped onto a closed path, where [t] runs
/// `0..1` around it. Wrapping is what makes the value at the seam agree from both sides.
private func coherentNoiseCyclic(seed: Int32, t: Float, cells: Int) -> Float {
    let turns = t * Float(cells)
    let cell = Int(turns.rounded(.down))
    let blend = smoothstep(turns - Float(cell))
    return hashNoise(seed: seed, index: Int32(cell.wrapped(cells))) * (1 - blend)
        + hashNoise(seed: seed, index: Int32((cell + 1).wrapped(cells))) * blend
}

/// Rescales [values] so the wrapped lattice they came from spans exactly `2 * amplitude`. The
/// span is read off the lattice rather than off [values]: smoothstep interpolates monotonically,
/// so the extremes sit exactly on the lattice points, and reading them from sparse anchors
/// instead lets the scale breathe every time their count steps.
private func scaleToLatticeRange(_ values: inout [Float], seed: Int32, cells: Int, amplitude: Float) {
    var lowest = Float.greatestFiniteMagnitude
    var highest = -Float.greatestFiniteMagnitude
    for cell in 0 ..< cells {
        let value = hashNoise(seed: seed, index: Int32(cell))
        lowest = min(lowest, value)
        highest = max(highest, value)
    }
    let halfRange = (highest - lowest) / 2
    if halfRange == 0 {
        values = [Float](repeating: 0, count: values.count)
        return
    }
    let midpoint = (highest + lowest) / 2
    for index in values.indices {
        values[index] = (values[index] - midpoint) / halfRange * amplitude
    }
}

private func cellsFor(_ perimeter: CGFloat, _ wavelength: CGFloat, minimumCells: Int) -> Int {
    max(minimumCells, Int((perimeter / wavelength).rounded(.toNearestOrAwayFromZero)))
}

private extension Int {
    /// The Kotlin `mod` this port mirrors, whose result carries the divisor's sign.
    func wrapped(_ modulus: Int) -> Int {
        let remainder = self % modulus
        return remainder < 0 ? remainder + modulus : remainder
    }
}

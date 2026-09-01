package io.github.droidkaigi.confsched.core.ui.profilecard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.DoodleLayerView
import io.github.droidkaigi.confsched.core.ui.DoodleOrigin
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_event_label
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_scan_me
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import org.jetbrains.compose.resources.stringResource
import qrcode.internals.QRCodeSquare
import qrcode.raw.ErrorCorrectionLevel
import qrcode.raw.QRCodeProcessor

/**
 * The card's back face, a "scene": a QR plate encoding the card's link under a banner, and the
 * chosen mascot standing on a gently rolling ground line beside a small flag. The QR plate is
 * drawn last so no stroke of [doodle] can cover the code.
 */
@Composable
fun ProfileCardBack(
    nickName: String,
    link: String,
    mascot: Mascot,
    sketchiness: Sketchiness,
    doodle: Doodle,
    taped: Boolean,
    modifier: Modifier = Modifier,
) {
    // The face seed is the front's, since the back is that same card turned over.
    val faceSeed = nickName.hashCode()
    ProfileCardFace(sketchiness = sketchiness, outlineSeed = faceSeed, topStartTape = false, bottomEndTape = taped, mirrored = true, modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ProfileCardBackDefaults.bannerHeight)
                .clip(BackBannerShape)
                .background(ProfileCardColors.banner),
        )
        EventLabelHeader(text = stringResource(Res.string.card_event_label), color = ProfileCardColors.onBanner, centeredLabel = true)
        Text(
            text = stringResource(Res.string.card_scan_me),
            color = ProfileCardColors.onBanner,
            style = ProfileCardTextStyles.display,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().cardOffset(ProfileCardBackDefaults.scanMeOffset),
        )
        Sparkles(ProfileCardBackDefaults.bannerSparkles, ProfileCardColors.onBanner)
        Sparkles(ProfileCardBackDefaults.groundSparkles, ProfileCardColors.duskBand)
        MascotIcon(
            mascot = mascot,
            modifier = Modifier
                .cardOffset(ProfileCardBackDefaults.mascotOffset)
                .size(ProfileCardBackDefaults.mascotBox)
                .rotate(ProfileCardBackDefaults.mascotRotationDegrees),
        )
        FlagMark(modifier = Modifier.cardOffset(ProfileCardBackDefaults.flagOffset))
        GroundLine(
            modifier = Modifier
                .cardOffset(ProfileCardBackDefaults.groundLineOffset)
                .width(ProfileCardBackDefaults.groundLineWidth),
        )
        GrassTicks(
            modifier = Modifier
                .cardOffset(ProfileCardBackDefaults.groundLineOffset)
                .width(ProfileCardBackDefaults.groundLineWidth),
        )
        DoodleLayerView(
            doodle = doodle,
            palette = profileCardDoodleInkPalette(),
            origin = DoodleOrigin.TopStart,
            scale = 1f,
            modifier = Modifier.matchParentSize(),
        )
        QrPlate(
            link = link,
            seed = qrPlateSeed(nickName),
            sketchiness = sketchiness,
            modifier = Modifier.cardOffset(ProfileCardBackDefaults.qrFrameOffset),
        )
    }
}

/**
 * The back face's QR plate on its own, laid out in the card's dp space scaled by [scale] — a
 * second, opaque copy for drawing over a doodle canvas so a stroke reads as passing under it.
 */
@Composable
fun ProfileCardBackQrPlateView(
    nickName: String,
    link: String,
    sketchiness: Sketchiness,
    scale: Float,
    modifier: Modifier = Modifier,
) {
    ProfileCardFaceSpace(scale = scale, modifier = modifier) {
        QrPlate(
            link = link,
            seed = qrPlateSeed(nickName),
            sketchiness = sketchiness,
            modifier = Modifier.cardOffset(ProfileCardBackDefaults.qrFrameOffset),
        )
    }
}

// Offset from the face seed so the plate's own wobble does not repeat the outline's.
private fun qrPlateSeed(nickName: String): Int = nickName.hashCode() + 101

/**
 * A thin wobbly frame around a smaller, plate-filled square holding the QR pattern — traced from
 * the Figma "QR Frame"/"QR Plate" pair, which are two nested shapes, not one bordered square.
 * Only the frame carries an outline; the plate is a flat fill in the design's brighter surface
 * role so the modules read as dark-on-light whatever the theme.
 */
@Composable
private fun QrPlate(link: String, seed: Int, sketchiness: Sketchiness, modifier: Modifier = Modifier) {
    val frameSize = ProfileCardBackDefaults.qrFrameSize
    val frameShape = SketchRoundRectShape(
        seed = seed,
        roughness = profileCardRoughness(frameSize, sketchiness),
        tremor = profileCardTremor(frameSize, sketchiness),
        sweepWavelength = ProfileCardSweepWavelength,
        cornerRadius = 12.dp,
        borderThickness = 1.5.dp,
    )
    Box(
        modifier = modifier.size(frameSize).sketchBorder(frameShape, ProfileCardColors.ink),
        contentAlignment = Alignment.Center,
    ) {
        val plateSize = ProfileCardBackDefaults.qrPlateSize
        val plateShape = SketchRoundRectShape(
            seed = seed + 1,
            roughness = profileCardRoughness(plateSize, sketchiness),
            tremor = profileCardTremor(plateSize, sketchiness),
            sweepWavelength = ProfileCardSweepWavelength,
            cornerRadius = 8.dp,
            borderThickness = 0.dp,
        )
        Box(
            modifier = Modifier
                .size(plateSize)
                .clip(plateShape)
                .background(ProfileCardColors.brightPlate)
                .padding(ProfileCardBackDefaults.qrPlateInset),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(ProfileCardBackDefaults.qrPaperCornerRadius))
                    .background(ProfileCardColors.qrPaper)
                    .padding(ProfileCardBackDefaults.qrQuietZone),
            ) {
                QrPattern(link = link, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/** The modules of a real QR code encoding [link]; a blank link leaves the plate empty. */
@Composable
private fun QrPattern(link: String, modifier: Modifier = Modifier) {
    val moduleColor = ProfileCardColors.qrModule
    val modules = remember(link) {
        if (link.isBlank()) {
            emptyList()
        } else {
            val processor = QRCodeProcessor(link, ProfileCardBackDefaults.qrErrorCorrectionLevel)
            // A short link would encode into a few coarse modules; the minimum version keeps the
            // pattern as fine as the design's.
            processor
                .encode(
                    type = maxOf(
                        ProfileCardBackDefaults.qrMinimumVersion,
                        QRCodeProcessor.infoDensityForDataAndECL(link, ProfileCardBackDefaults.qrErrorCorrectionLevel),
                    ),
                )
                .map { row -> row.map(QRCodeSquare::dark) }
        }
    }
    Canvas(modifier = modifier.size(ProfileCardBackDefaults.qrPlateSize - (ProfileCardBackDefaults.qrPlateInset + ProfileCardBackDefaults.qrQuietZone) * 2)) {
        if (modules.isEmpty()) return@Canvas
        val cell = size.width / modules.size
        modules.forEachIndexed { row, cells ->
            cells.forEachIndexed { column, dark ->
                if (dark) {
                    drawRect(
                        color = moduleColor,
                        topLeft = Offset(column * cell, row * cell),
                        size = Size(cell, cell),
                    )
                }
            }
        }
    }
}

/**
 * The gently rolling ground line the mascot stands on, traced from the Figma "Ground Line"
 * vector (a single stroked curve cresting near its middle) rather than the sketch system's usual
 * hand-jittered divider — this one line is a fixed hill shape, not a per-user random wobble.
 */
@Composable
private fun GroundLine(modifier: Modifier = Modifier) {
    val ink = ProfileCardColors.ink
    Canvas(modifier = modifier.height(ProfileCardBackDefaults.groundLineHeight)) {
        val scaleX = size.width / GROUND_LINE_SOURCE_WIDTH
        val scaleY = size.height / GROUND_LINE_SOURCE_HEIGHT
        val path = Path().apply {
            moveTo(1f * scaleX, 6.5f * scaleY)
            cubicTo(21f * scaleX, 4.5f * scaleY, 41f * scaleX, 3.3f * scaleY, 61f * scaleX, 2.5f * scaleY)
            cubicTo(86f * scaleX, 1.5f * scaleY, 106f * scaleX, 1f * scaleY, 131f * scaleX, 1f * scaleY)
            cubicTo(151f * scaleX, 1f * scaleY, 161f * scaleX, 2f * scaleY, 171f * scaleX, 3f * scaleY)
            cubicTo(191f * scaleX, 4f * scaleY, 206f * scaleX, 5.5f * scaleY, 221f * scaleX, 5.5f * scaleY)
            cubicTo(241f * scaleX, 6f * scaleY, 251f * scaleX, 6.7f * scaleY, 261f * scaleX, 7f * scaleY)
        }
        drawPath(
            path,
            color = ink,
            style = Stroke(width = ProfileCardBackDefaults.groundLineStrokeWidth.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

private const val GROUND_LINE_SOURCE_WIDTH = 262f
private const val GROUND_LINE_SOURCE_HEIGHT = 8f

/**
 * Two hand-drawn tufts of grass along the ground line, traced from Figma's own sparse pair — each
 * a small peaked "^", not a plain vertical tick.
 */
@Composable
private fun GrassTicks(modifier: Modifier = Modifier) {
    val ink = ProfileCardColors.ink
    Canvas(modifier = modifier.height(ProfileCardBackDefaults.groundLineHeight)) {
        ProfileCardBackDefaults.grassTicks.forEach { tick ->
            val x = size.width * tick.fraction
            val bottom = tick.baseY.toPx()
            val tickHeight = tick.height.toPx()
            val halfWidth = tickHeight * 0.42f
            val strokeWidth = tickHeight * 0.3f
            val path = Path().apply {
                moveTo(x - halfWidth, bottom)
                lineTo(x, bottom - tickHeight)
                lineTo(x + halfWidth, bottom)
            }
            drawPath(
                path,
                color = ink,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
    }
}

/**
 * A thin flagpole with a small pennant near its top, traced from Figma's separate "Flag Pole"
 * and "Pennant" layers rather than one squat combined shape. Both are fixed cubic-bezier traces
 * — the pole bows slightly rather than running straight, and the pennant's tip sits close to its
 * top edge rather than centered — positioned as fractions of their *combined* source bounding
 * box, the same "exact traced path, fractions of the box" technique [GroundLine] uses. Figma's
 * 6° rotation of both layers is already baked into that source data (their relative positions
 * encode the tilt), so nothing here applies a separate `Modifier.rotate` on top — doing so would
 * rotate an already-tilted trace a second time. Fills the pennant in the app's active `primary`
 * colour, not the fixed ink the pole uses — the two colours must stay independently
 * theme-tintable, which is why this is drawn as a plain traced path rather than a single-colour
 * vector-drawable asset (the technique [MascotIcon] uses) that a `ColorFilter.tint` could only
 * retint as one flat colour.
 */
@Composable
private fun FlagMark(modifier: Modifier = Modifier) {
    val ink = ProfileCardColors.ink
    val pennantColor = ProfileCardColors.duskBand
    Canvas(
        modifier = modifier.size(width = ProfileCardBackDefaults.flagWidth, height = ProfileCardBackDefaults.flagHeight),
    ) {
        fun px(xFraction: Float, yFraction: Float) = Offset(size.width * xFraction, size.height * yFraction)

        val poleStart = px(0.1916f, 0.0448f)
        val poleC1 = px(0.1687f, 0.3582f)
        val poleC2 = px(0.1230f, 0.6866f)
        val poleEnd = px(0f, 1f)
        val polePath = Path().apply {
            moveTo(poleStart.x, poleStart.y)
            cubicTo(poleC1.x, poleC1.y, poleC2.x, poleC2.y, poleEnd.x, poleEnd.y)
        }
        drawPath(
            polePath,
            color = ink,
            style = Stroke(width = ProfileCardBackDefaults.flagPoleWidth.toPx(), cap = StrokeCap.Round),
        )

        val pennantStart = px(0.2488f, 0f)
        val pennantPath = Path().apply {
            moveTo(pennantStart.x, pennantStart.y)
            val c1 = px(0.5088f, 0.0068f)
            val c2 = px(0.7393f, 0.0091f)
            val tip = px(1f, 0.0188f)
            cubicTo(c1.x, c1.y, c2.x, c2.y, tip.x, tip.y)
            val c3 = px(0.7519f, 0.0715f)
            val c4 = px(0.5304f, 0.1136f)
            val back = px(0.2817f, 0.1633f)
            cubicTo(c3.x, c3.y, c4.x, c4.y, back.x, back.y)
            val c5 = px(0.2867f, 0.1030f)
            val c6 = px(0.2778f, 0.0584f)
            cubicTo(c5.x, c5.y, c6.x, c6.y, pennantStart.x, pennantStart.y)
            close()
        }
        drawPath(pennantPath, color = pennantColor)
    }
}

/** One tuft of grass: where along the ground line it stands, and its base and height in that line's box. */
private data class GrassTick(val fraction: Float, val baseY: Dp, val height: Dp)

private object ProfileCardBackDefaults {
    val bannerHeight = 148.5.dp
    val scanMeOffset = DpOffset(0.dp, 56.5.dp)
    val qrFrameOffset = DpOffset(65.dp, 163.5.dp)
    val qrFrameSize = 190.dp
    val qrPlateSize = 154.dp
    val qrPlateInset = 11.dp
    val qrPaperCornerRadius = 4.dp
    val qrQuietZone = 14.dp
    val qrErrorCorrectionLevel = ErrorCorrectionLevel.HIGH
    val qrMinimumVersion = 4
    val mascotOffset = DpOffset(94.dp, 403.dp)

    // Wide enough that every mascot's drawable is scaled to the box's height, whatever its aspect
    // ratio, so all five stand the same height on the ground line.
    val mascotBox = DpSize(70.dp, 52.dp)
    val mascotRotationDegrees = 5f
    val flagOffset = DpOffset(191.5.dp, 392.5.dp)
    val flagWidth = 35.5.dp
    val flagHeight = 67.dp
    val flagPoleWidth = 2.dp
    val groundLineOffset = DpOffset(37.5.dp, 450.dp)
    val groundLineWidth = 245.dp
    val groundLineHeight = 8.dp
    val groundLineStrokeWidth = 2.dp

    val grassTicks = listOf(
        GrassTick(fraction = 0.146f, baseY = 5.5.dp, height = 4.8.dp),
        GrassTick(fraction = 0.779f, baseY = 6.5.dp, height = 5.8.dp),
    )

    // Traced from the Figma back face: the two sparks sitting on the banner.
    val bannerSparkles = listOf(
        SparklePlacement(x = 39.dp, y = 117.5.dp, size = 13.dp, rotationDegrees = 12f),
        SparklePlacement(x = 67.dp, y = 131.dp, size = 7.5.dp, rotationDegrees = -20f),
    )

    // Traced from the Figma back face: the seven sparks scattered below the banner and around
    // the ground scene.
    val groundSparkles = listOf(
        SparklePlacement(x = 37.dp, y = 196.dp, size = 13.dp, rotationDegrees = 12f),
        SparklePlacement(x = 50.5.dp, y = 248.5.dp, size = 8.5.dp, rotationDegrees = -20f),
        SparklePlacement(x = 279.dp, y = 199.dp, size = 11.dp, rotationDegrees = 12f),
        SparklePlacement(x = 287.dp, y = 254.dp, size = 7.5.dp, rotationDegrees = -20f),
        SparklePlacement(x = 41.5.dp, y = 322.5.dp, size = 9.5.dp, rotationDegrees = 12f),
        SparklePlacement(x = 94.dp, y = 376.dp, size = 9.5.dp, rotationDegrees = 10f),
        SparklePlacement(x = 225.5.dp, y = 428.dp, size = 11.dp, rotationDegrees = -18f),
    )
}

/**
 * The "SCAN ME" banner filling the top of the back face, traced from the Figma "Color Band"
 * vector — the same torn-edge shape the front dusk band uses (see `DuskBandShape`), just filled
 * in [ProfileCardColors.banner] instead of [ProfileCardColors.duskBand].
 */
private val BackBannerShape = TracedEdgeShape(
    sourceWidth = 338f,
    sourceHeight = 154f,
    edgeStartY = 149f,
    edge = listOf(
        TracedEdgeShape.EdgeSegment(321.907f, 145.76f, 305.81f, 147.144f, 289.71f, 153.15f),
        TracedEdgeShape.EdgeSegment(273.617f, 153.237f, 257.523f, 152.06f, 241.43f, 149.62f),
        TracedEdgeShape.EdgeSegment(225.336f, 151.44f, 209.24f, 152.217f, 193.14f, 151.95f),
        TracedEdgeShape.EdgeSegment(177.047f, 152.01f, 160.953f, 151.183f, 144.86f, 149.47f),
        TracedEdgeShape.EdgeSegment(128.76f, 147.43f, 112.664f, 148.51f, 96.5703f, 152.71f),
        TracedEdgeShape.EdgeSegment(80.477f, 151.183f, 64.3834f, 150.124f, 48.29f, 149.53f),
        TracedEdgeShape.EdgeSegment(32.2469f, 151.988f, 16.2074f, 151.82f, 0.170898f, 149.028f),
    ),
)

@LocalePreviews
@Composable
private fun ProfileCardBackPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardBack(
            nickName = "droidkaigi",
            link = "https://example.com",
            mascot = Mascot.C,
            sketchiness = Sketchiness.Normal,
            doodle = Doodle.fakeOnCardFace(),
            taped = true,
            modifier = Modifier.padding(24.dp),
        )
    }
}

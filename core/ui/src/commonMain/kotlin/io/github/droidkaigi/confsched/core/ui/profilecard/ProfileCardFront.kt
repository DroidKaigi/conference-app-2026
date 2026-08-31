package io.github.droidkaigi.confsched.core.ui.profilecard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.ByteArrayImage
import io.github.droidkaigi.confsched.core.ui.DoodleLayerView
import io.github.droidkaigi.confsched.core.ui.DoodleOrigin
import io.github.droidkaigi.confsched.core.ui.SketchEllipseShape
import io.github.droidkaigi.confsched.core.ui.SketchHorizontalDivider
import io.github.droidkaigi.confsched.core.ui.SketchOutlineShape
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_dates
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_event_label
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_greeting
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_venue
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * The card's front face, a "seal": the chosen mascot as a small badge, the user's photo in a
 * wobbly circular plate (a placeholder face when none was picked), and their nickname/occupation
 * over the event's name, venue, and dates, under whatever [doodle] the user has drawn on it.
 */
@Composable
fun ProfileCardFront(
    nickName: String,
    occupation: String,
    mascot: Mascot,
    sketchiness: Sketchiness,
    avatarImage: AvatarImage?,
    doodle: Doodle,
    taped: Boolean,
    modifier: Modifier = Modifier,
) {
    val seed = nickName.hashCode()
    ProfileCardFace(sketchiness = sketchiness, outlineSeed = seed, topStartTape = taped, bottomEndTape = taped, mirrored = false, modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ProfileCardFrontDefaults.duskBandHeight)
                .clip(DuskBandShape)
                .background(ProfileCardColors.duskBand),
        )
        EventLabelHeader(text = stringResource(Res.string.card_event_label), color = ProfileCardColors.onDuskBand, centeredLabel = false)
        ProfilePhotoPlate(
            seed = seed + 2,
            sketchiness = sketchiness,
            avatarImage = avatarImage,
            modifier = Modifier.cardOffset(ProfileCardFrontDefaults.photoPlateOffset),
        )
        SpeechBubble(
            text = stringResource(Res.string.card_greeting),
            modifier = Modifier.cardOffset(ProfileCardFrontDefaults.bubbleOffset),
        )
        Sparkles(ProfileCardFrontDefaults.duskBandSparkles, ProfileCardColors.onDuskBand)
        Sparkles(ProfileCardFrontDefaults.plateSparkles, ProfileCardColors.ink)
        Text(
            text = nickName,
            color = ProfileCardColors.ink,
            style = ProfileCardTextStyles.display,
            modifier = Modifier.cardOffset(ProfileCardFrontDefaults.nickNameOffset).width(ProfileCardFrontDefaults.nickNameWidth),
        )
        Text(
            text = occupation,
            color = ProfileCardColors.mutedInk,
            style = ProfileCardTextStyles.caption,
            modifier = Modifier.cardOffset(ProfileCardFrontDefaults.occupationOffset).width(ProfileCardFrontDefaults.occupationWidth),
        )
        SketchHorizontalDivider(
            seed = seed + 3,
            color = ProfileCardColors.hairline,
            thickness = ProfileCardFrontDefaults.dividerThickness,
            modifier = Modifier.cardOffset(ProfileCardFrontDefaults.dividerOffset).width(ProfileCardFrontDefaults.dividerWidth),
        )
        Text(
            text = stringResource(Res.string.card_venue),
            color = ProfileCardColors.ink,
            style = ProfileCardTextStyles.accent,
            modifier = Modifier.cardOffset(ProfileCardFrontDefaults.venueOffset),
        )
        Text(
            text = stringResource(Res.string.card_dates),
            color = ProfileCardColors.mutedInk,
            style = ProfileCardTextStyles.accent,
            modifier = Modifier.cardOffset(ProfileCardFrontDefaults.datesOffset),
        )
        MascotSealBadge(
            mascot = mascot,
            sketchiness = sketchiness,
            seed = seed + 4,
            modifier = Modifier.cardOffset(ProfileCardFrontDefaults.sealOffset).rotate(ProfileCardFrontDefaults.sealRotationDegrees),
        )
        DoodleLayerView(
            doodle = doodle,
            color = ProfileCardColors.ink,
            haloColor = ProfileCardColors.plate,
            origin = DoodleOrigin.TopStart,
            scale = 1f,
            modifier = Modifier.matchParentSize(),
        )
    }
}

@Composable
private fun ProfilePhotoPlate(seed: Int, sketchiness: Sketchiness, avatarImage: AvatarImage?, modifier: Modifier = Modifier) {
    val plateSize = ProfileCardFrontDefaults.photoPlateSize
    val shape = SketchEllipseShape(
        seed = seed,
        roughness = profileCardRoughness(plateSize, sketchiness),
        tremor = profileCardTremor(plateSize, sketchiness),
        sweepWavelength = ProfileCardSweepWavelength,
        borderThickness = ProfileCardFrontDefaults.plateBorderThickness,
    )
    Box(
        modifier = modifier
            .size(plateSize)
            .clip(shape)
            .background(if (avatarImage == null) ProfileCardColors.banner else ProfileCardColors.plate)
            .sketchBorder(shape, ProfileCardColors.ink),
        contentAlignment = Alignment.Center,
    ) {
        if (avatarImage != null) {
            ByteArrayImage(
                bytes = avatarImage.bytes,
                contentDescription = null,
                modifier = Modifier.size(plateSize).clip(shape),
                contentScale = ContentScale.Crop,
            )
        } else {
            PlaceholderFace(modifier = Modifier.size(plateSize))
        }
    }
}

/**
 * A minimal drawn face standing in for the user's photo, shown until one is picked. Matches the
 * design's own sample face, which is a placeholder rather than an asset to export: two crescent
 * eyes over a shallow smile, all fractions of the plate it fills.
 */
@Composable
private fun PlaceholderFace(modifier: Modifier = Modifier, color: Color = ProfileCardColors.onBanner) {
    Canvas(modifier = modifier) { drawPlaceholderFace(color) }
}

internal fun DrawScope.drawPlaceholderFace(color: Color) {
    val plate = size.minDimension
    val eyeStroke = Stroke(width = plate * 0.013f, cap = StrokeCap.Round)
    val eyeSize = Size(plate * 0.045f, plate * 0.053f)
    listOf(0.5f - 0.145f, 0.5f + 0.145f).forEach { eyeX ->
        drawArc(
            color = color,
            startAngle = -70f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(size.width * eyeX - eyeSize.width / 2f, size.height * 0.427f - eyeSize.height / 2f),
            size = eyeSize,
            style = eyeStroke,
        )
    }
    val smileSize = Size(plate * 0.20f, plate * 0.117f)
    drawArc(
        color = color,
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(size.width / 2f - smileSize.width / 2f, size.height * 0.505f),
        size = smileSize,
        style = Stroke(width = plate * 0.017f, cap = StrokeCap.Round),
    )
}

@Composable
private fun SpeechBubble(text: String, modifier: Modifier = Modifier) {
    val fill = ProfileCardColors.brightPlate
    val ink = ProfileCardColors.onBanner
    val bubble = remember(fill, ink) { speechBubbleVector(fill = fill, ink = ink) }
    Box(
        modifier = modifier
            .size(ProfileCardFrontDefaults.bubbleWidth, ProfileCardFrontDefaults.bubbleHeight)
            .rotate(ProfileCardFrontDefaults.bubbleRotationDegrees),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = rememberVectorPainter(bubble),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
        )
        Text(
            text = text,
            color = ink,
            style = ProfileCardTextStyles.accent,
            modifier = Modifier
                .offset(x = ProfileCardFrontDefaults.bubbleTextOffset.x, y = ProfileCardFrontDefaults.bubbleTextOffset.y)
                .rotate(ProfileCardFrontDefaults.bubbleTextRotationDegrees),
        )
    }
}

/** The Figma "Hi Bubble" vector as exported, coloured from the theme instead of its baked hex values. */
private fun speechBubbleVector(fill: Color, ink: Color): ImageVector = ImageVector.Builder(
    name = "SpeechBubble",
    defaultWidth = SPEECH_BUBBLE_VIEWPORT_WIDTH.dp,
    defaultHeight = SPEECH_BUBBLE_VIEWPORT_HEIGHT.dp,
    viewportWidth = SPEECH_BUBBLE_VIEWPORT_WIDTH,
    viewportHeight = SPEECH_BUBBLE_VIEWPORT_HEIGHT,
).apply {
    addPath(
        pathData = PathParser().parsePathString(SPEECH_BUBBLE_PATH).toNodes(),
        fill = SolidColor(fill),
        stroke = SolidColor(ink),
        strokeLineWidth = SPEECH_BUBBLE_STROKE_WIDTH,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
}.build()

private const val SPEECH_BUBBLE_VIEWPORT_WIDTH = 62f
private const val SPEECH_BUBBLE_VIEWPORT_HEIGHT = 44.8129f
private const val SPEECH_BUBBLE_STROKE_WIDTH = 1.6f
private const val SPEECH_BUBBLE_PATH =
    "M10.8 0.8H51.2C57.8667 0.8 61.2 4.13333 61.2 10.8V21.2C61.2 27.8667 57.8667 31.2 51.2 31.2L21.34 31.26L9.26 43.2L6.84 31.08L10.8 31.2C4.13333 31.2 0.8 27.8667 0.8 21.2V10.8C0.8 4.13333 4.13333 0.8 10.8 0.8Z"

/** The chosen mascot, drawn in the app's active `primary` colour — matching the front face's dusk
 * band — inside a jagged wax-seal badge, traced from the Figma source. */
@Composable
private fun MascotSealBadge(mascot: Mascot, sketchiness: Sketchiness, seed: Int, modifier: Modifier = Modifier) {
    val badgeSize = ProfileCardFrontDefaults.badgeSize
    val shape = MascotSealShape(
        seed = seed,
        jitter = ProfileCardFrontDefaults.sealJitter * sketchiness.amplitudeMultiplier,
        borderThickness = ProfileCardFrontDefaults.sealBorderThickness,
    )
    Box(
        modifier = modifier
            .size(badgeSize)
            .clip(shape)
            .background(ProfileCardColors.plate)
            .sketchBorder(shape, ProfileCardColors.ink),
        contentAlignment = Alignment.Center,
    ) {
        MascotIcon(
            mascot = mascot,
            modifier = Modifier
                .offset(x = ProfileCardFrontDefaults.sealMascotOffset.x, y = ProfileCardFrontDefaults.sealMascotOffset.y)
                .size(ProfileCardFrontDefaults.sealMascotBox)
                // The seal turns as a whole; the mascot stays in the pose its artwork has.
                .rotate(-ProfileCardFrontDefaults.sealRotationDegrees),
        )
    }
}

/**
 * A wax-seal outline: [pointCount] sharp points around a circle, each running straight down to the
 * valley between it and the next, traced from the Figma badge that frames the mascot on the front
 * face.
 */
private data class MascotSealShape(
    val seed: Int,
    val pointCount: Int = 14,
    val jitter: Float = 0f,
    override val borderThickness: Dp = 0.dp,
) : SketchOutlineShape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val random = Random(seed)
        val inset = with(density) { borderThickness.toPx() / 2f }
        val center = Offset(size.width / 2f, size.height / 2f)
        val pointRadius = size.minDimension / 2f - inset
        val valleyRadius = pointRadius * SEAL_VALLEY_RADIUS_RATIO
        val step = (2.0 * PI / pointCount).toFloat()
        val path = Path()
        repeat(pointCount) { index ->
            val pointAngle = step * index - (PI / 2).toFloat()
            val point = center + polarOffset(pointAngle + random.angleWobble(step), pointRadius * random.radiusWobble())
            val valley = center + polarOffset(pointAngle + step / 2f + random.angleWobble(step), valleyRadius * random.radiusWobble())
            if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
            path.lineTo(valley.x, valley.y)
        }
        path.close()
        return Outline.Generic(path)
    }

    private fun Random.radiusWobble(): Float = 1f + (nextFloat() - 0.5f) * jitter

    private fun Random.angleWobble(step: Float): Float = (nextFloat() - 0.5f) * jitter * step / 2f
}

/** The valley radius as a fraction of the point radius, measured on the Figma "Seal Edge" vector. */
private const val SEAL_VALLEY_RADIUS_RATIO = 0.85f

private fun polarOffset(angle: Float, radius: Float): Offset = Offset(cos(angle), sin(angle)) * radius

/**
 * The dusk band filling the top of the front face, traced from the Figma "Color Band" vector —
 * the same torn-edge shape the back banner uses (see `BackBannerShape`), just filled in
 * [ProfileCardColors.duskBand] instead of [ProfileCardColors.banner].
 */
private val DuskBandShape = TracedEdgeShape(
    sourceWidth = 338f,
    sourceHeight = 203f,
    edgeStartY = 199f,
    edge = listOf(
        TracedEdgeShape.EdgeSegment(321.907f, 197.58f, 305.81f, 196.789f, 289.71f, 196.629f),
        TracedEdgeShape.EdgeSegment(273.617f, 200.836f, 257.523f, 202.747f, 241.43f, 202.36f),
        TracedEdgeShape.EdgeSegment(225.336f, 198.08f, 209.24f, 196.427f, 193.14f, 197.4f),
        TracedEdgeShape.EdgeSegment(177.047f, 196.353f, 160.953f, 195.456f, 144.86f, 194.71f),
        TracedEdgeShape.EdgeSegment(128.76f, 199.376f, 112.664f, 199.89f, 96.5703f, 196.25f),
        TracedEdgeShape.EdgeSegment(80.477f, 199.463f, 64.3834f, 199.1f, 48.29f, 195.16f),
        TracedEdgeShape.EdgeSegment(32.3203f, 197.309f, 16.3538f, 198.584f, 0.390625f, 198.988f),
    ),
)

private object ProfileCardFrontDefaults {
    val duskBandHeight = 194.dp
    val photoPlateSize = 141.dp
    val photoPlateOffset = DpOffset(89.dp, 113.dp)
    val plateBorderThickness = 2.dp
    val bubbleWidth = 58.4.dp
    val bubbleHeight = 43.dp
    val bubbleOffset = DpOffset(211.1.dp, 106.4.dp)
    val bubbleTextOffset = DpOffset(1.2.dp, (-5.6).dp)
    val bubbleRotationDegrees = 12f

    // The greeting turns inside the already-rotated bubble, so this adds to [bubbleRotationDegrees].
    val bubbleTextRotationDegrees = -1.5f
    val nickNameOffset = DpOffset(22.5.dp, 271.dp)
    val nickNameWidth = 275.dp
    val occupationOffset = DpOffset(22.5.dp, 324.5.dp)
    val occupationWidth = 267.dp
    val dividerOffset = DpOffset(22.5.dp, 354.5.dp)
    val dividerWidth = 184.dp
    val dividerThickness = 1.5.dp
    val venueOffset = DpOffset(22.5.dp, 368.5.dp)
    val datesOffset = DpOffset(22.5.dp, 390.dp)
    val sealOffset = DpOffset(228.5.dp, 383.dp)
    val badgeSize = 76.dp
    val sealBorderThickness = 1.4.dp
    val sealJitter = 0.018f
    val sealRotationDegrees = 8f

    // Wide enough that every mascot's drawable is scaled to the box's height, whatever its aspect
    // ratio, so all five reach the same ink height.
    val sealMascotBox = DpSize(64.dp, 46.dp)
    val sealMascotOffset = DpOffset(0.dp, 0.dp)

    // Traced from the Figma front face: six sparks, alternating 12°/-20° rotation, split by
    // which background they sit on so each reads against it.
    val duskBandSparkles = listOf(
        SparklePlacement(x = 39.dp, y = 123.dp, size = 13.dp, rotationDegrees = 12f),
        SparklePlacement(x = 65.5.dp, y = 95.dp, size = 8.5.dp, rotationDegrees = -20f),
        SparklePlacement(x = 287.dp, y = 146.5.dp, size = 7.5.dp, rotationDegrees = -20f),
    )
    val plateSparkles = listOf(
        SparklePlacement(x = 279.dp, y = 208.5.dp, size = 11.dp, rotationDegrees = 12f),
        SparklePlacement(x = 282.dp, y = 293.5.dp, size = 9.5.dp, rotationDegrees = 12f),
        SparklePlacement(x = 63.dp, y = 254.dp, size = 7.5.dp, rotationDegrees = -20f),
    )
}

@LocalePreviews
@Composable
private fun ProfileCardFrontPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardFront(
            nickName = "droidkaigi",
            occupation = "Software Engineer",
            mascot = Mascot.C,
            sketchiness = Sketchiness.Normal,
            taped = true,
            avatarImage = null,
            doodle = Doodle.fakeOnCardFace(),
            modifier = Modifier.padding(24.dp),
        )
    }
}

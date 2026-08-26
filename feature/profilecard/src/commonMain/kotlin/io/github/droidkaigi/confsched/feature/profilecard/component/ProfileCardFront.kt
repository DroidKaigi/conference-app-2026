package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.ByteArrayImage
import io.github.droidkaigi.confsched.core.ui.SketchEllipseShape
import io.github.droidkaigi.confsched.core.ui.SketchHorizontalDivider
import io.github.droidkaigi.confsched.core.ui.SketchOutlineShape
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.card_dates
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.card_event_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.card_greeting
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.card_venue
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * The card's front face, a "seal": the chosen mascot as a small badge, the user's photo in a
 * wobbly circular plate (a placeholder face when none was picked), and their nickname/occupation
 * over the event's name, venue, and dates.
 */
@Composable
fun ProfileCardFront(
    nickName: String,
    occupation: String,
    mascot: Mascot,
    sketchiness: Sketchiness,
    avatarImage: AvatarImage?,
    taped: Boolean,
    modifier: Modifier = Modifier,
) {
    val seed = nickName.hashCode()
    ProfileCardFace(sketchiness = sketchiness, seed = seed, topStartTape = taped, bottomEndTape = taped, mirrored = false, modifier = modifier) {
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
    val shape = SpeechBubbleShape(borderThickness = ProfileCardFrontDefaults.bubbleBorderThickness)
    Box(
        modifier = modifier
            .size(ProfileCardFrontDefaults.bubbleWidth, ProfileCardFrontDefaults.bubbleHeight)
            .clip(shape)
            .background(ProfileCardColors.brightPlate)
            .sketchBorder(shape, ProfileCardColors.onBanner),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = ProfileCardColors.onBanner,
            style = ProfileCardTextStyles.accent,
            modifier = Modifier
                .offset(x = ProfileCardFrontDefaults.bubbleTextOffset.x, y = ProfileCardFrontDefaults.bubbleTextOffset.y)
                .rotate(ProfileCardFrontDefaults.bubbleTextRotationDegrees),
        )
    }
}

/**
 * The greeting bubble's outline, traced from the Figma badge: a tilted rounded body with a
 * small tail pointing toward the avatar, rather than a plain rounded rectangle.
 */
private data class SpeechBubbleShape(override val borderThickness: Dp = 0.dp) : SketchOutlineShape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val inset = with(density) { borderThickness.toPx() / 2f }
        val scaleX = (size.width - inset * 2f) / SOURCE_WIDTH
        val scaleY = (size.height - inset * 2f) / SOURCE_HEIGHT

        fun point(x: Float, y: Float) = Offset(x * scaleX + inset, y * scaleY + inset)

        fun Path.curveTo(c1: Offset, c2: Offset, end: Offset) = cubicTo(c1.x, c1.y, c2.x, c2.y, end.x, end.y)

        val path = Path()
        val start = point(15.1872f, 1.16408f)
        path.moveTo(start.x, start.y)
        point(54.7044f, 9.56371f).let { path.lineTo(it.x, it.y) }
        path.curveTo(point(61.2254f, 10.9498f), point(63.7928f, 14.9033f), point(62.4068f, 21.4243f))
        point(60.2445f, 31.597f).let { path.lineTo(it.x, it.y) }
        path.curveTo(point(58.8584f, 38.118f), point(54.9049f, 40.6855f), point(48.3839f, 39.2994f))
        point(19.1639f, 33.1498f).let { path.lineTo(it.x, it.y) }
        point(4.86543f, 42.3174f).let { path.lineTo(it.x, it.y) }
        point(5.01821f, 29.9591f).let { path.lineTo(it.x, it.y) }
        point(8.86672f, 30.8998f).let { path.lineTo(it.x, it.y) }
        path.curveTo(point(2.34574f, 29.5137f), point(-0.221714f, 25.5602f), point(1.16436f, 19.0392f))
        point(3.32664f, 8.86644f).let { path.lineTo(it.x, it.y) }
        path.curveTo(point(4.71272f, 2.34545f), point(8.66625f, -0.222f), start)
        path.close()
        return Outline.Generic(path)
    }

    private companion object {
        const val SOURCE_WIDTH = 64f
        const val SOURCE_HEIGHT = 44f
    }
}

/** The chosen mascot, drawn in the app's active `primary` colour — matching the front face's dusk
 * band — inside a scalloped wax-seal badge, traced from the Figma source. */
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
                .size(ProfileCardFrontDefaults.sealMascotBox),
        )
    }
}

/**
 * A wax-seal outline: [petalCount] smooth rounded scallops around a circle, traced from the
 * Figma badge that frames the mascot on the front face.
 */
private data class MascotSealShape(
    val seed: Int,
    val petalCount: Int = 13,
    val jitter: Float = 0f,
    override val borderThickness: Dp = 0.dp,
) : SketchOutlineShape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val random = Random(seed)
        val inset = with(density) { borderThickness.toPx() / 2f }
        val center = Offset(size.width / 2f, size.height / 2f)
        val outerRadius = size.minDimension / 2f - inset
        val valleyRadius = outerRadius * 0.84f
        val bumpRadius = outerRadius * 1.05f
        val path = Path()
        repeat(petalCount) { index ->
            val wobble = 1f + (random.nextFloat() - 0.5f) * jitter
            val startAngle = (2.0 * PI * index / petalCount - PI / 2).toFloat()
            val midAngle = (2.0 * PI * (index + 0.5) / petalCount - PI / 2).toFloat()
            val endAngle = (2.0 * PI * (index + 1) / petalCount - PI / 2).toFloat()
            val start = center + Offset(cos(startAngle), sin(startAngle)) * valleyRadius
            val control = center + Offset(cos(midAngle), sin(midAngle)) * (bumpRadius * wobble)
            val end = center + Offset(cos(endAngle), sin(endAngle)) * valleyRadius
            if (index == 0) path.moveTo(start.x, start.y) else path.lineTo(start.x, start.y)
            path.quadraticTo(control.x, control.y, end.x, end.y)
        }
        path.close()
        return Outline.Generic(path)
    }
}

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
    val bubbleWidth = 67.dp
    val bubbleHeight = 47.dp
    val bubbleOffset = DpOffset(210.dp, 97.5.dp)
    val bubbleTextOffset = DpOffset(2.dp, (-4).dp)
    val bubbleBorderThickness = 4.dp

    // The bubble outline is traced already tilted; only the greeting needs turning to sit along it.
    val bubbleTextRotationDegrees = -8f
    val nickNameOffset = DpOffset(22.5.dp, 271.dp)
    val nickNameWidth = 275.dp
    val occupationOffset = DpOffset(22.5.dp, 324.5.dp)
    val occupationWidth = 267.dp
    val dividerOffset = DpOffset(22.5.dp, 354.5.dp)
    val dividerWidth = 184.dp
    val dividerThickness = 1.5.dp
    val venueOffset = DpOffset(22.5.dp, 368.5.dp)
    val datesOffset = DpOffset(22.5.dp, 390.dp)
    val sealOffset = DpOffset(219.dp, 384.5.dp)
    val badgeSize = 80.dp
    val sealBorderThickness = 2.dp
    val sealJitter = 0.05f
    val sealRotationDegrees = 8f

    // Wide enough that every mascot's drawable is scaled to the box's height, whatever its aspect
    // ratio, so all five reach the same ink height.
    val sealMascotBox = DpSize(70.dp, 50.dp)
    val sealMascotOffset = DpOffset((-3.5).dp, (-3).dp)

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
            mascot = Mascot.Koala,
            sketchiness = Sketchiness.Normal,
            taped = true,
            avatarImage = null,
            modifier = Modifier.padding(24.dp),
        )
    }
}

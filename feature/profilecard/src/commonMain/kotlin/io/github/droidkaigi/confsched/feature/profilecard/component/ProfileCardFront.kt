package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
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
    modifier: Modifier = Modifier,
) {
    val seed = nickName.hashCode()
    ProfileCardFace(sketchiness = sketchiness, seed = seed, modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ProfileCardFrontDefaults.duskBandHeight)
                .clip(DuskBandShape)
                .background(ProfileCardColors.duskBand),
        )
        CornerBracket(
            mirrored = false,
            modifier = Modifier.align(Alignment.TopStart).padding(ProfileCardFrontDefaults.cornerPadding),
        )
        CornerBracket(
            mirrored = true,
            modifier = Modifier.align(Alignment.TopEnd).padding(ProfileCardFrontDefaults.cornerPadding),
        )
        Text(
            text = stringResource(Res.string.card_event_label),
            color = ProfileCardColors.onDuskBand,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.TopStart).padding(ProfileCardFrontDefaults.textPadding),
        )
        Box(
            modifier = Modifier.align(Alignment.TopCenter).offset(y = ProfileCardFrontDefaults.plateOffsetY),
        ) {
            ProfilePhotoPlate(seed = seed + 2, sketchiness = sketchiness, avatarImage = avatarImage)
            SpeechBubble(
                text = stringResource(Res.string.card_greeting),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = ProfileCardFrontDefaults.bubbleOffsetX, y = ProfileCardFrontDefaults.bubbleOffsetY),
            )
        }
        ProfileCardFrontDefaults.duskBandSparkles.forEach { sparkle ->
            SketchSparkle(
                color = ProfileCardColors.onDuskBand,
                markSize = sparkle.size,
                modifier = Modifier
                    .offset(x = sparkle.x - sparkle.size / 2, y = sparkle.y - sparkle.size / 2)
                    .rotate(sparkle.rotationDegrees),
            )
        }
        ProfileCardFrontDefaults.plateSparkles.forEach { sparkle ->
            SketchSparkle(
                color = ProfileCardColors.ink,
                markSize = sparkle.size,
                modifier = Modifier
                    .offset(x = sparkle.x - sparkle.size / 2, y = sparkle.y - sparkle.size / 2)
                    .rotate(sparkle.rotationDegrees),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = ProfileCardFrontDefaults.textBlockOffsetY)
                .padding(horizontal = ProfileCardFrontDefaults.textPadding),
        ) {
            Text(nickName, color = ProfileCardColors.ink, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(occupation, color = ProfileCardColors.ink, style = MaterialTheme.typography.bodyMedium)
            SketchHorizontalDivider(
                seed = seed + 3,
                color = ProfileCardColors.ink,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            )
            Text(
                stringResource(Res.string.card_venue),
                color = ProfileCardColors.ink,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(stringResource(Res.string.card_dates), color = ProfileCardColors.ink, style = MaterialTheme.typography.labelSmall)
        }
        MascotSealBadge(
            mascot = mascot,
            sketchiness = sketchiness,
            seed = seed + 4,
            modifier = Modifier.align(Alignment.BottomEnd).padding(ProfileCardFrontDefaults.cornerPadding),
        )
    }
}

/** A camera-viewfinder-style corner mark; [mirrored] opens it to the left instead of the right. */
@Composable
internal fun CornerBracket(mirrored: Boolean, modifier: Modifier = Modifier, color: Color = ProfileCardColors.onDuskBand) {
    Canvas(modifier = modifier.size(ProfileCardFrontDefaults.bracketSize)) {
        val strokeWidth = size.minDimension * 0.14f
        val cornerX = if (mirrored) size.width else 0f
        val armEndX = if (mirrored) 0f else size.width
        drawLine(color, Offset(cornerX, 0f), Offset(armEndX, 0f), strokeWidth, StrokeCap.Round)
        drawLine(color, Offset(cornerX, 0f), Offset(cornerX, size.height), strokeWidth, StrokeCap.Round)
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
            .background(ProfileCardColors.plate)
            .sketchBorder(shape, ProfileCardColors.ink),
        contentAlignment = Alignment.Center,
    ) {
        if (avatarImage != null) {
            AsyncImage(
                model = avatarImage.bytes,
                contentDescription = null,
                modifier = Modifier.size(plateSize).clip(shape),
                contentScale = ContentScale.Crop,
            )
        } else {
            PlaceholderFace(modifier = Modifier.size(plateSize * 0.55f))
        }
    }
}

/**
 * A minimal drawn face standing in for the user's photo, shown until one is picked. Matches the
 * design's own sample face, which is a placeholder rather than an asset to export.
 */
@Composable
private fun PlaceholderFace(modifier: Modifier = Modifier, color: Color = ProfileCardColors.ink) {
    Canvas(modifier = modifier) {
        val eyeRadius = size.minDimension * 0.06f
        val eyeY = size.height * 0.42f
        drawCircle(color, eyeRadius, Offset(size.width * 0.35f, eyeY))
        drawCircle(color, eyeRadius, Offset(size.width * 0.65f, eyeY))
        val smileWidth = size.width * 0.36f
        val smileHeight = smileWidth * 0.6f
        drawArc(
            color = color,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(size.width / 2f - smileWidth / 2f, size.height * 0.55f),
            size = Size(smileWidth, smileHeight),
            style = Stroke(width = size.minDimension * 0.05f, cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun SpeechBubble(text: String, modifier: Modifier = Modifier) {
    val shape = SpeechBubbleShape(borderThickness = 1.6.dp)
    Box(
        modifier = modifier
            .size(ProfileCardFrontDefaults.bubbleWidth, ProfileCardFrontDefaults.bubbleHeight)
            .clip(shape)
            .background(ProfileCardColors.plate)
            .sketchBorder(shape, ProfileCardColors.ink),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = ProfileCardColors.ink, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
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
        MascotIcon(mascot = mascot, modifier = Modifier.size(badgeSize * 0.55f))
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
    val duskBandHeight = 192.dp
    val cornerPadding = 16.dp
    val textPadding = 20.dp
    val bracketSize = 16.dp
    val photoPlateSize = 140.dp
    val plateBorderThickness = 2.dp
    val plateOffsetY = 96.dp
    val bubbleWidth = 57.dp
    val bubbleHeight = 41.dp
    val bubbleOffsetX = 36.dp
    val bubbleOffsetY = 6.dp
    val textBlockOffsetY = 250.dp
    val badgeSize = 80.dp
    val sealBorderThickness = 2.dp
    val sealJitter = 0.05f

    // Traced from the Figma front face: six sparks, alternating -12°/20° rotation, split by
    // which background they sit on so each reads against it.
    val duskBandSparkles = listOf(
        SparklePlacement(x = 282.dp, y = 141.dp, size = 8.dp, rotationDegrees = 20f),
        SparklePlacement(x = 60.dp, y = 89.dp, size = 9.dp, rotationDegrees = 20f),
        SparklePlacement(x = 31.dp, y = 115.dp, size = 13.dp, rotationDegrees = -12f),
    )
    val plateSparkles = listOf(
        SparklePlacement(x = 277.dp, y = 288.dp, size = 9.dp, rotationDegrees = -12f),
        SparklePlacement(x = 273.dp, y = 202.dp, size = 11.dp, rotationDegrees = -12f),
        SparklePlacement(x = 58.dp, y = 249.dp, size = 8.dp, rotationDegrees = 20f),
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
            avatarImage = null,
            modifier = Modifier.padding(24.dp),
        )
    }
}

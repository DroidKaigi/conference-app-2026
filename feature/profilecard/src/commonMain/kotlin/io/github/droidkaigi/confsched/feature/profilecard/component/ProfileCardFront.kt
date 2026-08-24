package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.LocalFileImage
import io.github.droidkaigi.confsched.core.ui.SketchEllipseShape
import io.github.droidkaigi.confsched.core.ui.SketchHorizontalDivider
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.core.ui.sketchHorizontalSplitBackground
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.card_dates
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.card_event_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.card_greeting
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.card_venue
import io.github.vinceglb.filekit.PlatformFile
import org.jetbrains.compose.resources.stringResource

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
    avatarImage: PlatformFile?,
    modifier: Modifier = Modifier,
) {
    val seed = nickName.hashCode()
    ProfileCardFace(sketchiness = sketchiness, seed = seed, modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .sketchHorizontalSplitBackground(
                    seed = seed + 1,
                    topColor = ProfileCardColors.duskBand,
                    bottomColor = ProfileCardColors.plate,
                    splitFraction = ProfileCardFrontDefaults.splitFraction,
                    roughness = profileCardRoughness(ProfileCardFaceDefaults.size.width, sketchiness, filled = true),
                    tremor = profileCardTremor(ProfileCardFaceDefaults.size.width, sketchiness, filled = true),
                ),
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
            color = ProfileCardColors.plate,
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
            SketchSparkle(
                color = ProfileCardColors.plate,
                modifier = Modifier.align(Alignment.TopStart).offset(x = (-24).dp, y = 8.dp),
            )
            SketchSparkle(
                color = ProfileCardColors.ink,
                modifier = Modifier.align(Alignment.BottomStart).offset(x = (-16).dp, y = 0.dp),
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
internal fun CornerBracket(mirrored: Boolean, modifier: Modifier = Modifier, color: Color = ProfileCardColors.plate) {
    Canvas(modifier = modifier.size(ProfileCardFrontDefaults.bracketSize)) {
        val strokeWidth = size.minDimension * 0.14f
        val cornerX = if (mirrored) size.width else 0f
        val armEndX = if (mirrored) 0f else size.width
        drawLine(color, Offset(cornerX, 0f), Offset(armEndX, 0f), strokeWidth, StrokeCap.Round)
        drawLine(color, Offset(cornerX, 0f), Offset(cornerX, size.height), strokeWidth, StrokeCap.Round)
    }
}

@Composable
private fun ProfilePhotoPlate(seed: Int, sketchiness: Sketchiness, avatarImage: PlatformFile?, modifier: Modifier = Modifier) {
    val plateSize = ProfileCardFrontDefaults.photoPlateSize
    val shape = SketchEllipseShape(
        seed = seed,
        roughness = profileCardRoughness(plateSize, sketchiness),
        tremor = profileCardTremor(plateSize, sketchiness),
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
            LocalFileImage(
                file = avatarImage,
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
    val shape = SketchRoundRectShape(
        seed = text.hashCode(),
        cornerRadius = ProfileCardFrontDefaults.bubbleCornerRadius,
        borderThickness = 1.5.dp,
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(ProfileCardColors.plate)
            .sketchBorder(shape, ProfileCardColors.ink)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(text, color = ProfileCardColors.ink, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

/** The chosen mascot, drawn in the app's active `primary` colour (the one themed element on an
 * otherwise fixed-palette card) inside a small wobbly "seal" badge. */
@Composable
private fun MascotSealBadge(mascot: Mascot, sketchiness: Sketchiness, seed: Int, modifier: Modifier = Modifier) {
    val badgeSize = ProfileCardFrontDefaults.badgeSize
    val shape = SketchEllipseShape(
        seed = seed,
        roughness = profileCardRoughness(badgeSize, sketchiness),
        tremor = profileCardTremor(badgeSize, sketchiness),
        borderThickness = 2.dp,
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

private object ProfileCardFrontDefaults {
    val splitFraction = 0.36f
    val cornerPadding = 16.dp
    val textPadding = 20.dp
    val bracketSize = 16.dp
    val photoPlateSize = 140.dp
    val plateBorderThickness = 2.dp
    val plateOffsetY = 96.dp
    val bubbleOffsetX = 28.dp
    val bubbleOffsetY = (-8).dp
    val bubbleCornerRadius = 10.dp
    val textBlockOffsetY = 250.dp
    val badgeSize = 64.dp
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

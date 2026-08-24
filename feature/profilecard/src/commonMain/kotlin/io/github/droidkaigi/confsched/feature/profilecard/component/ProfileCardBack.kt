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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchHorizontalDivider
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.card_event_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.card_scan_me
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

/**
 * The card's back face, a "scene": a QR plate (a placeholder for now — no scanner-code
 * generator is wired up yet) under a banner, and the chosen mascot standing on a hand-drawn
 * ground line beside a small flag.
 */
@Composable
fun ProfileCardBack(
    nickName: String,
    mascot: Mascot,
    sketchiness: Sketchiness,
    modifier: Modifier = Modifier,
) {
    val seed = nickName.hashCode() + 100
    ProfileCardFace(sketchiness = sketchiness, seed = seed, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(ProfileCardBackDefaults.bannerHeight)
                .clip(RoundedCornerShape(topStart = ProfileCardFaceDefaults.cornerRadius, topEnd = ProfileCardFaceDefaults.cornerRadius))
                .background(ProfileCardColors.banner)
                .padding(ProfileCardBackDefaults.textPadding),
        ) {
            Text(
                stringResource(Res.string.card_event_label),
                color = ProfileCardColors.ink,
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                stringResource(Res.string.card_scan_me),
                color = ProfileCardColors.ink,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        CornerBracket(
            mirrored = false,
            color = ProfileCardColors.ink,
            modifier = Modifier.align(Alignment.TopStart).padding(ProfileCardBackDefaults.cornerPadding),
        )
        CornerBracket(
            mirrored = true,
            color = ProfileCardColors.ink,
            modifier = Modifier.align(Alignment.TopEnd).padding(ProfileCardBackDefaults.cornerPadding),
        )
        QrPlate(
            seed = seed + 1,
            sketchiness = sketchiness,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = ProfileCardBackDefaults.qrPlateOffsetY),
        )
        SketchSparkle(
            color = ProfileCardColors.ink,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = ProfileCardBackDefaults.textPadding, y = ProfileCardBackDefaults.qrPlateOffsetY + 8.dp),
        )
        SketchSparkle(
            color = ProfileCardColors.ink,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = -ProfileCardBackDefaults.textPadding, y = ProfileCardBackDefaults.qrPlateOffsetY + 40.dp),
        )
        GroundScene(
            mascot = mascot,
            sketchiness = sketchiness,
            seed = seed + 2,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = ProfileCardBackDefaults.groundOffsetY),
        )
    }
}

@Composable
private fun QrPlate(seed: Int, sketchiness: Sketchiness, modifier: Modifier = Modifier) {
    val plateSize = ProfileCardBackDefaults.qrPlateSize
    val shape = SketchRoundRectShape(
        seed = seed,
        roughness = profileCardRoughness(plateSize, sketchiness),
        tremor = profileCardTremor(plateSize, sketchiness),
        cornerRadius = 12.dp,
        borderThickness = 2.dp,
    )
    Box(
        modifier = modifier
            .size(plateSize)
            .clip(shape)
            .background(ProfileCardColors.plate)
            .sketchBorder(shape, ProfileCardColors.ink)
            .padding(ProfileCardBackDefaults.qrPlateInset),
    ) {
        QrPattern(seed = seed, modifier = Modifier.fillMaxWidth())
    }
}

/** A grid of dark squares that reads as a QR code at a glance, not a real scannable one. */
@Composable
private fun QrPattern(seed: Int, modifier: Modifier = Modifier) {
    val ink = ProfileCardColors.ink
    Canvas(modifier = modifier.size(ProfileCardBackDefaults.qrPlateSize - ProfileCardBackDefaults.qrPlateInset * 2)) {
        val random = Random(seed)
        val cellCount = ProfileCardBackDefaults.qrCellCount
        val cell = size.width / cellCount
        for (row in 0 until cellCount) {
            for (column in 0 until cellCount) {
                val inFinderCorner = (row < 3 || row > cellCount - 4) && (column < 3 || column > cellCount - 4)
                val filled = inFinderCorner || random.nextFloat() < 0.45f
                if (filled) {
                    drawRect(
                        color = ink,
                        topLeft = Offset(column * cell, row * cell),
                        size = androidx.compose.ui.geometry.Size(cell * 0.9f, cell * 0.9f),
                    )
                }
            }
        }
    }
}

@Composable
private fun GroundScene(mascot: Mascot, sketchiness: Sketchiness, seed: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().height(ProfileCardBackDefaults.groundSceneHeight)) {
        MascotIcon(
            mascot = mascot,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = ProfileCardBackDefaults.textPadding)
                .size(ProfileCardBackDefaults.mascotSize)
                .offset(y = ProfileCardBackDefaults.groundLineInset),
        )
        FlagMark(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(
                    x = ProfileCardBackDefaults.textPadding + ProfileCardBackDefaults.mascotSize + 12.dp,
                    y = -ProfileCardBackDefaults.flagHeight + ProfileCardBackDefaults.groundLineInset,
                ),
        )
        SketchHorizontalDivider(
            seed = seed,
            color = ProfileCardColors.ink,
            thickness = 1.5.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = ProfileCardBackDefaults.textPadding)
                .offset(y = ProfileCardBackDefaults.groundLineInset),
        )
        GrassTicks(
            seed = seed + 1,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = ProfileCardBackDefaults.textPadding)
                .fillMaxWidth()
                .offset(y = ProfileCardBackDefaults.groundLineInset),
        )
    }
}

@Composable
private fun GrassTicks(seed: Int, modifier: Modifier = Modifier) {
    val ink = ProfileCardColors.ink
    Canvas(modifier = modifier.height(ProfileCardBackDefaults.grassTickHeight)) {
        val random = Random(seed)
        val strokeWidth = size.height * 0.18f
        var x = 0f
        while (x < size.width) {
            val tickHeight = size.height * (0.5f + random.nextFloat() * 0.5f)
            drawLine(
                color = ink,
                start = Offset(x, size.height),
                end = Offset(x, size.height - tickHeight),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
            x += size.height * 1.8f + random.nextFloat() * size.height
        }
    }
}

@Composable
private fun FlagMark(modifier: Modifier = Modifier) {
    val ink = ProfileCardColors.ink
    Canvas(modifier = modifier.size(width = ProfileCardBackDefaults.flagWidth, height = ProfileCardBackDefaults.flagHeight)) {
        val strokeWidth = size.width * 0.12f
        drawLine(
            color = ink,
            start = Offset(strokeWidth / 2f, 0f),
            end = Offset(strokeWidth / 2f, size.height),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        val flagPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(strokeWidth, 0f)
            lineTo(size.width, size.height * 0.2f)
            lineTo(strokeWidth, size.height * 0.4f)
            close()
        }
        drawPath(flagPath, color = ink)
    }
}

private object ProfileCardBackDefaults {
    val bannerHeight = 116.dp
    val cornerPadding = 16.dp
    val textPadding = 20.dp
    val qrPlateSize = 160.dp
    val qrPlateInset = 12.dp
    val qrPlateOffsetY = 140.dp
    val qrCellCount = 12
    val groundOffsetY = 360.dp
    val groundSceneHeight = 96.dp
    val groundLineInset = (-16).dp
    val mascotSize = 64.dp
    val flagWidth = 20.dp
    val flagHeight = 40.dp
    val grassTickHeight = 8.dp
}

@LocalePreviews
@Composable
private fun ProfileCardBackPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardBack(
            nickName = "droidkaigi",
            mascot = Mascot.Koala,
            sketchiness = Sketchiness.Normal,
            modifier = Modifier.padding(24.dp),
        )
    }
}

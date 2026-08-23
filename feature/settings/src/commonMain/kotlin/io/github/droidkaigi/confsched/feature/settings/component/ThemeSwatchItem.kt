package io.github.droidkaigi.confsched.feature.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.toMaterialColorScheme
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchDefaults
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.scaleSketchAmplitude
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun ThemeSwatchItem(
    colorScheme: KaigiColorScheme,
    label: String,
    selected: Boolean,
    seed: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ThemeSwatchLayout(
        label = label,
        selected = selected,
        seed = seed,
        onClick = onClick,
        modifier = modifier,
    ) {
        val palette = colorScheme.toMaterialColorScheme()
        val dotOffset = colorScheme.dotOffset
        Box(
            modifier = Modifier
                .size(ThemeSwatchDefaults.blobSize)
                .clip(
                    SketchRoundRectShape(
                        seed = combineSketchSeed(seed),
                        roughness = ThemeSwatchDefaults.blobRoughness,
                        tremor = ThemeSwatchDefaults.blobTremor,
                        cornerRadius = ThemeSwatchDefaults.blobSize / 2,
                    ),
                )
                .background(palette.primary),
        ) {
            Box(
                modifier = Modifier
                    .offset(x = dotOffset.x, y = dotOffset.y)
                    .size(ThemeSwatchDefaults.dotSize)
                    .clip(
                        SketchRoundRectShape(
                            seed = combineSketchSeed(seed + ThemeSwatchDefaults.DOT_SEED_OFFSET),
                            roughness = ThemeSwatchDefaults.dotRoughness,
                            tremor = ThemeSwatchDefaults.blobTremor,
                            cornerRadius = ThemeSwatchDefaults.dotSize / 2,
                        ),
                    )
                    .background(palette.onPrimary),
            )
        }
    }
}

/** The option that leaves the scheme to the draw the app makes at every launch. */
@Composable
internal fun RandomThemeSwatchItem(
    label: String,
    selected: Boolean,
    seed: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ThemeSwatchLayout(
        label = label,
        selected = selected,
        seed = seed,
        onClick = onClick,
        modifier = modifier,
    ) {
        val plateShape = SketchRoundRectShape(
            seed = combineSketchSeed(seed),
            roughness = ThemeSwatchDefaults.blobRoughness,
            tremor = ThemeSwatchDefaults.blobTremor,
            cornerRadius = ThemeSwatchDefaults.blobSize / 2,
            borderThickness = ThemeSwatchDefaults.plateBorderThickness,
        )
        Box(
            modifier = Modifier
                .size(ThemeSwatchDefaults.blobSize)
                .clip(plateShape)
                .background(MaterialTheme.colorScheme.surfaceBright)
                .sketchBorder(shape = plateShape, color = MaterialTheme.colorScheme.outline),
            contentAlignment = Alignment.Center,
        ) {
            KaigiColorScheme.entries.forEachIndexed { index, scheme ->
                val angle = ThemeSwatchDefaults.firstShardAngle +
                    index * (ThemeSwatchDefaults.fullTurn / KaigiColorScheme.entries.size)
                Box(
                    modifier = Modifier
                        .offset(
                            x = ThemeSwatchDefaults.shardRadius * cos(angle),
                            y = ThemeSwatchDefaults.shardRadius * sin(angle),
                        )
                        .size(ThemeSwatchDefaults.shardSize)
                        .clip(
                            SketchRoundRectShape(
                                seed = combineSketchSeed(seed + ThemeSwatchDefaults.SHARD_SEED_OFFSET + index),
                                roughness = ThemeSwatchDefaults.dotRoughness,
                                tremor = ThemeSwatchDefaults.blobTremor,
                                cornerRadius = ThemeSwatchDefaults.shardSize / 2,
                            ),
                        )
                        .background(scheme.toMaterialColorScheme().primary),
                )
            }
        }
    }
}

@Composable
private fun ThemeSwatchLayout(
    label: String,
    selected: Boolean,
    seed: Int,
    onClick: () -> Unit,
    modifier: Modifier,
    dab: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .width(ThemeSwatchDefaults.width)
            // A long scheme name takes a third line; the card grows rather than clipping it.
            .heightIn(min = ThemeSwatchDefaults.minHeight)
            .clip(
                SketchRoundRectShape(
                    seed = combineSketchSeed(seed + ThemeSwatchDefaults.FRAME_SEED_OFFSET),
                    roughness = SketchDefaults.roughness,
                    tremor = SketchDefaults.tremor,
                    cornerRadius = ThemeSwatchDefaults.cornerRadius,
                ),
            )
            .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp).padding(top = 20.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(
                    width = ThemeSwatchDefaults.dabWidth,
                    height = ThemeSwatchDefaults.dabHeight,
                ),
                contentAlignment = Alignment.Center,
            ) {
                dab()
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center,
            )
        }
        if (selected) {
            SelectionCheckIcon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 6.dp)
                    .size(18.dp),
            )
        }
    }
}

private object ThemeSwatchDefaults {
    // The swatch artwork is drawn by the app, so its wobble follows the strength in force.
    val blobRoughness: Dp @Composable get() = scaleSketchAmplitude(1.6.dp)
    val blobTremor: Dp @Composable get() = scaleSketchAmplitude(0.4.dp)
    val dotRoughness: Dp @Composable get() = scaleSketchAmplitude(0.5.dp)

    val width = 72.dp
    val minHeight = 120.dp
    val cornerRadius = 12.dp
    val dabWidth = 60.dp
    val dabHeight = 48.dp
    val blobSize = 44.dp
    val dotSize = 16.dp
    val plateBorderThickness = 2.dp
    val shardSize = 13.dp
    val shardRadius = 12.dp
    const val FRAME_SEED_OFFSET = 100
    const val DOT_SEED_OFFSET = 50
    const val SHARD_SEED_OFFSET = 200

    /** Radians, from the top of the plate, so the shards read as a ring rather than a row. */
    val firstShardAngle = (-PI / 2).toFloat()
    val fullTurn = (2 * PI).toFloat()
}

/** Where the highlight sits on the blob, staggered so no two swatches read as the same dab. */
private val KaigiColorScheme.dotOffset: DpOffset
    get() = when (this) {
        KaigiColorScheme.MorningMist -> DpOffset(x = 20.dp, y = 22.dp)
        KaigiColorScheme.DeepTeal -> DpOffset(x = 22.dp, y = 14.dp)
        KaigiColorScheme.SakuraPlum -> DpOffset(x = 18.dp, y = 10.dp)
        KaigiColorScheme.Terracotta -> DpOffset(x = 21.dp, y = 20.dp)
        KaigiColorScheme.CampfireNight -> DpOffset(x = 22.dp, y = 6.dp)
    }

@Preview
@Composable
private fun ThemeSwatchItemPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RandomThemeSwatchItem(
                label = "Random\nOn launch",
                selected = true,
                seed = 1,
                onClick = {},
            )
            ThemeSwatchItem(
                colorScheme = colorScheme,
                label = colorScheme.name,
                selected = false,
                seed = 2,
                onClick = {},
            )
        }
    }
}

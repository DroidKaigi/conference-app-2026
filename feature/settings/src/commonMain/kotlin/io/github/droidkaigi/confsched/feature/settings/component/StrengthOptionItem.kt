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
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.SketchStrength
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchDefaults
import io.github.droidkaigi.confsched.core.ui.SketchHorizontalDivider
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.sketchBorder

@Composable
internal fun StrengthOptionItem(
    strength: SketchStrength,
    label: String,
    selected: Boolean,
    seed: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(StrengthOptionDefaults.width)
            .heightIn(min = StrengthOptionDefaults.minHeight)
            .clip(
                SketchRoundRectShape(
                    seed = combineSketchSeed(seed),
                    roughness = SketchDefaults.roughness,
                    tremor = SketchDefaults.tremor,
                    cornerRadius = StrengthOptionDefaults.cornerRadius,
                ),
            )
            .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp).padding(top = 20.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StrengthPreviewView(strength = strength, seed = seed)
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
                    .padding(top = 2.dp)
                    .size(16.dp),
            )
        }
    }
}

/** The wobble the option selects, drawn at that wobble: a sample frame with a rule across it. */
@Composable
private fun StrengthPreviewView(strength: SketchStrength, seed: Int) {
    val roughness = StrengthOptionDefaults.previewRoughness * strength.amplitudeScale
    val tremor = StrengthOptionDefaults.previewTremor * strength.amplitudeScale
    val shape = SketchRoundRectShape(
        seed = combineSketchSeed(seed),
        roughness = roughness,
        tremor = tremor,
        sweepWavelength = StrengthOptionDefaults.previewSweepWavelength,
        tremorWavelength = StrengthOptionDefaults.previewTremorWavelength,
        cornerRadius = StrengthOptionDefaults.previewCornerRadius,
        borderThickness = StrengthOptionDefaults.previewStroke,
    )
    val dividerRoughness = roughness * StrengthOptionDefaults.PREVIEW_DIVIDER_ROUGHNESS_RATIO
    val dividerHeight = StrengthOptionDefaults.previewStroke + dividerRoughness * 2
    Box(
        modifier = Modifier
            .size(
                width = StrengthOptionDefaults.previewWidth,
                height = StrengthOptionDefaults.previewHeight,
            )
            .sketchBorder(shape = shape, color = MaterialTheme.colorScheme.outline),
    ) {
        SketchHorizontalDivider(
            seed = seed + StrengthOptionDefaults.PREVIEW_DIVIDER_SEED_OFFSET,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = StrengthOptionDefaults.previewHeight * 0.7f - dividerHeight / 2)
                .width(StrengthOptionDefaults.previewDividerWidth),
            color = MaterialTheme.colorScheme.outline,
            thickness = StrengthOptionDefaults.previewStroke,
            roughness = dividerRoughness,
            tremor = 0.dp,
            sweepWavelength = StrengthOptionDefaults.previewDividerSweepWavelength,
        )
    }
}

// The sample frame follows the design's figures: the amplitudes below are the ones it states
// for the middle level, which the strength's own scale then moves off.
private object StrengthOptionDefaults {
    val width = 100.dp
    val minHeight = 92.dp
    val cornerRadius = 12.dp

    val previewWidth = 72.dp
    val previewHeight = 32.dp
    val previewCornerRadius = 10.dp
    val previewStroke = 1.5.dp
    val previewRoughness = 0.95.dp
    val previewTremor = 0.24.dp
    val previewSweepWavelength = 48.dp
    val previewTremorWavelength = 18.dp

    val previewDividerWidth = 56.dp
    val previewDividerSweepWavelength = 30.dp
    const val PREVIEW_DIVIDER_ROUGHNESS_RATIO = 0.5f
    const val PREVIEW_DIVIDER_SEED_OFFSET = 10
}

@Preview
@Composable
private fun StrengthOptionItemPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SketchStrength.entries.forEachIndexed { index, strength ->
                StrengthOptionItem(
                    strength = strength,
                    label = strength.name,
                    selected = strength == SketchStrength.Normal,
                    seed = index,
                    onClick = {},
                )
            }
        }
    }
}

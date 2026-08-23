package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiSegmentedButton
import io.github.droidkaigi.confsched.core.ui.KaigiSingleChoiceSegmentedButtonRow
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.sketchiness_normal
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.sketchiness_playful
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.sketchiness_subtle
import org.jetbrains.compose.resources.stringResource

/**
 * How far the card's hand-sketched outlines wobble from a straight line, expressed as the
 * multiplier applied to the amplitude the card's own size derives.
 */
enum class Sketchiness(val amplitudeMultiplier: Float) {
    Subtle(0.5f),
    Normal(1.6f),
    Playful(3.4f),
}

private val Sketchiness.label: String
    @Composable get() = when (this) {
        Sketchiness.Subtle -> stringResource(Res.string.sketchiness_subtle)
        Sketchiness.Normal -> stringResource(Res.string.sketchiness_normal)
        Sketchiness.Playful -> stringResource(Res.string.sketchiness_playful)
    }

/**
 * A three-way segmented picker for the card's [Sketchiness]. Each option previews its own
 * wobble on a small swatch above the label, rather than naming the level in text alone.
 */
@Composable
fun SketchinessPicker(
    selected: Sketchiness,
    onSketchinessSelected: (Sketchiness) -> Unit,
    modifier: Modifier = Modifier,
) {
    val entries = Sketchiness.entries
    KaigiSingleChoiceSegmentedButtonRow(outlineSeed = SketchinessPickerDefaults.outlineSeed, modifier = modifier) {
        entries.forEachIndexed { index, sketchiness ->
            KaigiSegmentedButton(
                selected = sketchiness == selected,
                onClick = { onSketchinessSelected(sketchiness) },
                dividerSeed = if (index < entries.lastIndex) SketchinessPickerDefaults.outlineSeed + index + 1 else null,
                leadingDividerSeed = if (index > 0) SketchinessPickerDefaults.outlineSeed + index else null,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(SketchinessPickerDefaults.swatchSpacing),
                ) {
                    SketchinessSwatch(sketchiness = sketchiness, seed = SketchinessPickerDefaults.outlineSeed + 30 + index)
                    Text(sketchiness.label, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

/**
 * A small pill outline wobbling by [sketchiness]'s own [Sketchiness.amplitudeMultiplier], so the
 * option previews what picking it draws like rather than just naming it.
 */
@Composable
private fun SketchinessSwatch(
    sketchiness: Sketchiness,
    seed: Int,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
) {
    val shape = SketchRoundRectShape(
        seed = seed,
        roughness = SketchinessSwatchDefaults.baseRoughness * sketchiness.amplitudeMultiplier,
        tremor = SketchinessSwatchDefaults.baseTremor * sketchiness.amplitudeMultiplier,
        cornerRadius = SketchinessSwatchDefaults.height / 2,
        borderThickness = SketchinessSwatchDefaults.borderThickness,
    )
    Box(
        modifier = modifier
            .size(width = SketchinessSwatchDefaults.width, height = SketchinessSwatchDefaults.height)
            .sketchBorder(shape, color),
    )
}

object SketchinessPickerDefaults {
    val outlineSeed = 700
    val swatchSpacing = 2.dp
}

private object SketchinessSwatchDefaults {
    val width = 24.dp
    val height = 10.dp
    val baseRoughness = 0.3.dp
    val baseTremor = 0.15.dp
    val borderThickness = 1.dp
}

@LocalePreviews
@Composable
private fun SketchinessPickerPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SketchinessPicker(selected = Sketchiness.Normal, onSketchinessSelected = {})
    }
}

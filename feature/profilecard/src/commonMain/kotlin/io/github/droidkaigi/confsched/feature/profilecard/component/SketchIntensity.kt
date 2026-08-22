package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiSegmentedButton
import io.github.droidkaigi.confsched.core.ui.KaigiSingleChoiceSegmentedButtonRow

/**
 * How far the card's hand-sketched outlines wobble from a straight line, expressed as the
 * multiplier applied to the amplitude the card's own size derives.
 */
enum class SketchIntensity(val amplitudeMultiplier: Float) {
    Subtle(0.5f),
    Normal(1.6f),
    Playful(3.4f),
}

private val SketchIntensity.label: String
    get() = when (this) {
        SketchIntensity.Subtle -> "Subtle"
        SketchIntensity.Normal -> "Normal"
        SketchIntensity.Playful -> "Playful"
    }

/** A three-way segmented picker for the card's [SketchIntensity]. */
@Composable
fun SketchIntensityPicker(
    selected: SketchIntensity,
    onSketchIntensitySelected: (SketchIntensity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val entries = SketchIntensity.entries
    KaigiSingleChoiceSegmentedButtonRow(outlineSeed = SketchIntensityPickerDefaults.outlineSeed, modifier = modifier) {
        entries.forEachIndexed { index, sketchIntensity ->
            KaigiSegmentedButton(
                selected = sketchIntensity == selected,
                onClick = { onSketchIntensitySelected(sketchIntensity) },
                dividerSeed = if (index < entries.lastIndex) SketchIntensityPickerDefaults.outlineSeed + index + 1 else null,
                leadingDividerSeed = if (index > 0) SketchIntensityPickerDefaults.outlineSeed + index else null,
            ) {
                Text(sketchIntensity.label, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

object SketchIntensityPickerDefaults {
    val outlineSeed = 700
}

@Preview
@Composable
private fun SketchIntensityPickerPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SketchIntensityPicker(selected = SketchIntensity.Normal, onSketchIntensitySelected = {})
    }
}

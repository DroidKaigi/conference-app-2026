package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
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
 * A row of independent [Sketchiness] chips, each its own rounded-rect outline with a gap to its
 * neighbours, rather than segments sharing one continuous pill border.
 */
@Composable
fun SketchinessPicker(
    selected: Sketchiness,
    onSketchinessSelected: (Sketchiness) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(SketchinessPickerDefaults.gap),
    ) {
        Sketchiness.entries.forEach { sketchiness ->
            SketchinessOption(
                sketchiness = sketchiness,
                selected = sketchiness == selected,
                onClick = { onSketchinessSelected(sketchiness) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SketchinessOption(
    sketchiness: Sketchiness,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val shape = SketchRoundRectShape(
        seed = SketchinessPickerDefaults.outlineSeed + sketchiness.ordinal,
        roughness = SketchinessOptionDefaults.roughness,
        tremor = SketchinessOptionDefaults.tremor,
        cornerRadius = SketchinessOptionDefaults.cornerRadius,
        borderThickness = if (selected) {
            SketchinessOptionDefaults.selectedBorderThickness
        } else {
            SketchinessOptionDefaults.borderThickness
        },
    )
    Box(
        modifier = modifier
            .height(SketchinessOptionDefaults.height)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .clip(shape)
            .background(containerColor)
            .sketchBorder(shape, borderColor),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SketchinessPickerDefaults.gap),
            ) {
                SketchinessSwatch(
                    sketchiness = sketchiness,
                    seed = SketchinessPickerDefaults.outlineSeed + SketchinessSwatchDefaults.seedOffset + sketchiness.ordinal,
                )
                Text(sketchiness.label, style = MaterialTheme.typography.labelLarge)
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
    val gap = 4.dp
}

private object SketchinessOptionDefaults {
    val height = 60.dp
    val cornerRadius = 12.dp
    val borderThickness = 1.2.dp
    val selectedBorderThickness = 2.5.dp
    val roughness = 0.4.dp
    val tremor = 0.15.dp
}

private object SketchinessSwatchDefaults {
    val width = 24.dp
    val height = 10.dp
    val baseRoughness = 0.3.dp
    val baseTremor = 0.15.dp
    val borderThickness = 1.dp
    val seedOffset = 40
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

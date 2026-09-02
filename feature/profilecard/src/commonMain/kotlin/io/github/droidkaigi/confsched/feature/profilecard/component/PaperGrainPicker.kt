package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.PaperGrain
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.paperGrain
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardSweepWavelength
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.paper_grain_grained
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.paper_grain_rough
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.paper_grain_smooth
import org.jetbrains.compose.resources.stringResource

private val PaperGrain.label: String
    @Composable get() = when (this) {
        PaperGrain.Smooth -> stringResource(Res.string.paper_grain_smooth)
        PaperGrain.Grained -> stringResource(Res.string.paper_grain_grained)
        PaperGrain.Rough -> stringResource(Res.string.paper_grain_rough)
    }

internal fun paperGrainOptionTestTag(paperGrain: PaperGrain) = "PaperGrainOption:${paperGrain.name}"

/** A row of [PaperGrain] chips, laid out the way [SketchinessPicker] lays out its options. */
@Composable
fun PaperGrainPicker(
    selectedPaperGrain: PaperGrain,
    onPaperGrainClick: (PaperGrain) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(SketchinessPickerDefaults.optionGap),
    ) {
        PaperGrain.entries.forEach { paperGrain ->
            PaperGrainOption(
                paperGrain = paperGrain,
                selected = paperGrain == selectedPaperGrain,
                onClick = { onPaperGrainClick(paperGrain) },
                modifier = Modifier
                    .weight(1f)
                    .testTag(paperGrainOptionTestTag(paperGrain)),
            )
        }
    }
}

@Composable
private fun PaperGrainOption(
    paperGrain: PaperGrain,
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
        seed = PaperGrainOptionDefaults.outlineSeed + paperGrain.ordinal,
        roughness = PaperGrainOptionDefaults.roughness,
        tremor = PaperGrainOptionDefaults.tremor,
        sweepWavelength = ProfileCardSweepWavelength,
        cornerRadius = PaperGrainOptionDefaults.cornerRadius,
        borderThickness = if (selected) {
            PaperGrainOptionDefaults.selectedBorderThickness
        } else {
            PaperGrainOptionDefaults.borderThickness
        },
    )
    Box(
        modifier = modifier
            .height(PaperGrainOptionDefaults.height)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .clip(shape)
            .background(containerColor)
            .sketchBorder(shape, borderColor),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SketchinessPickerDefaults.contentGap),
            ) {
                PaperGrainSwatch(paperGrain = paperGrain)
                Text(paperGrain.label, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

/** A patch of the paper itself: the same grain the card face lays over its plate, boosted so the swatch-sized sample stays readable. */
@Composable
private fun PaperGrainSwatch(
    paperGrain: PaperGrain,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(width = PaperGrainSwatchDefaults.width, height = PaperGrainSwatchDefaults.height)
            .clip(RoundedCornerShape(PaperGrainSwatchDefaults.cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceBright)
            .paperGrain(alpha = paperGrain.grainAlpha * PaperGrainSwatchDefaults.alphaBoost)
            .border(
                width = PaperGrainSwatchDefaults.borderWidth,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(PaperGrainSwatchDefaults.cornerRadius),
            ),
    )
}

private object PaperGrainOptionDefaults {
    val outlineSeed = 740
    val height = 60.dp
    val cornerRadius = 12.dp
    val borderThickness = 1.2.dp
    val selectedBorderThickness = 2.5.dp
    val roughness = 0.4.dp
    val tremor = 0.15.dp
}

private object PaperGrainSwatchDefaults {
    val width = 56.dp
    val height = 26.dp
    val cornerRadius = 6.dp
    val borderWidth = 1.dp
    val alphaBoost = 2f
}

@LocalePreviews
@Composable
private fun PaperGrainPickerPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PaperGrainPicker(selectedPaperGrain = PaperGrain.Grained, onPaperGrainClick = {})
    }
}

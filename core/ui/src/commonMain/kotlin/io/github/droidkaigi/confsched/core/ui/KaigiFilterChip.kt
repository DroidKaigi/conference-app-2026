package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

/**
 * One option of a filter, drawn as its own hand-sketched pill rather than sharing an outline
 * with its neighbours.
 *
 * The one in effect fills and takes a leading tick, so a row of them reads as a filter even
 * where the pills sit over content rather than on the header band. It is drawn at the height
 * the design gives it and takes its clicks over a taller area, so a row stays reachable
 * without the pills growing apart.
 *
 * @param selected whether this option is the one in effect.
 * @param onClick called when the option is clicked.
 * @param label the text naming the option.
 * @param seed the value the pill is drawn from. The same seed always produces the same pill,
 *   so give neighbouring chips different ones or a row of them reads as a repeat.
 * @param modifier the [Modifier] applied to the chip.
 * @param selectedContainerColor the colour filling the pill while [selected].
 * @param selectedContentColor the colour the tick, [label] and the outline take while [selected].
 * @param contentColor the colour [label] takes while not [selected].
 * @param borderColor the colour of the outline while not [selected].
 */
@Composable
fun KaigiFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    seed: Int,
    modifier: Modifier = Modifier,
    selectedContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    selectedContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    borderColor: Color = MaterialTheme.colorScheme.outline,
) {
    val combinedSeed = combineSketchSeed(seed)
    val shape = SketchEllipseShape(
        seed = combinedSeed,
        roughness = KaigiFilterChipDefaults.roughness,
        tremor = KaigiFilterChipDefaults.tremor,
        borderThickness = KaigiFilterChipDefaults.borderThickness,
    )
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .background(
                color = if (selected) selectedContainerColor else Color.Transparent,
                shape = shape,
            )
            .sketchBorder(shape, if (selected) selectedContentColor else borderColor)
            .clip(shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .height(KaigiFilterChipDefaults.height)
                .padding(horizontal = KaigiFilterChipDefaults.horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(KaigiFilterChipDefaults.iconSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = selectedContentColor,
                    modifier = Modifier.size(KaigiFilterChipDefaults.iconSize),
                )
            }
            Text(
                text = label,
                // The option in effect is set a weight heavier, so a row of them reads
                // even where the fill alone is hard to tell apart.
                style = if (selected) {
                    KaigiFilterChipDefaults.labelStyle.copy(fontWeight = FontWeight.SemiBold)
                } else {
                    KaigiFilterChipDefaults.labelStyle
                },
                color = if (selected) selectedContentColor else contentColor,
            )
        }
    }
}

object KaigiFilterChipDefaults {
    val height = 36.dp

    val horizontalPadding = 16.dp
    val iconSpacing = 4.dp
    val iconSize = 18.dp
    val borderThickness = 1.5.dp
    val roughness = 0.4.dp
    val tremor = 0.15.dp

    val labelStyle
        @Composable get() = MaterialTheme.typography.labelLarge
}

@Preview
@Composable
private fun KaigiFilterChipPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            KaigiFilterChip(selected = true, onClick = {}, label = "1F", seed = 811)
            KaigiFilterChip(selected = false, onClick = {}, label = "B1F", seed = 812)
        }
    }
}

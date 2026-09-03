package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardSweepWavelength
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.sketchiness_normal
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.sketchiness_playful
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.sketchiness_subtle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val Sketchiness.label: String
    @Composable get() = when (this) {
        Sketchiness.Subtle -> stringResource(Res.string.sketchiness_subtle)
        Sketchiness.Normal -> stringResource(Res.string.sketchiness_normal)
        Sketchiness.Playful -> stringResource(Res.string.sketchiness_playful)
    }

private val Sketchiness.drawableResource: DrawableResource
    get() = when (this) {
        Sketchiness.Subtle -> Res.drawable.sketchiness_subtle
        Sketchiness.Normal -> Res.drawable.sketchiness_normal
        Sketchiness.Playful -> Res.drawable.sketchiness_playful
    }

internal fun sketchinessOptionTestTag(sketchiness: Sketchiness) = "SketchinessOption:${sketchiness.name}"

/**
 * A row of independent [Sketchiness] chips, each its own rounded-rect outline with a gap to its
 * neighbours, rather than segments sharing one continuous pill border.
 */
@Composable
fun SketchinessPicker(
    selectedSketchiness: Sketchiness,
    onSketchinessClick: (Sketchiness) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(SketchinessPickerDefaults.optionGap),
    ) {
        Sketchiness.entries.forEach { sketchiness ->
            SketchinessOption(
                sketchiness = sketchiness,
                selected = sketchiness == selectedSketchiness,
                onClick = { onSketchinessClick(sketchiness) },
                modifier = Modifier
                    .weight(1f)
                    .testTag(sketchinessOptionTestTag(sketchiness)),
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
        sweepWavelength = ProfileCardSweepWavelength,
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
                verticalArrangement = Arrangement.spacedBy(SketchinessPickerDefaults.contentGap),
            ) {
                SketchinessSwatch(sketchiness = sketchiness)
                Text(sketchiness.label, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

/**
 * Line art of [sketchiness]'s own wobble, traced from the Figma source and shipped as SVG. The
 * artwork is a single flat stroke color in the source file, so [color] retints it via
 * [ColorFilter.tint] rather than each option needing per-theme variants of the file itself.
 */
@Composable
private fun SketchinessSwatch(
    sketchiness: Sketchiness,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
) {
    Image(
        painter = painterResource(sketchiness.drawableResource),
        contentDescription = null,
        modifier = modifier.size(width = SketchinessSwatchDefaults.width, height = SketchinessSwatchDefaults.height),
        colorFilter = ColorFilter.tint(color),
    )
}

object SketchinessPickerDefaults {
    val outlineSeed = 700
    val contentGap = 4.dp
    val optionGap = 12.dp
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
    val width = 56.dp
    val height = 26.dp
}

@LocalePreviews
@Composable
private fun SketchinessPickerPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SketchinessPicker(selectedSketchiness = Sketchiness.Normal, onSketchinessClick = {})
    }
}

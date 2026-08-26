package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchEllipseShape
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.hall
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.jellyfish
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.koala
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.ladybug
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.meerkat
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val Mascot.drawableResource: DrawableResource
    get() = when (this) {
        Mascot.Hall -> Res.drawable.hall
        Mascot.Jellyfish -> Res.drawable.jellyfish
        Mascot.Koala -> Res.drawable.koala
        Mascot.Ladybug -> Res.drawable.ladybug
        Mascot.Meerkat -> Res.drawable.meerkat
    }

/**
 * Line art for a [Mascot], traced from the Figma source and shipped as SVG. The artwork is a
 * single flat stroke color in the source file, so [color] retints it via [ColorFilter.tint]
 * rather than the mascot needing per-theme variants of the file itself.
 */
@Composable
fun MascotIcon(
    mascot: Mascot,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Image(
        painter = painterResource(mascot.drawableResource),
        contentDescription = null,
        modifier = modifier,
        colorFilter = ColorFilter.tint(color),
    )
}

/** A row letting the user pick one of the five [Mascot]s. */
@Composable
fun MascotPicker(
    selected: Mascot,
    onMascotSelected: (Mascot) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Mascot.entries.forEach { mascot ->
            MascotOption(
                mascot = mascot,
                selected = mascot == selected,
                onClick = { onMascotSelected(mascot) },
                seed = mascot.ordinal,
            )
        }
    }
}

/**
 * One option of [MascotPicker], drawn as its own hand-sketched circle rather than sharing an
 * outline with its neighbours, matching [io.github.droidkaigi.confsched.core.ui.KaigiFilterChip].
 *
 * @param seed the value the circle is drawn from. The same seed always produces the same
 *   outline, so give neighbouring options different ones or a row of them reads as a repeat.
 */
@Composable
private fun MascotOption(
    mascot: Mascot,
    selected: Boolean,
    onClick: () -> Unit,
    seed: Int,
    modifier: Modifier = Modifier,
    selectedContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    selectedContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    borderColor: Color = MaterialTheme.colorScheme.outline,
) {
    val shape = SketchEllipseShape(
        seed = seed,
        roughness = MascotOptionDefaults.roughness,
        tremor = MascotOptionDefaults.tremor,
        sweepWavelength = ProfileCardSweepWavelength,
        borderThickness = MascotOptionDefaults.borderThickness,
    )
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .size(MascotOptionDefaults.size)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .clip(shape)
            .background(if (selected) selectedContainerColor else Color.Transparent)
            .sketchBorder(shape, if (selected) selectedContentColor else borderColor),
        contentAlignment = Alignment.Center,
    ) {
        MascotIcon(
            mascot = mascot,
            color = if (selected) selectedContentColor else contentColor,
            modifier = Modifier.size(MascotOptionDefaults.iconSize),
        )
    }
}

object MascotOptionDefaults {
    val size = 48.dp
    val iconSize = 28.dp
    val borderThickness = 1.5.dp
    val roughness = 0.4.dp
    val tremor = 0.15.dp
}

@Preview
@Composable
private fun MascotIconPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Row(modifier = Modifier.padding(16.dp)) {
            Mascot.entries.forEach { mascot ->
                MascotIcon(mascot = mascot, modifier = Modifier.padding(4.dp).size(48.dp))
            }
        }
    }
}

@Preview
@Composable
private fun MascotPickerPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        MascotPicker(selected = Mascot.Koala, onMascotSelected = {}, modifier = Modifier.padding(16.dp))
    }
}

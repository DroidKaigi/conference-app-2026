package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchEllipseShape
import io.github.droidkaigi.confsched.core.ui.profilecard.MascotIcon
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardSweepWavelength
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.mascot_a
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.mascot_b
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.mascot_c
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.mascot_d
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.mascot_e
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.mascot_f
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal fun mascotOptionTestTag(mascot: Mascot) = "MascotOption:${mascot.name}"

/** A row letting the user pick one of the five [Mascot]s. */
@Composable
fun MascotPicker(
    selectedMascot: Mascot,
    onMascotClick: (Mascot) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(MascotOptionDefaults.gap),
    ) {
        Mascot.entries.forEach { mascot ->
            MascotOption(
                mascot = mascot,
                selected = mascot == selectedMascot,
                onClick = { onMascotClick(mascot) },
                seed = mascot.ordinal,
                modifier = Modifier.testTag(mascotOptionTestTag(mascot)),
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
    val name = stringResource(mascot.nameResource)
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .size(MascotOptionDefaults.size)
            .semantics { contentDescription = name }
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

    /** The design lays the row out as 56dp cells; the ring inside one is [size]. */
    val gap = 16.dp
    val iconSize = 30.dp
    val borderThickness = 1.5.dp
    val roughness = 0.4.dp
    val tremor = 0.15.dp
}

@LocalePreviews
@Composable
private fun MascotPickerPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        MascotPicker(selectedMascot = Mascot.C, onMascotClick = {}, modifier = Modifier.padding(16.dp))
    }
}

private val Mascot.nameResource: StringResource
    get() = when (this) {
        Mascot.A -> Res.string.mascot_a
        Mascot.B -> Res.string.mascot_b
        Mascot.C -> Res.string.mascot_c
        Mascot.D -> Res.string.mascot_d
        Mascot.E -> Res.string.mascot_e
        Mascot.F -> Res.string.mascot_f
    }

package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.DoodleInk
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.doodle_ink_accent
import io.github.droidkaigi.confsched.core.ui.generated.resources.doodle_ink_default
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Picks the ink the next stroke is drawn in, shown as the two colors the surface itself draws them
 * in: [inkColor] for [DoodleInk.Default] and [accentColor] for [DoodleInk.Accent].
 */
@Composable
fun DoodleInkRow(
    selectedInk: DoodleInk,
    inkColor: Color,
    accentColor: Color,
    onInkClick: (DoodleInk) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DoodleInkRowDefaults.gap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DoodleInk.entries.forEach { ink ->
            DoodleInkSwatch(
                ink = ink,
                color = when (ink) {
                    DoodleInk.Default -> inkColor
                    DoodleInk.Accent -> accentColor
                },
                selected = ink == selectedInk,
                onClick = { onInkClick(ink) },
            )
        }
    }
}

/**
 * One option of [DoodleInkRow]. The swatch keeps a rim of its own whatever the selection, so a
 * light ink stays visible against the controls' own background.
 */
@Composable
private fun DoodleInkSwatch(
    ink: DoodleInk,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val swatchShape = inkCircleShape(combineSketchSeed(INK_SWATCH_SEED + ink.ordinal))
    val ringShape = inkCircleShape(combineSketchSeed(INK_RING_SEED + ink.ordinal))
    val description = stringResource(ink.label)
    Box(
        modifier = modifier
            .size(DoodleInkRowDefaults.touchTargetSize)
            .semantics { contentDescription = description }
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(DoodleInkRowDefaults.ringSize)
                    .sketchBorder(ringShape, MaterialTheme.colorScheme.primary),
            )
        }
        Box(
            modifier = Modifier
                .size(DoodleInkRowDefaults.swatchSize)
                .clip(swatchShape)
                .background(color)
                .sketchBorder(swatchShape, MaterialTheme.colorScheme.outline),
        )
    }
}

private fun inkCircleShape(seed: Int): SketchEllipseShape = SketchEllipseShape(
    seed = seed,
    roughness = DoodleInkRowDefaults.roughness,
    tremor = DoodleInkRowDefaults.tremor,
    sweepWavelength = DoodleInkRowDefaults.sweepWavelength,
    borderThickness = DoodleInkRowDefaults.borderThickness,
)

private object DoodleInkRowDefaults {
    val touchTargetSize = 44.dp
    val swatchSize = 28.dp
    val ringSize = 40.dp
    val gap = 8.dp
    val borderThickness = 1.5.dp
    val roughness = 0.4.dp
    val tremor = 0.15.dp
    val sweepWavelength = 60.dp
}

private val DoodleInk.label: StringResource
    get() = when (this) {
        DoodleInk.Default -> Res.string.doodle_ink_default
        DoodleInk.Accent -> Res.string.doodle_ink_accent
    }

private const val INK_SWATCH_SEED = 4371

private const val INK_RING_SEED = 4381

@LocalePreviews
@Composable
private fun DoodleInkRowPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleInkRow(
            selectedInk = DoodleInk.Default,
            inkColor = MaterialTheme.colorScheme.onSurface,
            accentColor = MaterialTheme.colorScheme.tertiary,
            onInkClick = {},
            modifier = Modifier.padding(DoodleInkRowPreviewPadding),
        )
    }
}

@LocalePreviews
@Composable
private fun DoodleInkRowAccentSelectedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleInkRow(
            selectedInk = DoodleInk.Accent,
            inkColor = MaterialTheme.colorScheme.onPrimary,
            accentColor = MaterialTheme.colorScheme.tertiary,
            onInkClick = {},
            modifier = Modifier.padding(DoodleInkRowPreviewPadding),
        )
    }
}

private val DoodleInkRowPreviewPadding = 16.dp

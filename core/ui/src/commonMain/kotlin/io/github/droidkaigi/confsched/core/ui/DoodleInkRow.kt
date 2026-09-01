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
import io.github.droidkaigi.confsched.core.ui.generated.resources.doodle_ink_band
import io.github.droidkaigi.confsched.core.ui.generated.resources.doodle_ink_banner
import io.github.droidkaigi.confsched.core.ui.generated.resources.doodle_ink_ink
import io.github.droidkaigi.confsched.core.ui.generated.resources.doodle_ink_paper
import io.github.droidkaigi.confsched.core.ui.generated.resources.doodle_ink_wall
import io.github.droidkaigi.confsched.core.ui.profilecard.profileCardDoodleInkPalette
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Picks the ink the next stroke is drawn in, each option shown in the color [palette] gives it.
 * Only the inks [palette] tells apart are offered, so a scheme painting two of them alike offers
 * one swatch for the pair.
 */
@Composable
fun DoodleInkRow(
    selectedInk: DoodleInk,
    palette: DoodleInkPalette,
    surface: DoodleInkRowSurface,
    onInkClick: (DoodleInk) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DoodleInkRowDefaults.gap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        palette.distinctInks().forEach { ink ->
            DoodleInkSwatch(
                ink = ink,
                surface = surface,
                color = palette.style(ink).color,
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
    surface: DoodleInkRowSurface,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val swatchShape = inkCircleShape(combineSketchSeed(INK_SWATCH_SEED + ink.ordinal))
    val ringShape = inkCircleShape(combineSketchSeed(INK_RING_SEED + ink.ordinal))
    val description = stringResource(ink.label(surface))
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

private fun DoodleInk.label(surface: DoodleInkRowSurface): StringResource = when (this) {
    DoodleInk.Ink -> Res.string.doodle_ink_ink

    // On the wall the Band slot resolves to the wall's own fill, so the label names what
    // the user is looking at rather than a card part that is not on screen.
    DoodleInk.Band -> if (surface == DoodleInkRowSurface.Wall) Res.string.doodle_ink_wall else Res.string.doodle_ink_band

    DoodleInk.Paper -> Res.string.doodle_ink_paper

    DoodleInk.Banner -> Res.string.doodle_ink_banner
}

/** The surface a [DoodleInkRow] offers inks for; it decides how the Band slot is named. */
enum class DoodleInkRowSurface { Wall, Card }

private const val INK_SWATCH_SEED = 4371

private const val INK_RING_SEED = 4381

@LocalePreviews
@Composable
private fun DoodleInkRowPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleInkRow(
            selectedInk = DoodleInk.Ink,
            palette = profileCardDoodleInkPalette(),
            surface = DoodleInkRowSurface.Card,
            onInkClick = {},
            modifier = Modifier.padding(DoodleInkRowPreviewPadding),
        )
    }
}

@LocalePreviews
@Composable
private fun DoodleInkRowBandSelectedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleInkRow(
            selectedInk = DoodleInk.Band,
            palette = aboutWallDoodleInkPalette(),
            surface = DoodleInkRowSurface.Wall,
            onInkClick = {},
            modifier = Modifier.padding(DoodleInkRowPreviewPadding),
        )
    }
}

private val DoodleInkRowPreviewPadding = 16.dp

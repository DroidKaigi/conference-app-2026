package io.github.droidkaigi.confsched.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * The inks as the About wall draws them, over the wall's own `primary` fill. The default ink is the
 * one color the wall never sits close to, so it alone is drawn bare; the crayons take a `surface`
 * rim that lifts a fixed pigment off whichever wall the scheme paints, and the chalk crayon, light
 * enough to vanish into that rim, is outlined in `scrim` instead.
 */
@Composable
fun aboutWallDoodleInkPalette(): DoodleInkPalette = DoodleInkPalette(
    default = DoodleInkStyle(color = MaterialTheme.colorScheme.onPrimary, haloColor = null),
    accent = DoodleInkStyle(
        color = DoodleCrayonColors.Orange,
        haloColor = MaterialTheme.colorScheme.surface,
    ),
    pink = DoodleInkStyle(
        color = DoodleCrayonColors.Pink,
        haloColor = MaterialTheme.colorScheme.surface,
    ),
    chalk = DoodleInkStyle(
        color = DoodleCrayonColors.Chalk,
        haloColor = MaterialTheme.colorScheme.scrim,
    ),
)

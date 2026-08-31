package io.github.droidkaigi.confsched.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * The inks as the About wall draws them, over the wall's own `primary` fill. The default ink is the
 * one color the wall never sits close to, so it alone is drawn bare; the rest take a rim. The chalk
 * ink is `surfaceBright`, light in every scheme, and its `scrim` rim both carries it over the light
 * walls of the dark schemes and tells it apart from a default ink that is white in the light ones.
 */
@Composable
fun aboutWallDoodleInkPalette(): DoodleInkPalette = DoodleInkPalette(
    default = DoodleInkStyle(color = MaterialTheme.colorScheme.onPrimary, haloColor = null),
    accent = DoodleInkStyle(
        color = MaterialTheme.colorScheme.tertiary,
        haloColor = MaterialTheme.colorScheme.surface,
    ),
    gold = DoodleInkStyle(
        color = MaterialTheme.colorScheme.secondary,
        haloColor = MaterialTheme.colorScheme.surface,
    ),
    chalk = DoodleInkStyle(
        color = MaterialTheme.colorScheme.surfaceBright,
        haloColor = MaterialTheme.colorScheme.scrim,
    ),
)

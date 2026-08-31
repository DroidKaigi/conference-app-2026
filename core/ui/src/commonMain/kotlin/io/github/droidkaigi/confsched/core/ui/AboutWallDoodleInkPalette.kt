package io.github.droidkaigi.confsched.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * The inks as the About wall draws them, over the wall's own `primary` fill. Each rim is the
 * Material pair of the color the ink is drawn in, so it contrasts the stroke in every scheme. The
 * default ink is `onPrimary`, whose pair is the wall itself, so it takes `onSurface` instead.
 */
@Composable
fun aboutWallDoodleInkPalette(): DoodleInkPalette = DoodleInkPalette(
    ink = DoodleInkStyle(
        color = MaterialTheme.colorScheme.onPrimary,
        rimColor = MaterialTheme.colorScheme.onSurface,
    ),
    band = DoodleInkStyle(
        color = MaterialTheme.colorScheme.primary,
        rimColor = MaterialTheme.colorScheme.onPrimary,
    ),
    paper = DoodleInkStyle(
        color = MaterialTheme.colorScheme.surface,
        rimColor = MaterialTheme.colorScheme.onSurface,
    ),
    banner = DoodleInkStyle(
        color = MaterialTheme.colorScheme.secondaryContainer,
        rimColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ),
)

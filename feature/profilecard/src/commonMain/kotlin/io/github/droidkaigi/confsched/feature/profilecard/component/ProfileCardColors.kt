package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * The finished card's palette, drawn from the app's active [io.github.droidkaigi.confsched.core.model.KaigiColorScheme]
 * so the card follows whichever theme the reader has chosen, the way issue #81 requires. Mirrors
 * the Figma source's own variable bindings (`md3/primary`, `md3/secondaryContainer`, `md3/surface`)
 * rather than a hand-picked pair, so [onDuskBand]/[onBanner] stay legible on [duskBand]/[banner].
 */
object ProfileCardColors {
    val plate: Color
        @Composable get() = MaterialTheme.colorScheme.surface

    val duskBand: Color
        @Composable get() = MaterialTheme.colorScheme.primary

    val onDuskBand: Color
        @Composable get() = MaterialTheme.colorScheme.onPrimary

    val banner: Color
        @Composable get() = MaterialTheme.colorScheme.secondaryContainer

    val onBanner: Color
        @Composable get() = MaterialTheme.colorScheme.onSecondaryContainer

    val ink: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface
}

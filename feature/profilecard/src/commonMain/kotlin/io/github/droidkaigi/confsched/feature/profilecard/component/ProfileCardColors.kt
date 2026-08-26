package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * The finished card's palette, drawn from the app's active [io.github.droidkaigi.confsched.core.model.KaigiColorScheme]
 * so the card follows whichever theme the reader has chosen, the way issue #81 requires. Mirrors
 * the Figma source's own variable bindings (`md3/primary`, `md3/secondaryContainer`, `md3/surface`)
 * rather than a hand-picked pair, so [onDuskBand]/[onBanner] stay legible on [duskBand]/[banner].
 */
object ProfileCardColors {
    val plate: Color
        @Composable get() = MaterialTheme.colorScheme.surface

    /** The lighter plate the QR code and the greeting bubble sit on (`md3/surfaceBright`). */
    val brightPlate: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceBright

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

    /** The secondary text colour the occupation and date lines are set in (`md3/onSurfaceVariant`). */
    val mutedInk: Color
        @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

    /**
     * The QR code's modules. A reader needs them dark against [brightPlate], so this takes
     * whichever of the surface pair the active theme paints darker — under a dark scheme that is
     * the surface itself, not the colour drawn on it.
     */
    val qrModule: Color
        @Composable get() {
            val onSurface = MaterialTheme.colorScheme.onSurface
            val surface = MaterialTheme.colorScheme.surface
            return if (onSurface.luminance() <= surface.luminance()) onSurface else surface
        }

    /** The rule under the nickname block (`md3/outline`). */
    val hairline: Color
        @Composable get() = MaterialTheme.colorScheme.outline
}

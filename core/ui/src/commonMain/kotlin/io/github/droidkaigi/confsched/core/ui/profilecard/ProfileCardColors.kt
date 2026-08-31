package io.github.droidkaigi.confsched.core.ui.profilecard

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * The finished card's palette, drawn from the app's active [io.github.droidkaigi.confsched.core.model.KaigiColorScheme]
 * so the card follows whichever theme the reader has chosen. Mirrors
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
     * The QR code itself is a neutral scan artefact, white paper and black modules in every
     * theme, as the design places it as an image rather than a themed surface.
     */
    val qrPaper: Color get() = Color.White

    val qrModule: Color get() = Color(0xFF1A1A1A)

    /** The rule under the nickname block (`md3/outline`). */
    val hairline: Color
        @Composable get() = MaterialTheme.colorScheme.outline
}

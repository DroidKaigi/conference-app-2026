package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * The finished card's palette, drawn from the app's active [io.github.droidkaigi.confsched.core.model.KaigiColorScheme]
 * so the card follows whichever theme the reader has chosen, the way issue #81 requires.
 *
 * [duskBand] and [plate] lean on `inverseSurface`/`surfaceContainerHigh`, a pair every scheme
 * defines to contrast with each other regardless of which one reads lighter in a given theme,
 * so content drawn in [plate] stays legible on a [duskBand] background either way.
 */
object ProfileCardColors {
    val plate: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceContainerHigh

    val duskBand: Color
        @Composable get() = MaterialTheme.colorScheme.inverseSurface

    val banner: Color
        @Composable get() = MaterialTheme.colorScheme.tertiaryContainer

    val ink: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface
}

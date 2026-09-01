package io.github.droidkaigi.confsched.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The type roles the design file names that no Material slot carries on its own.
 *
 * The accent face is the display family set at a body or label scale — the hand-lettered look
 * the design gives short, all-caps marks such as room and language chips. No [androidx.compose.material3.Typography]
 * slot below headline size carries that family, so the styles here re-dress a Material slot with it.
 */
object KaigiTextStyles {
    /** "label/medium - accent": the display family at 12sp, Bold, with the tracking closed up. */
    val labelMediumAccent: TextStyle
        @Composable get() = accent(MaterialTheme.typography.labelMedium)

    /** "title/small - accent": the same face one step up, at 14sp. */
    val titleSmallAccent: TextStyle
        @Composable get() = accent(MaterialTheme.typography.titleSmall)

    /**
     * [base] re-dressed in the accent face, keeping its size and line height.
     *
     * The family is read off the display slot rather than bound to a font directly, so the
     * settings screen's font choice carries: picking Noto Sans sets the accents in Noto Sans too.
     */
    @Composable
    private fun accent(base: TextStyle): TextStyle = base.copy(
        fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp,
    )
}

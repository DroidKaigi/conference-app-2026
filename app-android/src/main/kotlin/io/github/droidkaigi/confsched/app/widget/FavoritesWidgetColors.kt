package io.github.droidkaigi.confsched.app.widget

import androidx.compose.ui.graphics.Color
import io.github.droidkaigi.confsched.core.designsystem.isDark
import io.github.droidkaigi.confsched.core.designsystem.toMaterialColorScheme
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme

/** The scheme tokens the widget draws with; the scheme itself is fixed light or dark. */
internal data class FavoritesWidgetColors(
    val surface: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val primary: Color,
    val onPrimary: Color,
    val isDark: Boolean,
)

internal fun KaigiColorScheme.toFavoritesWidgetColors(): FavoritesWidgetColors {
    val scheme = toMaterialColorScheme()
    return FavoritesWidgetColors(
        surface = scheme.surfaceContainerLow,
        onSurface = scheme.onSurface,
        onSurfaceVariant = scheme.onSurfaceVariant,
        primary = scheme.primary,
        onPrimary = scheme.onPrimary,
        isDark = isDark,
    )
}

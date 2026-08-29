package io.github.droidkaigi.confsched.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import io.github.droidkaigi.confsched.core.common.PlatformOnly
import io.github.droidkaigi.confsched.core.common.TargetPlatform

/**
 * Keeps the status bar icons legible over [bandColor], the color the app draws behind them.
 * Android's edge-to-edge default follows the device's light/dark setting, which says nothing
 * about what the app draws there; iOS adapts its status bar to the content behind it, and the
 * desktop and web windows draw no status bar of their own.
 */
@PlatformOnly(TargetPlatform.Android)
@Composable
internal expect fun AndroidStatusBarIconAppearanceEffect(bandColor: Color)

/** Whether dark icons read better than light ones over this color. */
internal fun Color.prefersDarkIcons(): Boolean = luminance() > DARK_ICON_LUMINANCE_THRESHOLD

// The luminance where black and white icons tie in WCAG contrast, sqrt(0.05 * 1.05) - 0.05;
// above it dark icons read better. A 0.5 midpoint would pick the losing side for DeepTeal's
// light top app bar.
private const val DARK_ICON_LUMINANCE_THRESHOLD = 0.1791287f

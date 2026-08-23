package io.github.droidkaigi.confsched.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.github.droidkaigi.confsched.core.common.PlatformOnly
import io.github.droidkaigi.confsched.core.common.TargetPlatform
import io.github.droidkaigi.confsched.core.designsystem.isDark
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import kotlin.math.roundToInt

// Connects the root tab bar to the native shell: publishes the current tab through
// RootTabNavigator (null = tab bar hidden), routes native tab taps into the navigator, and hands
// the shell the theme colors it draws the bar with.
@PlatformOnly(TargetPlatform.Ios)
@Composable
internal fun IosTabBarSyncEffect(
    backStack: NavBackStack<NavKey>,
    rootTabNavigator: RootTabNavigator,
    rootTabBarAppearance: RootTabBarAppearance,
    colorScheme: KaigiColorScheme,
    onTabClick: (RootTab) -> Unit,
) {
    val currentTab = RootTab.entries.firstOrNull { it.key == backStack.lastOrNull() }
    LaunchedEffect(currentTab) {
        rootTabNavigator.updateCurrentTab(currentTab)
    }

    val palette = RootTabBarPalette(
        accentArgb = MaterialTheme.colorScheme.primary.toArgbLong(),
        isDark = colorScheme.isDark,
    )
    LaunchedEffect(palette) {
        rootTabBarAppearance.updatePalette(palette)
    }

    LaunchedEffect(Unit) {
        rootTabNavigator.selections.collect { tab -> onTabClick(tab) }
    }
}

// Not androidx's toArgb(): it packs the value into an Int that an opaque color makes negative, and
// widening that to Long sign-extends it into the bits above alpha.
private fun Color.toArgbLong(): Long {
    fun channel(value: Float) = (value * 255f).roundToInt().toLong()
    return (channel(alpha) shl 24) or (channel(red) shl 16) or (channel(green) shl 8) or channel(blue)
}

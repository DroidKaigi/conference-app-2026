package io.github.droidkaigi.confsched.app

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import io.github.droidkaigi.confsched.core.common.LocalStatusBarBandState
import io.github.droidkaigi.confsched.core.common.StatusBarBandState
import io.github.droidkaigi.confsched.core.common.context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Recreation — a configuration change or process death — always delivers a non-null
        // savedInstanceState and redelivers the task's original intent; the restored back stack
        // already reflects the link, so only a fresh creation consumes it.
        if (savedInstanceState == null) {
            submitDeepLink(intent)
        }
        setContent {
            val statusBarBandState = remember { StatusBarBandState() }
            StatusBarIconAppearanceEffect(window, statusBarBandState)
            CompositionLocalProvider(LocalStatusBarBandState provides statusBarBandState) {
                context(appGraph) {
                    KaigiApp()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        submitDeepLink(intent)
    }

    private fun submitDeepLink(intent: Intent) {
        intent.toDeepLink()?.let(appGraph.deepLinkStore::submit)
    }
}

// Keeps the status bar icons legible over the app's own colors: enableEdgeToEdge's default style
// follows the device's light/dark setting, which says nothing about the band the app draws.
@Composable
private fun StatusBarIconAppearanceEffect(window: Window, state: StatusBarBandState) {
    val view = LocalView.current
    val bandColor = state.bandColor
    SideEffect(window, view, bandColor) {
        if (bandColor.isUnspecified) return@SideEffect
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
            bandColor.luminance() > DARK_ICON_LUMINANCE_THRESHOLD
    }
}

// The luminance where black and white icons tie in WCAG contrast, sqrt(0.05 * 1.05) - 0.05;
// above it dark icons read better. A 0.5 midpoint would pick the losing side for DeepTeal's
// light top app bar.
private const val DARK_ICON_LUMINANCE_THRESHOLD = 0.1791287f

package io.github.droidkaigi.confsched.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.droidkaigi.confsched.app.notification.sessionReminderDependencies
import io.github.droidkaigi.confsched.core.common.LocalStatusBarBandState
import io.github.droidkaigi.confsched.core.common.StatusBarBandState
import io.github.droidkaigi.confsched.core.common.context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestNotificationPermission = registerForActivityResult(RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Recreation — a configuration change or process death — always delivers a non-null
        // savedInstanceState and redelivers the task's original intent; the restored back stack
        // already reflects the link, so only a fresh creation consumes it.
        if (savedInstanceState == null) {
            submitDeepLink(intent)
        }
        askForNotificationPermissionOnFirstFavorite()
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

    /** Session reminders are the only notification the app posts, so the prompt waits for a favorite. */
    private fun askForNotificationPermissionOnFirstFavorite() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sessionReminderDependencies.favoritesStore.favoriteIds().first { it.isNotEmpty() }
                val granted = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
                // A denied permission whose rationale is false has never been put to the user.
                val declined = shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
                if (!granted && !declined) {
                    requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
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

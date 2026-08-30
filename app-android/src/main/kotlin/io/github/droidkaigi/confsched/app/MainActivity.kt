package io.github.droidkaigi.confsched.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.droidkaigi.confsched.app.notification.sessionReminderDependencies
import io.github.droidkaigi.confsched.core.common.context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestNotificationPermission = registerForActivityResult(RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        // From API 30 the keyboard reports itself as an animating inset, so the window is left
        // as it is and the content makes room for it on its own; the padding then follows the
        // keyboard's animation rather than landing once the resize is over. Below 30 there is no
        // such inset to read, and the manifest's adjustResize stands.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        }
        super.onCreate(savedInstanceState)

        // Recreation — a configuration change or process death — always delivers a non-null
        // savedInstanceState and redelivers the task's original intent; the restored back stack
        // already reflects the link, so only a fresh creation consumes it.
        if (savedInstanceState == null) {
            submitDeepLink(intent)
        }
        askForNotificationPermissionOnFirstFavorite()
        setContent {
            context(appGraph) {
                KaigiApp()
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

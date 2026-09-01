package io.github.droidkaigi.confsched.core.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CompletableDeferred
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

@Composable
actual fun rememberNotificationPermissionRequester(): suspend () -> Unit {
    val context = LocalContext.current
    val activity = LocalActivity.current
    var pendingResult by remember { mutableStateOf<CompletableDeferred<Boolean>?>(null) }
    val launcher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
        pendingResult?.complete(granted)
        pendingResult = null
    }
    return remember(context, activity, launcher) {
        suspend {
            val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            var denied = false
            var permanentlyDenied = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !granted) {
                val rationaleBefore =
                    activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == true
                val result = CompletableDeferred<Boolean>()
                pendingResult = result
                val requestedAt = TimeSource.Monotonic.markNow()
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                denied = !result.await()
                // Only a request the system resolved by itself proves a permanent denial. The
                // rationale flag cannot tell that case apart from a first-time dialog that was
                // denied or cancelled unseen (the flag is down before and after in both), so the
                // remaining signal is time: a dialog that appeared cannot report back within the
                // same instant, while an auto-denied request does.
                val dialogNeverShown = requestedAt.elapsedNow() < 1.seconds
                permanentlyDenied = denied && !rationaleBefore && dialogNeverShown &&
                    activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == false
            }
            // Notifications can stay off with the permission granted (or below the runtime
            // permission entirely) when the reader disabled them in system settings, so the
            // request alone cannot finish the job. A dialog the reader freshly denied is the
            // one case that must not bounce to settings.
            val stillDisabled = !NotificationManagerCompat.from(context).areNotificationsEnabled()
            if (stillDisabled && (!denied || permanentlyDenied)) {
                context.startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName),
                )
            }
        }
    }
}

@Composable
actual fun rememberNotificationPermissionGranted(): Boolean? {
    val context = LocalContext.current
    // Covers the runtime permission and a channel the reader turned off in system settings alike.
    return remember(context) { NotificationManagerCompat.from(context).areNotificationsEnabled() }
}

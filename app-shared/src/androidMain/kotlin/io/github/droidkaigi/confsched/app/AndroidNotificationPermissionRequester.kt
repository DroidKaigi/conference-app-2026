package io.github.droidkaigi.confsched.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.NotificationPermissionResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

// Stable across activities: the registry hands a result it is holding to whoever registers the
// key it was launched under, which is what carries an answer over a recreation.
private const val POST_NOTIFICATIONS_REQUEST_KEY = "io.github.droidkaigi.confsched.app.post-notifications"

@Inject
class AndroidNotificationPermissionRequester(
    private val context: Context,
    private val activityHolder: CurrentActivityHolder,
) {
    suspend fun request(): NotificationPermissionResult = withContext(Dispatchers.Main) {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        var denied = false
        var permanentlyDenied = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !granted) {
            val rationaleBefore = shouldShowRationale()
            val requestedAt = TimeSource.Monotonic.markNow()
            denied = !awaitPermissionAnswer()
            // Only a request the system resolved by itself proves a permanent denial. The
            // rationale flag cannot tell that case apart from a first-time dialog that was
            // denied or cancelled unseen (the flag is down before and after in both), so the
            // remaining signal is time: a dialog that appeared cannot report back within the
            // same instant, while an auto-denied request does.
            val dialogNeverShown = requestedAt.elapsedNow() < 1.seconds
            permanentlyDenied = denied && !rationaleBefore && dialogNeverShown && !shouldShowRationale()
        }
        // Notifications can stay off with the permission granted (or below the runtime
        // permission entirely) when the reader disabled them in system settings, so the
        // request alone cannot finish the job. A dialog the reader freshly denied is the
        // one case that must not bounce to settings.
        val stillDisabled = !NotificationManagerCompat.from(context).areNotificationsEnabled()
        when {
            !stillDisabled -> NotificationPermissionResult.Enabled

            denied && !permanentlyDenied -> NotificationPermissionResult.Disabled

            else -> {
                openNotificationSettings()
                NotificationPermissionResult.SettingsOpened
            }
        }
    }

    private suspend fun awaitPermissionAnswer(): Boolean = coroutineScope {
        val answer = CompletableDeferred<Boolean>()
        var launched = false
        val registrations = launch {
            activityHolder.currentFlow.collectLatest { activity ->
                if (activity == null) return@collectLatest
                val launcher = activity.activityResultRegistry.register(
                    POST_NOTIFICATIONS_REQUEST_KEY,
                    RequestPermission(),
                    answer::complete,
                )
                try {
                    if (!launched) {
                        launched = true
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    awaitCancellation()
                } finally {
                    launcher.unregister()
                }
            }
        }
        try {
            answer.await()
        } finally {
            registrations.cancel()
        }
    }

    private fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        val activity = activityHolder.current
        if (activity != null) {
            activity.startActivity(intent)
        } else {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private fun shouldShowRationale(): Boolean =
        activityHolder.current?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == true
}

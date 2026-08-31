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
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CompletableDeferred

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !granted) {
                val result = CompletableDeferred<Boolean>()
                pendingResult = result
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                // A permission the reader has turned down for good returns without the system
                // dialog ever appearing, and leaves the rationale flag down; system settings is
                // then the only place it can still be granted.
                val permanentlyDenied = !result.await() &&
                    activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == false
                if (permanentlyDenied) {
                    context.startActivity(
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName),
                    )
                }
            }
        }
    }
}

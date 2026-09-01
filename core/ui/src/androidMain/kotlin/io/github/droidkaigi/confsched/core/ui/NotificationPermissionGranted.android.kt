package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat

@Composable
actual fun rememberNotificationPermissionGranted(): Boolean? {
    val context = LocalContext.current
    // Covers the runtime permission and a channel the reader turned off in system settings alike.
    return remember(context) { NotificationManagerCompat.from(context).areNotificationsEnabled() }
}

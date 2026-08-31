package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

@Composable
actual fun rememberNotificationPermissionRequester(): suspend () -> Unit = remember {
    suspend {
        suspendCancellableCoroutine { continuation ->
            UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge,
            ) { _, _ -> if (continuation.isActive) continuation.resume(Unit) }
        }
    }
}

@Composable
actual fun rememberNotificationPermissionGranted(): Boolean? {
    var granted by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(Unit) {
        granted = suspendCancellableCoroutine { continuation ->
            UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler { settings ->
                if (continuation.isActive) {
                    continuation.resume(settings?.authorizationStatus == UNAuthorizationStatusAuthorized)
                }
            }
        }
    }
    return granted
}

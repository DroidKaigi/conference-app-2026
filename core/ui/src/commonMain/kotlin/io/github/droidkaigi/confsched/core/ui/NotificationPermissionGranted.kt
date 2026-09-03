package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable

/**
 * Whether the platform already lets the app post notifications, or null while the platform has
 * not answered yet.
 */
@Composable
expect fun rememberNotificationPermissionGranted(): Boolean?

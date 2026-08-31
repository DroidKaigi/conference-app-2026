package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable

/**
 * Asks the platform for permission to post notifications and returns once the reader has
 * answered. A platform that grants it without asking, or that posts no notifications at all,
 * returns immediately.
 */
@Composable
expect fun rememberNotificationPermissionRequester(): suspend () -> Unit

/**
 * Whether the platform already lets the app post notifications, or null while the platform has
 * not answered yet.
 */
@Composable
expect fun rememberNotificationPermissionGranted(): Boolean?

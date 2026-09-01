package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable

/**
 * Asks the home screen to pin the app's widget, or `null` where the platform has no such
 * request — iOS, the desktop and the web, and an Android launcher that does not support it.
 */
@Composable
expect fun rememberFavoritesWidgetPinner(): (() -> Unit)?

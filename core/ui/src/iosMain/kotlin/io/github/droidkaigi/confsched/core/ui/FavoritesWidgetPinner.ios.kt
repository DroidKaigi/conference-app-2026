package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable

// iOS has no API a running app can call to place a widget; the reader does it from the home screen.
@Composable
actual fun rememberFavoritesWidgetPinner(): (() -> Unit)? = null

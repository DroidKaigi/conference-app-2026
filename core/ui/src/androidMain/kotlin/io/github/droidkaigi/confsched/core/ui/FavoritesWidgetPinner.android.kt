package io.github.droidkaigi.confsched.core.ui

import android.appwidget.AppWidgetManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberFavoritesWidgetPinner(): (() -> Unit)? {
    val context = LocalContext.current
    return remember(context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return@remember null
        val manager = AppWidgetManager.getInstance(context)
        if (!manager.isRequestPinAppWidgetSupported) return@remember null
        // The provider is looked up rather than named, so this stays out of the app module that
        // declares the widget.
        val provider = manager.getInstalledProvidersForPackage(context.packageName, null)
            .firstOrNull()
            ?.provider
            ?: return@remember null
        { manager.requestPinAppWidget(provider, null, null) }
    }
}

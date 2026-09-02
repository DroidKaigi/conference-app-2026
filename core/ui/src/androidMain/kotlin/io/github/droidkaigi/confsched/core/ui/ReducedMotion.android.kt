package io.github.droidkaigi.confsched.core.ui

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.awaitCancellation

@Composable
actual fun rememberReducedMotion(): Boolean {
    val contentResolver = LocalContext.current.contentResolver
    val reducedMotion by produceState(contentResolver.animationsRemoved(), contentResolver) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                value = contentResolver.animationsRemoved()
            }
        }
        contentResolver.registerContentObserver(
            Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
            false,
            observer,
        )
        try {
            awaitCancellation()
        } finally {
            contentResolver.unregisterContentObserver(observer)
        }
    }
    return reducedMotion
}

// "Remove animations" in the accessibility settings sets this scale to zero.
private fun ContentResolver.animationsRemoved(): Boolean =
    Settings.Global.getFloat(this, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f

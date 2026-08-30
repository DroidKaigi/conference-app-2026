package io.github.droidkaigi.confsched.app

import android.graphics.Color.TRANSPARENT
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import io.github.droidkaigi.confsched.core.common.PlatformOnly
import io.github.droidkaigi.confsched.core.common.TargetPlatform

@PlatformOnly(TargetPlatform.Android)
@Composable
internal actual fun AndroidStatusBarIconAppearanceEffect(bandColor: Color) {
    val activity = LocalActivity.current as? ComponentActivity ?: return
    val darkIcons = bandColor.prefersDarkIcons()
    SideEffect(activity, darkIcons) {
        // A light status bar carries dark icons, so the style names read the other way round.
        // navigationBarStyle keeps the default the activity already applies on creation.
        activity.enableEdgeToEdge(
            statusBarStyle = if (darkIcons) {
                SystemBarStyle.light(TRANSPARENT, TRANSPARENT)
            } else {
                SystemBarStyle.dark(TRANSPARENT)
            },
        )
    }
}

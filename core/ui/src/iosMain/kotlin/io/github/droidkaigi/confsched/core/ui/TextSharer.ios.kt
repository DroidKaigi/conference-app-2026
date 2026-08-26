package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIActivityViewController

@Composable
actual fun rememberTextSharer(): (String) -> Unit = remember {
    { text ->
        keyRootViewController()?.presentViewController(
            UIActivityViewController(activityItems = listOf(text), applicationActivities = null),
            animated = true,
            completion = null,
        )
    }
}

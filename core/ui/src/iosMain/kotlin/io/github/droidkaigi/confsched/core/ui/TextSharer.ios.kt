package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

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

private fun keyRootViewController(): UIViewController? = UIApplication.sharedApplication
    .connectedScenes
    .filterIsInstance<UIWindowScene>()
    .flatMap { scene -> scene.windows.filterIsInstance<UIWindow>() }
    .firstOrNull { it.isKeyWindow() }
    ?.rootViewController

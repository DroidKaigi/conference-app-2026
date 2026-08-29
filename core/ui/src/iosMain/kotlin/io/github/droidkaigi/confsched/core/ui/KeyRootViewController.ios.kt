package io.github.droidkaigi.confsched.core.ui

import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

internal fun keyRootViewController(): UIViewController? = UIApplication.sharedApplication
    .connectedScenes
    .filterIsInstance<UIWindowScene>()
    .flatMap { scene -> scene.windows.filterIsInstance<UIWindow>() }
    .firstOrNull { it.isKeyWindow() }
    ?.rootViewController

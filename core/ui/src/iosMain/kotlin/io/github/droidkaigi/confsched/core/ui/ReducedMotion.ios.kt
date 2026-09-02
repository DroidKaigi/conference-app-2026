package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import kotlinx.coroutines.awaitCancellation
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIAccessibilityIsReduceMotionEnabled
import platform.UIKit.UIAccessibilityReduceMotionStatusDidChangeNotification

@Composable
actual fun rememberReducedMotion(): Boolean {
    val reducedMotion by produceState(UIAccessibilityIsReduceMotionEnabled()) {
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIAccessibilityReduceMotionStatusDidChangeNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
        ) { _ ->
            value = UIAccessibilityIsReduceMotionEnabled()
        }
        try {
            awaitCancellation()
        } finally {
            NSNotificationCenter.defaultCenter.removeObserver(observer)
        }
    }
    return reducedMotion
}

package io.github.droidkaigi.confsched.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.navigationevent.NavigationEventHandler
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner

/**
 * Takes the system back gesture while [enabled], so a mode a screen opens within itself closes on
 * back instead of the screen being left. The innermost enabled handler wins, so the back stack only
 * sees the gesture once no screen claims it.
 *
 * Where no dispatcher is installed — a preview, a screenshot render — nothing is registered and the
 * gesture is left to the host.
 */
@Composable
fun SystemBackEffect(enabled: Boolean, onBack: () -> Unit) {
    val dispatcher = LocalNavigationEventDispatcherOwner.current?.navigationEventDispatcher ?: return
    val handler = remember(onBack) { SystemBackNavigationEventHandler(onBack) }
    handler.isBackEnabled = enabled
    DisposableEffect(dispatcher, handler) {
        dispatcher.addHandler(handler)
        onDispose(handler::remove)
    }
}

private class SystemBackNavigationEventHandler(
    private val onBack: () -> Unit,
) : NavigationEventHandler<NavigationEventInfo>(
    initialInfo = NavigationEventInfo.None,
    isBackEnabled = false,
) {
    override fun onBackCompleted() {
        onBack()
    }
}

package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import kotlinx.browser.window
import kotlinx.coroutines.awaitCancellation
import org.w3c.dom.events.Event

@Composable
actual fun rememberReducedMotion(): Boolean {
    val query = remember { window.matchMedia(REDUCED_MOTION_QUERY) }
    val reducedMotion by produceState(query.matches, query) {
        val listener: (Event) -> Unit = { value = query.matches }
        query.addEventListener(CHANGE_EVENT, listener)
        try {
            awaitCancellation()
        } finally {
            query.removeEventListener(CHANGE_EVENT, listener)
        }
    }
    return reducedMotion
}

private const val REDUCED_MOTION_QUERY = "(prefers-reduced-motion: reduce)"
private const val CHANGE_EVENT = "change"

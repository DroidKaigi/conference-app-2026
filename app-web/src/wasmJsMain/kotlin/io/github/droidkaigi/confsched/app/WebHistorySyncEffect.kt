package io.github.droidkaigi.confsched.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.HistorySyncEffect
import io.github.droidkaigi.confsched.core.common.NoopHistorySyncEffect
import kotlinx.browser.window
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.drop

@Inject
@ContributesBinding(AppScope::class, replaces = [NoopHistorySyncEffect::class])
class WebHistorySyncEffect(private val navigator: AppNavigator) : HistorySyncEffect {

    @Composable
    override fun Sync(backStack: NavBackStack<NavKey>) {
        val state = remember { SyncState() }

        LaunchedEffect(backStack) {
            snapshotFlow(backStack::lastOrNull)
                .drop(1)
                .collect { topKey ->
                    if (state.suppressNextPush) {
                        state.suppressNextPush = false
                        return@collect
                    }
                    val hash = topKey?.toUrlHash() ?: return@collect
                    window.history.pushState(null, "", hash)
                }
        }

        LaunchedEffect(backStack) {
            popStateFlow().collect { hash ->
                val targetStack = parseUrlHash(hash)
                val targetTop = targetStack.lastOrNull() ?: return@collect
                when {
                    backStack.dropLast(1).toList() == targetStack -> {
                        state.suppressNextPush = true
                        navigator.back()
                    }

                    backStack.lastOrNull() != targetTop -> {
                        state.suppressNextPush = true
                        navigator.moveToTop(targetTop)
                    }
                }
            }
        }
    }

    private class SyncState {
        var suppressNextPush = false
    }
}

// Kotlin function types are supported as Kotlin/Wasm JS interop parameters.
@JsFun("(handler) => { const fn = () => handler(); window.addEventListener('popstate', fn); return fn; }")
private external fun jsAddPopStateListener(handler: () -> Unit): JsAny

@JsFun("(fn) => window.removeEventListener('popstate', fn)")
private external fun jsRemovePopStateListener(fn: JsAny)

private fun popStateFlow() = callbackFlow<String> {
    val fn = jsAddPopStateListener { trySend(window.location.hash) }
    awaitClose { jsRemovePopStateListener(fn) }
}

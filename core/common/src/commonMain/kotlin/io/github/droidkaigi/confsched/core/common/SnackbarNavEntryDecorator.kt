package io.github.droidkaigi.confsched.core.common

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.contains
import androidx.navigation3.scene.DialogSceneStrategy

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("LocalSnackbarHostState is provided by snackbarNavEntryDecorator")
}

@Composable
fun <T : Any> rememberSnackbarNavEntryDecorator(): NavEntryDecorator<T> {
    return remember {
        NavEntryDecorator { entry ->
            // Retained (not remembered) so a snackbar shown or queued right before a
            // configuration change survives the recreated composition.
            val snackbarHostState = retain { SnackbarHostState() }
            CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
                if (DialogSceneStrategy.Companion.DialogKey in entry.metadata) {
                    // The Scaffold's container colour would paint over everything behind the
                    // dialog window, leaving the entry's own scrim with nothing to dim.
                    entry.Content()
                } else {
                    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
                        entry.Content()
                    }
                }
            }
        }
    }
}

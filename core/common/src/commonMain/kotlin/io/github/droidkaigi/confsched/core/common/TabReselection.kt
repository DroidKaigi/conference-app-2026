package io.github.droidkaigi.confsched.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter

// Empty by default so a screen composed outside the app shell (previews, tests) collects nothing.
val LocalTabReselectionEvents = staticCompositionLocalOf<Flow<NavKey>>(::emptyFlow)

/** Runs [onReselect] each time the root [key] is reselected while it already sits on top. */
@Composable
fun TabReselectEffect(key: NavKey, onReselect: suspend () -> Unit) {
    val reselections = LocalTabReselectionEvents.current
    // Call the latest lambda per emission: a caller's scroll state can be rebuilt (a list-detail
    // pane swaps its LazyListState across the boundary) without restarting this collector.
    val currentOnReselect by rememberUpdatedState(onReselect)
    LaunchedEffect(reselections, key) {
        reselections.filter { it == key }.collect { currentOnReselect() }
    }
}

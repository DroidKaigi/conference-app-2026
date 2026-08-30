package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * A [LazyListState] that survives the list moving in and out of the list-detail pane layout,
 * for a list a `listPane()` entry draws.
 *
 * The pane scaffold lays its panes out inside a `LookaheadScope`, and a [LazyListState] latches
 * onto the first lookahead pass it sees: from then on it only publishes layout info and scroll
 * limits from lookahead passes. Once the detail closes, the list moves out of that scope, no
 * lookahead pass ever runs again, and the state freezes at the last values it saw there — the
 * list stops scrolling past the offset it held and any sticky content pinned to the layout info
 * piles up. Crossing the boundary therefore takes a fresh state, carrying the scroll position
 * over; the latch has no reset: https://issuetracker.google.com/issues/552354343
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun rememberListDetailSceneAwareLazyListState(): LazyListState {
    val isInListDetailSceneScope = LocalListDetailSceneScope.current != null
    var previous by remember { mutableStateOf<LazyListState?>(null) }
    val current = rememberSaveable(isInListDetailSceneScope, saver = LazyListState.Saver) {
        LazyListState(
            previous?.firstVisibleItemIndex ?: 0,
            previous?.firstVisibleItemScrollOffset ?: 0,
        )
    }
    SideEffect {
        previous = current
    }
    return current
}

package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListPrefetchResultScope
import androidx.compose.foundation.lazy.LazyListPrefetchScope
import androidx.compose.foundation.lazy.LazyListPrefetchStrategy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridLayoutInfo
import androidx.compose.foundation.lazy.grid.LazyGridPrefetchResultScope
import androidx.compose.foundation.lazy.grid.LazyGridPrefetchScope
import androidx.compose.foundation.lazy.grid.LazyGridPrefetchStrategy
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.layout.LazyLayoutPrefetchState.PrefetchHandle
import androidx.compose.foundation.lazy.layout.NestedPrefetchScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * A [LazyListState] for a lazy container a list-detail pane entry draws.
 *
 * The state is rebuilt, carrying the scroll position over, when the entry crosses the list-detail
 * boundary, and the prefetch requests it left outstanding are canceled with it. Both are required:
 * see [Lazy containers in a pane](https://github.com/DroidKaigi/conference-app-2026/blob/main/docs/navigation-list-detail.md#lazy-containers-in-a-pane).
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun rememberListDetailSceneAwareLazyListState(): LazyListState {
    val isInListDetailSceneScope = LocalListDetailSceneScope.current != null
    var previous by remember { mutableStateOf<LazyListState?>(null) }
    val prefetchStrategy = remember(isInListDetailSceneScope) { CancelableLazyListPrefetchStrategy() }
    val current = rememberLazyListState(
        initialFirstVisibleItemIndex = previous?.firstVisibleItemIndex ?: 0,
        initialFirstVisibleItemScrollOffset = previous?.firstVisibleItemScrollOffset ?: 0,
        prefetchStrategy = prefetchStrategy,
    )
    DisposableEffect(prefetchStrategy) {
        onDispose(prefetchStrategy::cancelOutstandingPrefetches)
    }
    SideEffect {
        previous = current
    }
    return current
}

/** The [LazyGridState] counterpart of [rememberListDetailSceneAwareLazyListState]. */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun rememberListDetailSceneAwareLazyGridState(): LazyGridState {
    val isInListDetailSceneScope = LocalListDetailSceneScope.current != null
    var previous by remember { mutableStateOf<LazyGridState?>(null) }
    val prefetchStrategy = remember(isInListDetailSceneScope) { CancelableLazyGridPrefetchStrategy() }
    val current = rememberLazyGridState(
        initialFirstVisibleItemIndex = previous?.firstVisibleItemIndex ?: 0,
        initialFirstVisibleItemScrollOffset = previous?.firstVisibleItemScrollOffset ?: 0,
        prefetchStrategy = prefetchStrategy,
    )
    DisposableEffect(prefetchStrategy) {
        onDispose(prefetchStrategy::cancelOutstandingPrefetches)
    }
    SideEffect {
        previous = current
    }
    return current
}

@OptIn(ExperimentalFoundationApi::class)
private class CancelableLazyListPrefetchStrategy : LazyListPrefetchStrategy {
    private val delegate = LazyListPrefetchStrategy()
    private val outstanding = OutstandingPrefetches()

    fun cancelOutstandingPrefetches() {
        outstanding.cancelAll()
    }

    override fun LazyListPrefetchScope.onScroll(delta: Float, layoutInfo: LazyListLayoutInfo) {
        val tracked = TrackingScope(this)
        with(delegate) { tracked.onScroll(delta, layoutInfo) }
    }

    override fun LazyListPrefetchScope.onVisibleItemsUpdated(layoutInfo: LazyListLayoutInfo) {
        val tracked = TrackingScope(this)
        with(delegate) { tracked.onVisibleItemsUpdated(layoutInfo) }
    }

    override fun NestedPrefetchScope.onNestedPrefetch(firstVisibleItemIndex: Int) {
        with(delegate) { onNestedPrefetch(firstVisibleItemIndex) }
    }

    private inner class TrackingScope(private val actual: LazyListPrefetchScope) : LazyListPrefetchScope {
        override fun schedulePrefetch(
            index: Int,
            onPrefetchFinished: (LazyListPrefetchResultScope.() -> Unit)?,
        ): PrefetchHandle {
            var scheduled: PrefetchHandle? = null
            val handle = actual.schedulePrefetch(index) {
                scheduled?.let(outstanding::forget)
                onPrefetchFinished?.invoke(this)
            }
            scheduled = handle
            outstanding.track(handle)
            return handle
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private class CancelableLazyGridPrefetchStrategy : LazyGridPrefetchStrategy {
    private val delegate = LazyGridPrefetchStrategy()
    private val outstanding = OutstandingPrefetches()

    fun cancelOutstandingPrefetches() {
        outstanding.cancelAll()
    }

    override fun LazyGridPrefetchScope.onScroll(delta: Float, layoutInfo: LazyGridLayoutInfo) {
        val tracked = TrackingScope(this)
        with(delegate) { tracked.onScroll(delta, layoutInfo) }
    }

    override fun LazyGridPrefetchScope.onVisibleItemsUpdated(layoutInfo: LazyGridLayoutInfo) {
        val tracked = TrackingScope(this)
        with(delegate) { tracked.onVisibleItemsUpdated(layoutInfo) }
    }

    override fun NestedPrefetchScope.onNestedPrefetch(firstVisibleItemIndex: Int) {
        with(delegate) { onNestedPrefetch(firstVisibleItemIndex) }
    }

    private inner class TrackingScope(private val actual: LazyGridPrefetchScope) : LazyGridPrefetchScope {
        // The interface default drops the callback, so route the uncustomized call through the
        // overload that keeps the tracking one.
        override fun scheduleLinePrefetch(lineIndex: Int): List<PrefetchHandle> =
            scheduleLinePrefetch(lineIndex, null)

        override fun scheduleLinePrefetch(
            lineIndex: Int,
            onPrefetchFinished: (LazyGridPrefetchResultScope.() -> Unit)?,
        ): List<PrefetchHandle> {
            var scheduled: List<PrefetchHandle>? = null
            val handles = actual.scheduleLinePrefetch(lineIndex) {
                scheduled?.forEach(outstanding::forget)
                onPrefetchFinished?.invoke(this)
            }
            scheduled = handles
            handles.forEach(outstanding::track)
            return handles
        }
    }
}

/**
 * The prefetch handles a strategy has scheduled and the prefetcher has not reported as finished.
 */
@OptIn(ExperimentalFoundationApi::class)
private class OutstandingPrefetches {
    private val handles = mutableListOf<PrefetchHandle>()

    fun track(handle: PrefetchHandle) {
        handles.add(handle)
    }

    fun forget(handle: PrefetchHandle) {
        handles.remove(handle)
    }

    fun cancelAll() {
        handles.forEach(PrefetchHandle::cancel)
        handles.clear()
    }
}

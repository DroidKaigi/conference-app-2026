package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * A [PagerState] that survives the list moving in and out of the list-detail pane layout.
 *
 * Like [androidx.compose.foundation.lazy.LazyListState], [PagerState] keeps using lookahead
 * layout information after it has entered the pane scaffold's `LookaheadScope`. Leaving that
 * scope when a detail pane closes would otherwise leave the pager with stale scroll limits.
 * Crossing the boundary takes a fresh state at the page held by [PagerPosition].
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun rememberListDetailSceneAwarePagerState(
    position: PagerPosition,
    pageCount: () -> Int,
): PagerState {
    val isInListDetailSceneScope = LocalListDetailSceneScope.current != null
    val updatedPageCount = rememberUpdatedState(pageCount)
    val currentPageCount = remember { LatestPageCount(updatedPageCount) }
    return rememberSaveable(
        isInListDetailSceneScope,
        saver = pagerStateSaver(currentPageCount),
    ) {
        PagerState(
            currentPage = position.currentPage,
            currentPageOffsetFraction = 0f,
            pageCount = currentPageCount,
        )
    }
}

/**
 * The committed page to use when a pager needs a new state for a list-detail scene transition.
 *
 * A pager's fractional offset belongs to an in-progress gesture, so scene transitions always
 * restart at [currentPage] with an offset of zero.
 */
@Stable
class PagerPosition(initialPage: Int) {
    var currentPage by mutableIntStateOf(initialPage)
}

@Composable
fun rememberPagerPosition(initialPage: Int = 0): PagerPosition = rememberSaveable(
    saver = PagerPositionSaver,
) {
    PagerPosition(initialPage)
}

private val PagerPositionSaver = Saver<PagerPosition, Int>(
    save = { position -> position.currentPage },
    restore = { page -> PagerPosition(page) },
)

private class LatestPageCount(
    private val source: State<() -> Int>,
) : () -> Int {
    private val currentPageCount by source

    override fun invoke(): Int = currentPageCount()
}

@OptIn(ExperimentalFoundationApi::class)
private fun pagerStateSaver(pageCount: () -> Int) = listSaver<PagerState, Any>(
    save = { state ->
        listOf(state.currentPage, state.currentPageOffsetFraction)
    },
    restore = { values ->
        PagerState(
            currentPage = values[0] as Int,
            currentPageOffsetFraction = values[1] as Float,
            pageCount = pageCount,
        )
    },
)

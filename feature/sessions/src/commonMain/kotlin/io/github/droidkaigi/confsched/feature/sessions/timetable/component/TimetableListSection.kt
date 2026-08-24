package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.core.ui.TimetableItemCard
import io.github.droidkaigi.confsched.core.ui.TimetableTimeRange
import io.github.droidkaigi.confsched.core.ui.current

@Composable
internal fun TimetableListSection(
    uiState: TimetableListSectionUiState,
    contentPadding: PaddingValues,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = contentPadding + PaddingValues(
            top = 24.dp,
            bottom = 24.dp + LocalNavigationBarOccupiedHeight.current,
        ),
    ) {
        val hasBanner = uiState.countdownBannerUiState != null
        uiState.countdownBannerUiState?.let { countdownState ->
            item(key = "countdown_banner") {
                TimetableCountdownBanner(
                    uiState = countdownState,
                    seed = countdownState.nextSessions.firstOrNull()?.id?.value?.hashCode() ?: 0,
                    onItemClick = onItemClick,
                )
            }
        }

        itemsIndexed(uiState.timeSlots, key = { _, slot -> "${slot.startsAt}-${slot.endsAt}" }) { index, slot ->
            val layoutIndex = if (hasBanner) index + 1 else index
            SessionRow(
                slot = slot,
                bookmarks = uiState.bookmarks,
                onBookmarkClick = onBookmarkClick,
                onItemClick = onItemClick,
                timeRangeTranslationY = { timeRangeHeightPx ->
                    stickyTimeRangeTranslationY(listState, layoutIndex, timeRangeHeightPx)
                },
            )
        }
    }
}

/** One slot: when it runs, and the sessions running in it. */
@Composable
private fun SessionRow(
    slot: TimetableListSectionUiState.TimeSlot,
    bookmarks: Set<TimetableItemId>,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
    timeRangeTranslationY: (timeRangeHeightPx: Float) -> Float,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TimetableTimeRange(
            startsAt = slot.startsAt,
            endsAt = slot.endsAt,
            timeRangeState = slot.timeRangeState,
            liveBadgeEnabled = true,
            seed = slot.startsAt.hashCode(),
            modifier = Modifier.graphicsLayer {
                translationY = timeRangeTranslationY(size.height)
            },
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            for (item in slot.items) {
                TimetableItemCard(
                    title = item.title.current(),
                    room = item.room,
                    speaker = item.speakerNames,
                    isCancelled = item.isCancelled,
                    language = item.language,
                    isFavorite = item.id in bookmarks,
                    seed = item.id.value.hashCode(),
                    onBookmarkClick = { onBookmarkClick(item.id) },
                    onClick = { onItemClick(item.id) },
                )
            }
        }
    }
}

/**
 * Reads [LazyListState.layoutInfo]; call it from a draw-phase lambda, or every scroll frame
 * recomposes the caller.
 */
private fun stickyTimeRangeTranslationY(
    listState: LazyListState,
    itemIndex: Int,
    timeRangeHeightPx: Float,
): Float {
    val layoutInfo = listState.layoutInfo
    val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemIndex } ?: return 0f
    val pinLinePx = layoutInfo.viewportStartOffset + layoutInfo.beforeContentPadding
    val maxTranslationPx = (itemInfo.size - timeRangeHeightPx).coerceAtLeast(0f)
    return (pinLinePx - itemInfo.offset).toFloat().coerceIn(0f, maxTranslationPx)
}

@LocalePreviews
@Composable
private fun TimetableListSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableListSection(
            uiState = TimetableListSectionUiState.fake(),
            contentPadding = PaddingValues(),
            onBookmarkClick = {},
            onItemClick = {},
        )
    }
}

@LocalePreviews
@Composable
private fun TimetableListSectionStickyTimeRangePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        // Shorter than the sample content, so the list can hold the pinned scroll position.
        Box(modifier = Modifier.height(400.dp)) {
            TimetableListSection(
                uiState = TimetableListSectionUiState.fake(),
                contentPadding = PaddingValues(),
                onBookmarkClick = {},
                onItemClick = {},
                listState = rememberLazyListState(
                    initialFirstVisibleItemIndex = 1,
                    initialFirstVisibleItemScrollOffset = 100,
                ),
            )
        }
    }
}

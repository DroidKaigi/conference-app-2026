package io.github.droidkaigi.confsched.feature.favorites.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.common.TabReselectEffect
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableSpeaker
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.core.ui.TimetableDayHeader
import io.github.droidkaigi.confsched.core.ui.TimetableItemCard
import io.github.droidkaigi.confsched.core.ui.TimetableItemCardsFlowRow
import io.github.droidkaigi.confsched.core.ui.TimetableLineState
import io.github.droidkaigi.confsched.core.ui.TimetableTimeRange
import io.github.droidkaigi.confsched.core.ui.current
import io.github.droidkaigi.confsched.core.ui.rememberListDetailSceneAwareLazyListState
import io.github.droidkaigi.confsched.feature.favorites.FavoritesNavKey
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun FavoritesListSection(
    uiState: FavoritesListSectionUiState,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberListDetailSceneAwareLazyListState()
    TabReselectEffect(FavoritesNavKey) { listState.animateScrollToItem(0) }
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(
            top = 12.dp,
            bottom = 24.dp + LocalNavigationBarOccupiedHeight.current,
        ),
    ) {
        uiState.timeSlots.groupBy { slot -> slot.day }.forEach { (day, slots) ->
            if (uiState.dayHeadersVisible) {
                item(key = "header-$day") {
                    TimetableDayHeader(day = day)
                }
            }
            items(
                items = slots,
                key = { slot -> "$day-${slot.startsAt}-${slot.endsAt}" },
            ) { slot ->
                FavoriteSessionRow(
                    startsAt = slot.startsAt,
                    endsAt = slot.endsAt,
                    timeRangeState = slot.timeRangeState,
                    items = slot.items,
                    onBookmarkClick = onBookmarkClick,
                    onItemClick = onItemClick,
                )
            }
        }
    }
}

/** One slot: when it runs, and the saved sessions running in it. */
@Composable
private fun FavoriteSessionRow(
    startsAt: String,
    endsAt: String,
    timeRangeState: TimetableLineState,
    items: PersistentList<TimetableItem>,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        TimetableTimeRange(
            startsAt = startsAt,
            endsAt = endsAt,
            timeRangeState = timeRangeState,
            seed = startsAt.hashCode(),
        )
        TimetableItemCardsFlowRow(
            items = items,
            modifier = Modifier.weight(1f),
        ) { item ->
            FavoriteTimetableItemCard(
                id = item.id,
                title = item.title.current(),
                room = item.room,
                speakers = item.speakers,
                language = item.language,
                isCancelled = item.isCancelled,
                onBookmarkClick = { onBookmarkClick(item.id) },
                onItemClick = { onItemClick(item.id) },
            )
        }
    }
}

/** Fades the card out locally, then reports the unfavorite once the fade finishes. */
@Composable
private fun FavoriteTimetableItemCard(
    id: TimetableItemId,
    title: String,
    room: SessionRoom,
    speakers: List<TimetableSpeaker>,
    language: Language,
    isCancelled: Boolean,
    onBookmarkClick: () -> Unit,
    onItemClick: () -> Unit,
) {
    var visible by remember(id) { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    AnimatedVisibility(
        visible = visible,
        exit = fadeOut(animationSpec = tween(FavoriteTimetableItemCardDefaults.FADE_OUT_DURATION)),
    ) {
        TimetableItemCard(
            title = title,
            room = room,
            speakers = speakers,
            isCancelled = isCancelled,
            language = language,
            isFavorite = true,
            seed = id.value.hashCode(),
            onBookmarkClick = {
                visible = false
                coroutineScope.launch {
                    delay(FavoriteTimetableItemCardDefaults.FADE_OUT_DURATION.toLong())
                    onBookmarkClick()
                }
            },
            onClick = onItemClick,
        )
    }
}

private object FavoriteTimetableItemCardDefaults {
    const val FADE_OUT_DURATION = 500
}

@LocalePreviews
@Composable
private fun FavoritesListSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FavoritesListSection(
            uiState = FavoritesListSectionUiState.fake(),
            onBookmarkClick = {},
            onItemClick = {},
        )
    }
}

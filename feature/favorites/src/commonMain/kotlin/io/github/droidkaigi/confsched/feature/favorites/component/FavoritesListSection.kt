package io.github.droidkaigi.confsched.feature.favorites.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.core.ui.TimetableDayHeader
import io.github.droidkaigi.confsched.core.ui.TimetableItemCard
import io.github.droidkaigi.confsched.core.ui.TimetableLineState
import io.github.droidkaigi.confsched.core.ui.TimetableTimeRange
import io.github.droidkaigi.confsched.core.ui.current
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
    LazyColumn(
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
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            for (item in items) {
                FavoriteTimetableItemCard(
                    item = item,
                    onBookmarkClick = { onBookmarkClick(item.id) },
                    onItemClick = { onItemClick(item.id) },
                )
            }
        }
    }
}

/** Fades the card out locally, then reports the unfavorite once the fade finishes. */
@Composable
private fun FavoriteTimetableItemCard(
    item: TimetableItem,
    onBookmarkClick: () -> Unit,
    onItemClick: () -> Unit,
) {
    var visible by remember(item.id) { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    AnimatedVisibility(
        visible = visible,
        exit = fadeOut(animationSpec = tween(FavoriteTimetableItemCardDefaults.FADE_OUT_DURATION)),
    ) {
        TimetableItemCard(
            title = item.title.current(),
            room = item.room,
            speaker = item.speakerNames,
            isCancelled = item.isCancelled,
            language = item.language,
            isFavorite = true,
            seed = item.id.value.hashCode(),
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

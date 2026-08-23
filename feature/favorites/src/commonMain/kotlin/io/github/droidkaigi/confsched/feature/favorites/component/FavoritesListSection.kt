package io.github.droidkaigi.confsched.feature.favorites.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiNavigationBarDefaults
import io.github.droidkaigi.confsched.core.ui.SketchHorizontalDivider
import io.github.droidkaigi.confsched.core.ui.TimetableItemCard
import io.github.droidkaigi.confsched.core.ui.TimetableLineState
import io.github.droidkaigi.confsched.core.ui.TimetableTimeRange
import io.github.droidkaigi.confsched.core.ui.current
import kotlinx.collections.immutable.PersistentList

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
            bottom = 24.dp + KaigiNavigationBarDefaults.occupiedHeight,
        ),
    ) {
        uiState.timeSlots.groupBy { slot -> slot.day }.forEach { (day, slots) ->
            if (uiState.dayHeadersVisible) {
                item(key = "header-$day") {
                    FavoritesDayHeader(day = day)
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

@Composable
private fun FavoritesDayHeader(day: DroidKaigi2026Day) {
    Row(
        modifier = Modifier
            .padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = day.label,
            modifier = Modifier.width(46.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SketchHorizontalDivider(
            seed = 693 + day.ordinal,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.3.dp,
        )
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
                TimetableItemCard(
                    title = item.title.current(),
                    room = item.room,
                    speaker = item.speakerNames,
                    isCancelled = item.isCancelled,
                    language = item.language,
                    isFavorite = true,
                    seed = item.id.value.hashCode(),
                    onBookmarkClick = { onBookmarkClick(item.id) },
                    onClick = { onItemClick(item.id) },
                )
            }
        }
    }
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

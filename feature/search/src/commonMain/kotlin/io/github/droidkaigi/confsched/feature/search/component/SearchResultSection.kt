package io.github.droidkaigi.confsched.feature.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.core.ui.TimetableItemCard
import io.github.droidkaigi.confsched.core.ui.TimetableTimeRange
import io.github.droidkaigi.confsched.core.ui.current
import io.github.droidkaigi.confsched.feature.search.generated.resources.Res
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_result_count
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SearchResultSection(
    uiState: SearchResultUiState.Found,
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
        item(key = "count") {
            Text(
                text = stringResource(Res.string.search_result_count, uiState.matchCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(
            items = uiState.timeSlots,
            key = { slot -> "${slot.day}-${slot.startsAt}-${slot.endsAt}" },
        ) { slot ->
            SearchResultRow(
                startsAt = slot.startsAt,
                endsAt = slot.endsAt,
                items = slot.items,
                bookmarks = uiState.bookmarks,
                titleMark = uiState.titleMark,
                onBookmarkClick = onBookmarkClick,
                onItemClick = onItemClick,
            )
        }
    }
}

/** One slot: when it runs, and the sessions a search found in it. */
@Composable
private fun SearchResultRow(
    startsAt: String,
    endsAt: String,
    items: PersistentList<TimetableItem>,
    bookmarks: PersistentSet<TimetableItemId>,
    titleMark: String,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TimetableTimeRange(startsAt = startsAt, endsAt = endsAt, seed = startsAt.hashCode())
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            for (item in items) {
                TimetableItemCard(
                    title = item.title.current(),
                    room = item.room,
                    speaker = item.speaker,
                    language = item.language,
                    isFavorite = item.id in bookmarks,
                    seed = item.id.value.hashCode(),
                    onBookmarkClick = { onBookmarkClick(item.id) },
                    onClick = { onItemClick(item.id) },
                    titleMark = titleMark,
                )
            }
        }
    }
}

/**
 * The two cases the highlight note singles out: a title matching the word more than once, and a
 * match the wrap splits across two lines.
 */
@LocalePreviews
@Composable
private fun SearchResultSectionMarkedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        val timetable = Timetable.fake()
        val marked = persistentListOf(
            timetable.items[0].copy(
                title = MultiLangText(
                    ja = "Compose で書く Compose",
                    en = "Compose written in Compose",
                ),
            ),
            timetable.items[1].copy(
                title = MultiLangText(
                    ja = "折り返しをまたぐ Compose Multiplatform の話",
                    en = "A placeholder title long enough that Compose Multiplatform falls across the wrap",
                ),
            ),
        )
        SearchResultSection(
            uiState = SearchResultUiState.Found(
                matchCount = marked.size,
                timeSlots = marked.toSearchTimeSlots(),
                bookmarks = timetable.bookmarks,
                titleMark = "Compose",
            ),
            onBookmarkClick = {},
            onItemClick = {},
        )
    }
}

@LocalePreviews
@Composable
private fun SearchResultSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        val timetable = Timetable.fake()
        SearchResultSection(
            uiState = SearchResultUiState.Found(
                matchCount = timetable.items.size,
                timeSlots = timetable.items.toSearchTimeSlots(),
                bookmarks = timetable.bookmarks,
                titleMark = "Session",
            ),
            onBookmarkClick = {},
            onItemClick = {},
        )
    }
}

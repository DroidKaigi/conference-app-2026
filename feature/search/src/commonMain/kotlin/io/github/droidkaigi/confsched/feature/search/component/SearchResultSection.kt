package io.github.droidkaigi.confsched.feature.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
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
import io.github.droidkaigi.confsched.core.ui.TimetableDayHeader
import io.github.droidkaigi.confsched.core.ui.TimetableItemCard
import io.github.droidkaigi.confsched.core.ui.TimetableItemCardsFlowRow
import io.github.droidkaigi.confsched.core.ui.TimetableLineState
import io.github.droidkaigi.confsched.core.ui.TimetableTimeRange
import io.github.droidkaigi.confsched.core.ui.current
import io.github.droidkaigi.confsched.core.ui.stableSketchSeed
import io.github.droidkaigi.confsched.core.ui.toTimetableTimeSlots
import io.github.droidkaigi.confsched.feature.search.generated.resources.Res
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_result_count
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.pluralStringResource
import kotlin.time.Instant

internal const val SEARCH_RESULT_SECTION_COUNT_TEST_TAG = "SearchResultSectionCountTestTag"

@Composable
internal fun SearchResultSection(
    uiState: SearchResultUiState.Found,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val timeSlotsByDay = remember(uiState.timeSlots) { uiState.timeSlots.groupBy { it.day } }
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = pluralStringResource(
                Res.plurals.search_result_count,
                uiState.matchCount,
                uiState.matchCount,
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 32.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag(SEARCH_RESULT_SECTION_COUNT_TEST_TAG)
                .semantics { liveRegion = LiveRegionMode.Polite },
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(
                top = 24.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 40.dp + WindowInsets.safeDrawing
                    .exclude(WindowInsets.ime)
                    .asPaddingValues()
                    .calculateBottomPadding(),
            ),
        ) {
            timeSlotsByDay.forEach { (day, slots) ->
                if (uiState.dayHeadersVisible) {
                    item(key = "header-$day") {
                        TimetableDayHeader(day = day)
                    }
                }
                items(
                    items = slots,
                    key = { slot -> "$day-${slot.startsAt}-${slot.endsAt}" },
                ) { slot ->
                    SearchResultRow(
                        startsAt = slot.startsAt,
                        endsAt = slot.endsAt,
                        timeRangeState = slot.timeRangeState,
                        items = slot.items,
                        bookmarks = uiState.bookmarks,
                        titleMark = uiState.titleMark,
                        onBookmarkClick = onBookmarkClick,
                        onItemClick = onItemClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    startsAt: String,
    endsAt: String,
    timeRangeState: TimetableLineState,
    items: PersistentList<TimetableItem>,
    bookmarks: PersistentSet<TimetableItemId>,
    titleMark: String,
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
            key(item.id) {
                TimetableItemCard(
                    title = item.title.current(),
                    room = item.room,
                    speakers = item.speakers,
                    language = item.language,
                    isFavorite = item.id in bookmarks,
                    isCancelled = item.isCancelled,
                    seed = item.id.value.hashCode(),
                    onBookmarkClick = { onBookmarkClick(item.id) },
                    onClick = { onItemClick(item.id) },
                    titleMark = titleMark,
                    titleMarkSeed = stableSketchSeed(item.id.value),
                )
            }
        }
    }
}

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
                timeSlots = marked.toTimetableTimeSlots(SearchResultPreviewTime),
                bookmarks = timetable.bookmarks,
                titleMark = "Compose",
                dayHeadersVisible = true,
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
                timeSlots = timetable.items.toTimetableTimeSlots(SearchResultPreviewTime),
                bookmarks = timetable.bookmarks,
                titleMark = "Session",
                dayHeadersVisible = true,
            ),
            onBookmarkClick = {},
            onItemClick = {},
        )
    }
}

private val SearchResultPreviewTime = Instant.parse("2026-09-02T12:00:00Z")

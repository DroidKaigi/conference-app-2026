package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Room
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
    onBookmarkClick: (TimetableItemId) -> Unit,
    onRoomClick: (Room) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(
            top = 24.dp,
            bottom = 24.dp + LocalNavigationBarOccupiedHeight.current,
        ),
    ) {
        items(uiState.timeSlots, key = { "${it.startsAt}-${it.endsAt}" }) { slot ->
            SessionRow(
                slot = slot,
                bookmarks = uiState.bookmarks,
                onBookmarkClick = onBookmarkClick,
                onRoomClick = onRoomClick,
                onItemClick = onItemClick,
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
    onRoomClick: (Room) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TimetableTimeRange(
            startsAt = slot.startsAt,
            endsAt = slot.endsAt,
            seed = slot.startsAt.hashCode(),
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
                    onRoomClick = if (item.room.floor == null) null else { -> onRoomClick(item.room) },
                    onClick = { onItemClick(item.id) },
                )
            }
        }
    }
}

@LocalePreviews
@Composable
private fun TimetableListSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableListSection(
            uiState = TimetableListSectionUiState.fake(),
            onBookmarkClick = {},
            onRoomClick = {},
            onItemClick = {},
        )
    }
}

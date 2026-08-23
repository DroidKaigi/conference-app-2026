package io.github.droidkaigi.confsched.feature.favorites.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.current
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun FavoritesListSection(
    uiState: FavoritesListSectionUiState,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        uiState.timeSlots.forEach { slot ->
            item(key = "${slot.day}-${slot.startsAt}-${slot.endsAt}") {
                // The day lives on each card's badge (unconditional, see FavoriteCard); the header stays time-only to avoid repeating it.
                Text(
                    text = "${slot.startsAt} - ${slot.endsAt}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            items(slot.items, key = { it.id.value }) { item ->
                FavoriteCard(
                    day = item.day,
                    title = item.title.current(),
                    room = item.room.name,
                    speaker = item.speakers.joinToString(", ") { it.name },
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

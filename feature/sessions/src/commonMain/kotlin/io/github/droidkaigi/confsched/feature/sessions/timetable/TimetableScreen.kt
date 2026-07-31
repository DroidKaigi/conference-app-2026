package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.safeClick
import io.github.droidkaigi.confsched.core.ui.safeClickable
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

@Composable
fun TimetableScreen(
    uiState: TimetableScreenUiState,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onDayClick: (DroidKaigi2026Day) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
    onToggleRawResponseClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DroidKaigi2026Day.entries.forEach { day ->
                FilterChip(
                    selected = uiState.day == day,
                    onClick = safeClick { onDayClick(day) },
                    label = { Text(day.name) },
                )
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.sessions) { item ->
                TimetableCard(
                    item = item,
                    isFavorite = item.id in uiState.bookmarks,
                    onBookmarkClick = onBookmarkClick,
                    onClick = onItemClick,
                )
            }

            item {
                RawResponseSection(
                    rawResponse = uiState.rawResponse,
                    isExpanded = uiState.isRawResponseExpanded,
                    onToggleClick = onToggleRawResponseClick,
                )
            }
        }
    }
}

@Composable
private fun TimetableCard(
    item: TimetableItem,
    isFavorite: Boolean,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onClick: (TimetableItemId) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth().safeClickable { onClick(item.id) }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold)
                Text("${item.startsAt}-${item.endsAt} · ${item.room} · ${item.speaker}")
            }
            IconButton(onClick = safeClick { onBookmarkClick(item.id) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove favorite" else "Add favorite",
                )
            }
        }
    }
}

@Composable
private fun RawResponseSection(
    rawResponse: String,
    isExpanded: Boolean,
    onToggleClick: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().safeClickable(onClick = onToggleClick).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowRight,
                contentDescription = if (isExpanded) "Collapse raw response" else "Expand raw response",
            )
            Text("Raw response", fontWeight = FontWeight.Bold)
        }

        if (isExpanded) {
            Text(
                text = rawResponse.ifEmpty { "(empty)" },
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            )
        }
    }
}

@Preview
@Composable
fun TimetableScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableScreen(
            uiState = TimetableScreenUiState(
                day = DroidKaigi2026Day.Day1,
                sessions = persistentListOf(
                    TimetableItem(TimetableItemId("d1a"), "Compose Multiplatform in Practice", "Room1", "Sp1", DroidKaigi2026Day.Day1, "10:00", "10:40"),
                    TimetableItem(TimetableItemId("d1b"), "Themed previews without codegen", "Room2", "Sp2", DroidKaigi2026Day.Day1, "11:00", "11:40"),
                ),
                bookmarks = persistentSetOf(TimetableItemId("d1a")),
                rawResponse = """{ "sessions": [ { "id": "d1a" } ] }""",
                isRawResponseExpanded = true,
            ),
            onBookmarkClick = {},
            onDayClick = {},
            onItemClick = {},
            onToggleRawResponseClick = {},
        )
    }
}

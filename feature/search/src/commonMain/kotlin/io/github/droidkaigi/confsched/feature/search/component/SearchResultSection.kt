package io.github.droidkaigi.confsched.feature.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.core.ui.TimetableItemCard
import io.github.droidkaigi.confsched.core.ui.current

@Composable
internal fun SearchResultSection(
    uiState: SearchResultUiState.Found,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            top = 24.dp,
            bottom = 24.dp + LocalNavigationBarOccupiedHeight.current,
        ),
    ) {
        items(uiState.items, key = { it.id.value }) { item ->
            TimetableItemCard(
                title = item.title.current(),
                room = item.room,
                speaker = item.speaker,
                language = item.language,
                isFavorite = item.id in uiState.bookmarks,
                seed = item.id.value.hashCode(),
                onBookmarkClick = { onBookmarkClick(item.id) },
                onClick = { onItemClick(item.id) },
            )
        }
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
            uiState = SearchResultUiState.Found(items = timetable.items, bookmarks = timetable.bookmarks),
            onBookmarkClick = {},
            onItemClick = {},
        )
    }
}

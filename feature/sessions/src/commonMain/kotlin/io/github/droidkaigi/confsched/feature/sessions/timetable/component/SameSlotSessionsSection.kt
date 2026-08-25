package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.DisplayLanguage
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.TimetableItemCard
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.sessions_in_the_same_slot
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailScreenUiState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SameSlotSessionsSection(
    items: PersistentList<TimetableItemDetailScreenUiState.SameSlotItem>,
    displayLanguage: DisplayLanguage,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SessionSectionLabel(text = stringResource(Res.string.sessions_in_the_same_slot))
        for (sameSlotItem in items) {
            val item = sameSlotItem.item
            TimetableItemCard(
                title = item.title.of(displayLanguage),
                room = item.room,
                speaker = item.speakerNames,
                language = item.language,
                isFavorite = sameSlotItem.isFavorite,
                isCancelled = item.isCancelled,
                seed = item.id.value.hashCode(),
                onBookmarkClick = { onBookmarkClick(item.id) },
                onClick = { onItemClick(item.id) },
            )
        }
    }
}

@LocalePreviews
@Composable
private fun SameSlotSessionsSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SameSlotSessionsSection(
            items = Timetable.fake().detailOf(TimetableItemId("d1b")).sameSlotItems
                .map { TimetableItemDetailScreenUiState.SameSlotItem(item = it, isFavorite = false) }
                .toPersistentList(),
            displayLanguage = DisplayLanguage.Japanese,
            onBookmarkClick = {},
            onItemClick = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}

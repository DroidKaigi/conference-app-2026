package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.roomTheme
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.RoomChip
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.add_favorite
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.remove_favorite
import org.jetbrains.compose.resources.stringResource

/**
 * One session, outlined by hand: the room and language it runs in, its title, and who gives
 * it, with the bookmark tinted in the room's color.
 */
@Composable
internal fun TimetableItemCard(
    title: String,
    room: Room,
    speaker: String,
    language: Language,
    isFavorite: Boolean,
    seed: Int,
    onBookmarkClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = SketchRoundRectShape(
        seed = seed,
        cornerRadius = TimetableItemCardDefaults.cornerRadius,
        borderThickness = TimetableItemCardDefaults.borderThickness,
        referenceSize = TimetableItemCardDefaults.referenceSize,
    )
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
        )
        CardBody(
            title = title,
            room = room,
            speaker = speaker,
            language = language,
            isFavorite = isFavorite,
            seed = seed,
            onBookmarkClick = onBookmarkClick,
            modifier = Modifier.clickable(onClick = onClick),
        )
        Box(
            Modifier
                .matchParentSize()
                .sketchBorder(shape, MaterialTheme.colorScheme.outline),
        )
    }
}

@Composable
private fun CardBody(
    title: String,
    room: Room,
    speaker: String,
    language: Language,
    isFavorite: Boolean,
    seed: Int,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChipRow(room = room, language = language, seed = seed)
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (speaker.isNotEmpty()) {
                Text(
                    text = speaker,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        FavoriteMark(room = room, isFavorite = isFavorite, onBookmarkClick = onBookmarkClick)
    }
}

@Composable
private fun ChipRow(room: Room, language: Language, seed: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        RoomChip(room = room, seed = seed + 1)
        LanguageChip(language = language, seed = seed + 2)
    }
}

@Composable
private fun FavoriteMark(room: Room, isFavorite: Boolean, onBookmarkClick: () -> Unit) {
    Icon(
        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
        contentDescription = if (isFavorite) stringResource(Res.string.remove_favorite) else stringResource(Res.string.add_favorite),
        tint = roomTheme(room).accent,
        modifier = Modifier
            .size(TimetableItemCardDefaults.favoriteSize)
            .clickable(onClick = onBookmarkClick),
    )
}

private object TimetableItemCardDefaults {
    val cornerRadius = 24.dp
    val borderThickness = 2.dp
    val favoriteSize = 24.dp

    /** The size the design draws a card at, which holds its outline still as one resizes. */
    val referenceSize = DpSize(292.dp, 135.dp)
}

@LocalePreviews
@Composable
private fun TimetableItemCardPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableItemCardSamples()
    }
}

@Composable
private fun TimetableItemCardSamples() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TimetableItemCard(
            title = "Sample Session A",
            room = Room.NARWHAL,
            speaker = "",
            language = Language.MIXED,
            isFavorite = true,
            seed = 100,
            onBookmarkClick = {},
            onClick = {},
        )
        TimetableItemCard(
            title = "サンプルセッションE、折り返しを確かめるための長いプレースホルダーのタイトル",
            room = Room.OTTER,
            speaker = "Speaker B",
            language = Language.ENGLISH,
            isFavorite = false,
            seed = 200,
            onBookmarkClick = {},
            onClick = {},
        )
    }
}

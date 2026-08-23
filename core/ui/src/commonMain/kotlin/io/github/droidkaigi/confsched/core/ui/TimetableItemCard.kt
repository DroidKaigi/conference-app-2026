package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.FavoriteBorder
import io.github.droidkaigi.confsched.core.designsystem.icon.FavoriteFilled
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.roomTheme
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.add_favorite
import io.github.droidkaigi.confsched.core.ui.generated.resources.remove_favorite
import io.github.droidkaigi.confsched.core.ui.generated.resources.room_mascot_meerkat
import io.github.droidkaigi.confsched.core.ui.generated.resources.room_mascot_narwhal
import io.github.droidkaigi.confsched.core.ui.generated.resources.room_mascot_otter
import io.github.droidkaigi.confsched.core.ui.generated.resources.room_mascot_panda
import io.github.droidkaigi.confsched.core.ui.generated.resources.room_mascot_quail
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * One session, outlined by hand: the room and language it runs in, its title, and who gives
 * it, with the bookmark tinted in the room's color. A saved session also carries the room's
 * mascot, which marks the card as saved at a glance.
 */
@Composable
fun TimetableItemCard(
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
    val combinedSeed = combineSketchSeed(seed)
    val shape = SketchRoundRectShape(
        seed = combinedSeed,
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
            seed = seed,
            modifier = Modifier.clickable(onClick = onClick),
        )
        if (isFavorite) {
            room.mascot?.let { mascot ->
                Icon(
                    painter = painterResource(mascot),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(TimetableItemCardDefaults.mascotPadding),
                )
            }
        }
        FavoriteMark(
            room = room,
            isFavorite = isFavorite,
            onBookmarkClick = onBookmarkClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(TimetableItemCardDefaults.favoritePadding),
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
    seed: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ChipRow(room = room, language = language, seed = seed)
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (speaker.isNotEmpty()) {
            SpeakerRow(speaker = speaker, seed = seed)
        }
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
private fun SpeakerRow(speaker: String, seed: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KaigiPlaceholderAvatar(
            seed = seed + 3,
            size = TimetableItemCardDefaults.avatarSize,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            borderColor = MaterialTheme.colorScheme.primaryContainer,
        ) {}
        Text(
            text = speaker,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FavoriteMark(
    room: Room,
    isFavorite: Boolean,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = if (isFavorite) KaigiIcons.Default.FavoriteFilled else KaigiIcons.Default.FavoriteBorder,
        contentDescription = if (isFavorite) stringResource(Res.string.remove_favorite) else stringResource(Res.string.add_favorite),
        tint = roomTheme(room).accent,
        modifier = modifier
            .size(TimetableItemCardDefaults.favoriteSize)
            .clickable(onClick = onBookmarkClick),
    )
}

/** The mascot drawn on a saved session's card, or null for a room the design gives none. */
private val Room.mascot: DrawableResource?
    get() = when (this) {
        Room.NARWHAL -> Res.drawable.room_mascot_narwhal
        Room.OTTER -> Res.drawable.room_mascot_otter
        Room.PANDA -> Res.drawable.room_mascot_panda
        Room.QUAIL -> Res.drawable.room_mascot_quail
        Room.MEERKAT -> Res.drawable.room_mascot_meerkat
        Room.UNKNOWN -> null
    }

private object TimetableItemCardDefaults {
    val cornerRadius = 24.dp
    val borderThickness = 2.dp
    val favoriteSize = 24.dp
    val favoritePadding = 12.dp
    val avatarSize = 24.dp

    /** Bottom-end inset of the mascot, taken from the design's mascot slot. */
    val mascotPadding = PaddingValues(end = 12.dp, bottom = 8.dp)

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
        TimetableItemCard(
            title = "Sample Session C",
            room = Room.QUAIL,
            speaker = "Speaker C",
            language = Language.ENGLISH,
            isFavorite = true,
            seed = 300,
            onBookmarkClick = {},
            onClick = {},
        )
    }
}

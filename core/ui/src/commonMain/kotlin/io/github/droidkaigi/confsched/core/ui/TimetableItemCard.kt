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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.FavoriteBorder
import io.github.droidkaigi.confsched.core.designsystem.icon.FavoriteFilled
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.roomTheme
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.TimetableSpeaker
import io.github.droidkaigi.confsched.core.model.TimetableSpeakerId
import io.github.droidkaigi.confsched.core.model.mascot
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.add_favorite
import io.github.droidkaigi.confsched.core.ui.generated.resources.cancelled_session
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_mascot_a
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_mascot_b
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_mascot_c
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_mascot_e
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_f
import io.github.droidkaigi.confsched.core.ui.generated.resources.remove_favorite
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * One session, outlined by hand: the room and language it runs in, its title, and who gives
 * it, with the bookmark tinted in the room's color. A saved session also carries the room's
 * mascot, which marks the card as saved at a glance, and a cancelled one leads with a banner
 * saying so and strikes its title through.
 */
@Composable
fun TimetableItemCard(
    title: String,
    room: SessionRoom,
    speakers: List<TimetableSpeaker>,
    language: Language,
    isFavorite: Boolean,
    isCancelled: Boolean,
    seed: Int,
    onBookmarkClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleMark: String = "",
    titleMarkSeed: Int = seed,
) {
    val combinedSeed = combineSketchSeed(seed)
    val shape = SketchRoundRectShape(
        seed = combinedSeed,
        roughness = SketchDefaults.roughness,
        tremor = SketchDefaults.tremor,
        cornerRadius = TimetableItemCardDefaults.cornerRadius,
        borderThickness = TimetableItemCardDefaults.borderThickness,
        referenceSize = TimetableItemCardDefaults.referenceSize,
    )
    // The card itself stays unclipped: sketchBorder strokes the clip outline down its center, so
    // clipping the whole card would cut the stroke in half. Only the background layer is clipped,
    // and it carries the click so the ripple and tap target cover the whole card.
    Box(modifier = modifier.fillMaxWidth().semantics(mergeDescendants = true) {}) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .clickable(onClick = onClick),
        )
        // Drawn before the body so a long title or speaker list stays legible over the mascot
        if (isFavorite) {
            room.mascot.cardArt?.let { mascot ->
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
        CardBody(
            title = title,
            titleMark = titleMark,
            room = room,
            speakers = speakers,
            language = language,
            isCancelled = isCancelled,
            seed = seed,
            titleMarkSeed = titleMarkSeed,
        )
        FavoriteMark(
            room = room,
            isFavorite = isFavorite,
            onBookmarkClick = onBookmarkClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(TimetableItemCardDefaults.favoritePadding),
        )
        Box(Modifier.matchParentSize().sketchBorder(shape, MaterialTheme.colorScheme.outline))
    }
}

@Composable
private fun CardBody(
    title: String,
    titleMark: String,
    room: SessionRoom,
    speakers: List<TimetableSpeaker>,
    language: Language,
    isCancelled: Boolean,
    seed: Int,
    titleMarkSeed: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (isCancelled) {
            CancelledBanner(modifier = Modifier.padding(end = TimetableItemCardDefaults.cancelledBannerEndInset))
        }
        ChipRow(room = room, language = language, seed = seed)
        SketchMarkedText(
            text = title,
            mark = titleMark,
            seed = titleMarkSeed,
            style = MaterialTheme.typography.titleMedium,
            color = if (isCancelled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            textDecoration = if (isCancelled) TextDecoration.LineThrough else null,
        )
        if (speakers.isNotEmpty()) {
            SpeakerColumn(speakers = speakers)
        }
    }
}

@Composable
private fun CancelledBanner(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(Res.string.cancelled_session),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.inverseOnSurface,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TimetableItemCardDefaults.cancelledBannerCornerRadius))
            .background(MaterialTheme.colorScheme.inverseSurface)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
private fun ChipRow(room: SessionRoom, language: Language, seed: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        RoomChip(room = room, seed = seed + 1)
        LanguageChip(language = language, seed = seed + 2)
    }
}

@Composable
private fun SpeakerColumn(speakers: List<TimetableSpeaker>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (speaker in speakers) {
            SpeakerRow(speaker = speaker)
        }
    }
}

@Composable
private fun SpeakerRow(speaker: TimetableSpeaker) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val iconUrl = speaker.iconUrl
        if (iconUrl != null) {
            KaigiAvatar(
                imageUrl = iconUrl,
                contentDescription = null,
                size = TimetableItemCardDefaults.avatarSize,
            )
        } else {
            KaigiFaceAvatar(
                size = TimetableItemCardDefaults.avatarSize,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                contentColor = MaterialTheme.colorScheme.primaryContainer,
                borderColor = MaterialTheme.colorScheme.primaryContainer,
            )
        }
        Text(
            text = speaker.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FavoriteMark(
    room: SessionRoom,
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

/** The card render of a [Mascot], or null for a character the design never draws on cards. */
private val Mascot.cardArt: DrawableResource?
    get() = when (this) {
        Mascot.A -> Res.drawable.card_mascot_a
        Mascot.B -> Res.drawable.card_mascot_b
        Mascot.C -> Res.drawable.card_mascot_c
        Mascot.D -> null
        Mascot.E -> Res.drawable.card_mascot_e
        Mascot.F -> Res.drawable.mascot_f
    }

private object TimetableItemCardDefaults {
    val cornerRadius = 24.dp
    val borderThickness = 2.dp
    val favoriteSize = 24.dp
    val favoritePadding = 12.dp
    val avatarSize = 32.dp
    val cancelledBannerCornerRadius = 6.dp

    /** Keeps the banner clear of the bookmark, which the card draws over the same corner. */
    val cancelledBannerEndInset = 32.dp

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
            room = SessionRoom.NARWHAL,
            speakers = emptyList(),
            language = Language.MIXED,
            isFavorite = true,
            isCancelled = false,
            seed = 100,
            onBookmarkClick = {},
            onClick = {},
        )
        TimetableItemCard(
            title = "サンプルセッションE、折り返しを確かめるための長いプレースホルダーのタイトル",
            room = SessionRoom.OTTER,
            speakers = listOf(sampleSpeaker("Speaker B")),
            language = Language.ENGLISH,
            isFavorite = false,
            isCancelled = true,
            seed = 200,
            onBookmarkClick = {},
            onClick = {},
        )
        TimetableItemCard(
            title = "Sample Session C",
            room = SessionRoom.QUAIL,
            speakers = listOf(sampleSpeaker("Speaker C"), sampleSpeaker("Speaker D")),
            language = Language.ENGLISH,
            isFavorite = true,
            isCancelled = false,
            seed = 300,
            onBookmarkClick = {},
            onClick = {},
        )
    }
}

private fun sampleSpeaker(name: String) = TimetableSpeaker(
    id = TimetableSpeakerId(name),
    name = name,
    tagLine = "",
    iconUrl = null,
)

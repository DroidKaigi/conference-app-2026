package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Person
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableSpeaker
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiAvatar
import io.github.droidkaigi.confsched.core.ui.KaigiPlaceholderAvatar
import io.github.droidkaigi.confsched.core.ui.LanguageChip
import io.github.droidkaigi.confsched.core.ui.RoomChip
import io.github.droidkaigi.confsched.core.ui.current
import kotlinx.collections.immutable.PersistentList

@Composable
internal fun SessionHeaderView(
    room: SessionRoom,
    title: String,
    language: Language,
    hasInterpretation: Boolean,
    isCancelled: Boolean,
    speakers: PersistentList<TimetableSpeaker>,
    seed: Int,
    contentInsets: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val headerColor = MaterialTheme.colorScheme.inverseSurface
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (!isCancelled) {
                    Modifier.drawBehind {
                        val extent = size.height * 4
                        drawRect(
                            color = headerColor,
                            topLeft = Offset(0f, -extent),
                            size = Size(size.width, size.height + extent),
                        )
                    }
                } else {
                    Modifier
                },
            )
            .background(headerColor)
            .padding(contentInsets)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RoomChip(room = room, seed = seed + 1)
            LanguageChip(language = language, seed = seed + 2)
            val interpreted = language.interpretedInto()
            if (hasInterpretation && interpreted != null) {
                LanguageChip(language = interpreted, seed = seed + 3)
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.inverseOnSurface,
            textDecoration = if (isCancelled) TextDecoration.LineThrough else null,
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            for (speaker in speakers) {
                SpeakerRow(speaker = speaker)
            }
        }
    }
}

@Composable
private fun SpeakerRow(speaker: TimetableSpeaker) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val iconUrl = speaker.iconUrl
        if (iconUrl != null) {
            KaigiAvatar(
                imageUrl = iconUrl,
                contentDescription = null,
                size = SessionHeaderViewDefaults.avatarSize,
                borderColor = MaterialTheme.colorScheme.inverseOnSurface,
            )
        } else {
            KaigiPlaceholderAvatar(
                seed = speaker.id.value.hashCode(),
                size = SessionHeaderViewDefaults.avatarSize,
                borderColor = MaterialTheme.colorScheme.inverseOnSurface,
            ) {
                Icon(
                    imageVector = KaigiIcons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(SessionHeaderViewDefaults.avatarIconSize),
                )
            }
        }
        Column {
            Text(
                text = speaker.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
            if (speaker.tagLine.isNotEmpty()) {
                Text(
                    text = speaker.tagLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                )
            }
        }
    }
}

internal fun Language.interpretedInto(): Language? = when (this) {
    Language.JAPANESE -> Language.ENGLISH
    Language.ENGLISH -> Language.JAPANESE
    Language.MIXED -> null
}

private object SessionHeaderViewDefaults {
    val avatarSize = 48.dp
    val avatarIconSize = 24.dp
}

@LocalePreviews
@Composable
private fun SessionHeaderViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        val item = TimetableItem.fake()
        SessionHeaderView(
            room = item.room,
            title = item.title.current(),
            language = item.language,
            hasInterpretation = item.hasInterpretation,
            isCancelled = item.isCancelled,
            speakers = item.speakers,
            seed = 610,
            contentInsets = PaddingValues(),
        )
    }
}

package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.roomTheme
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.current

@Composable
internal fun TimetableGridCell(
    title: MultiLangText,
    room: Room,
    speaker: String,
    startsAt: String,
    endsAt: String,
    height: Dp,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = roomTheme(room)
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .size(width = TimetableGridRoomColumnWidth, height = height)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(width = 1.dp, color = theme.accent, shape = shape)
            .clickable(onClick = onItemClick)
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title.current(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
                maxLines = if (height >= 92.dp) 3 else 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (height >= 56.dp) {
                Text(
                    text = "$startsAt - $endsAt",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (height >= 92.dp && speaker.isNotEmpty()) {
                Text(
                    text = speaker,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@LocalePreviews
@Composable
private fun TimetableGridCellPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        val item = TimetableItem.fake()
        TimetableGridCell(
            title = item.title,
            room = item.room,
            speaker = item.speaker,
            startsAt = item.startsAt,
            endsAt = item.endsAt,
            height = 88.dp,
            onItemClick = {},
        )
    }
}

@LocalePreviews
@Composable
private fun TimetableGridShortCellPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableGridCell(
            title = MultiLangText(
                ja = "短いセッション",
                en = "Short session with a long enough title",
            ),
            room = Room.NARWHAL,
            speaker = "Speaker A",
            startsAt = "10:00",
            endsAt = "10:20",
            height = 40.dp,
            onItemClick = {},
        )
    }
}

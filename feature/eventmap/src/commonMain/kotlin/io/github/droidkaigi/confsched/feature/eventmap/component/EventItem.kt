package io.github.droidkaigi.confsched.feature.eventmap.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiOutlinedButton
import io.github.droidkaigi.confsched.core.ui.RoomChip
import io.github.droidkaigi.confsched.feature.eventmap.EventMapItem
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.Res
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_learn_more_button_label
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_meetup_description
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_meetup_message
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_meetup_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EventItem(
    event: EventMapItem,
    seed: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            RoomChip(room = event.room, seed = seed)
            Text(
                text = stringResource(event.title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Text(
            text = stringResource(event.description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        event.note?.let {
            Text(
                text = stringResource(it),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
        event.detailPage?.let {
            KaigiOutlinedButton(
                onClick = { /* TODO */ },
                seed = 872 + seed,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(Res.string.event_map_learn_more_button_label),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}

@LocalePreviews
@Composable
private fun EventItemPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        EventItem(
            event = EventMapItem(
                title = Res.string.event_map_meetup_title,
                description = Res.string.event_map_meetup_description,
                room = Room.NARWHAL,
                note = Res.string.event_map_meetup_message,
                detailPage = "https://droidkaigi.jp/2026/",
            ),
            seed = 1,
        )
    }
}

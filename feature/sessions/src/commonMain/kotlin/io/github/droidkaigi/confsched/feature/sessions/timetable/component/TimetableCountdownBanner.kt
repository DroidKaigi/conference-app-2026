package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.RoomChip
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.current
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.countdown_banner_title
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.countdown_hours_minutes
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.countdown_minutes
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class TimetableCountdownBannerUiState(
    val nextSessions: PersistentList<TimetableItem>,
    val remainingDuration: Duration,
)

@Composable
internal fun TimetableCountdownBanner(
    uiState: TimetableCountdownBannerUiState,
    seed: Int,
    onItemClick: (TimetableItemId) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.nextSessions.isEmpty()) return

    val shape = SketchRoundRectShape(
        seed = combineSketchSeed(seed),
        cornerRadius = 16.dp,
        borderThickness = 1.5.dp,
    )

    val countdownText = if (uiState.remainingDuration.inWholeHours > 0) {
        stringResource(
            Res.string.countdown_hours_minutes,
            uiState.remainingDuration.inWholeHours.toInt(),
            (uiState.remainingDuration.inWholeMinutes % 60).toInt(),
        )
    } else {
        stringResource(
            Res.string.countdown_minutes,
            uiState.remainingDuration.inWholeMinutes.toInt(),
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .sketchBorder(shape, MaterialTheme.colorScheme.onSurface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "${stringResource(Res.string.countdown_banner_title)}  $countdownText",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            uiState.nextSessions.forEach { session ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(session.id) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RoomChip(
                        room = session.room,
                        seed = session.id.value.hashCode(),
                    )
                    Text(
                        text = session.title.current(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@LocalePreviews
@Composable
private fun TimetableCountdownBannerPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableCountdownBanner(
            uiState = TimetableCountdownBannerUiState(
                nextSessions = Timetable.fake().items.take(2).toPersistentList(),
                remainingDuration = 25.minutes,
            ),
            seed = 0,
            onItemClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

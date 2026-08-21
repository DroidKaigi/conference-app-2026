package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.LocationOn
import io.github.droidkaigi.confsched.core.designsystem.icon.Schedule
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.location
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.schedule
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TimetableItemDetailSummaryCard(
    day: DroidKaigi2026Day,
    startsAt: String,
    endsAt: String,
    room: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SummaryRow(
            imageVector = KaigiIcons.Default.Schedule,
            title = stringResource(Res.string.schedule),
            description = "${day.name} $startsAt - $endsAt",
        )
        SummaryRow(
            imageVector = KaigiIcons.Default.LocationOn,
            title = stringResource(Res.string.location),
            description = room,
        )
    }
}

@Composable
private fun SummaryRow(
    imageVector: ImageVector,
    title: String,
    description: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@LocalePreviews
@Composable
private fun TimetableItemDetailSummaryCardPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableItemDetailSummaryCard(
            day = DroidKaigi2026Day.Day1,
            startsAt = "10:00",
            endsAt = "10:40",
            room = "NARWHAL",
        )
    }
}

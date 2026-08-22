package io.github.droidkaigi.confsched.feature.about.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.LocationOn
import io.github.droidkaigi.confsched.core.designsystem.icon.Schedule
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_event_date
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_venue
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_view_map
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AboutEventCard(
    date: String,
    venue: String,
    viewMapLabel: String,
    onViewMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(onClick = onViewMap, modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(imageVector = KaigiIcons.Default.Schedule, contentDescription = null)
                Text(text = date, style = MaterialTheme.typography.bodyLarge)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(imageVector = KaigiIcons.Default.LocationOn, contentDescription = null)
                Column {
                    Text(text = venue, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = viewMapLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@LocalePreviews
@Composable
private fun AboutEventCardPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutEventCard(
            date = stringResource(Res.string.about_event_date),
            venue = stringResource(Res.string.about_venue),
            viewMapLabel = stringResource(Res.string.about_view_map),
            onViewMap = {},
        )
    }
}

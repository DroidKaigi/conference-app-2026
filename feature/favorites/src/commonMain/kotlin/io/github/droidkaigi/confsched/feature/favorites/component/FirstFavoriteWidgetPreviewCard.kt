package io.github.droidkaigi.confsched.feature.favorites.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.kaigiTypography
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.KaigiFontFamily
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.RoomChip
import io.github.droidkaigi.confsched.core.ui.SketchCard
import io.github.droidkaigi.confsched.core.ui.SketchDefaults
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.Res
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_widget_preview_heading
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_widget_preview_session1
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_widget_preview_session2
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_widget_preview_session3
import org.jetbrains.compose.resources.stringResource

/** A still picture of the home screen widget, drawn here rather than rendered by the launcher. */
@Composable
internal fun FirstFavoriteWidgetPreviewCard(modifier: Modifier = Modifier) {
    SketchCard(
        modifier = modifier.fillMaxWidth(),
        shape = SketchRoundRectShape(
            seed = 501,
            roughness = SketchDefaults.roughness,
            tremor = SketchDefaults.tremor,
            cornerRadius = 14.dp,
            borderThickness = 1.5.dp,
        ),
        borderColor = MaterialTheme.colorScheme.outlineVariant,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.first_favorite_widget_preview_heading),
                style = kaigiTypography(KaigiFontFamily.CourierPrime).labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            WidgetPreviewRow("10:30", stringResource(Res.string.first_favorite_widget_preview_session1), SessionRoom.OTTER, 511)
            WidgetPreviewRow("13:00", stringResource(Res.string.first_favorite_widget_preview_session2), SessionRoom.PANDA, 512)
            WidgetPreviewRow("15:20", stringResource(Res.string.first_favorite_widget_preview_session3), SessionRoom.QUAIL, 513)
        }
    }
}

@Composable
private fun WidgetPreviewRow(time: String, title: String, room: Room, seed: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = time,
            style = kaigiTypography(KaigiFontFamily.CourierPrime).labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            softWrap = false,
            modifier = Modifier.width(46.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        RoomChip(room = room, seed = seed)
    }
}

@LocalePreviews
@Composable
private fun FirstFavoriteWidgetPreviewCardPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FirstFavoriteWidgetPreviewCard(modifier = Modifier.padding(16.dp))
    }
}

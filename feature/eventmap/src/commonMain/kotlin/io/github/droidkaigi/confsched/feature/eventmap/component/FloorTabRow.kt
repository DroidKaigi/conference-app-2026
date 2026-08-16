package io.github.droidkaigi.confsched.feature.eventmap.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiFilterChip
import io.github.droidkaigi.confsched.feature.eventmap.EventMapFloor

@Composable
internal fun FloorTabRow(
    selectedFloor: EventMapFloor,
    onFloorClick: (EventMapFloor) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(FloorTabRowDefaults.spacing),
    ) {
        EventMapFloor.entries.forEachIndexed { index, floor ->
            KaigiFilterChip(
                selected = selectedFloor == floor,
                onClick = { onFloorClick(floor) },
                label = floor.label,
                seed = FloorTabRowDefaults.FIRST_SEED + index,
            )
        }
    }
}

private object FloorTabRowDefaults {
    val spacing = 8.dp
    const val FIRST_SEED = 811
}

@Preview
@Composable
private fun FloorTabRowPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FloorTabRow(selectedFloor = EventMapFloor.entries.first(), onFloorClick = {})
    }
}

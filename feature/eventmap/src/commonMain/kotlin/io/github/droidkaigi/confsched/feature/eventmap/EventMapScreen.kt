package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.feature.eventmap.component.FloorTabRow
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.Res
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_1f
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_b1f
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_content_description
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_title
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EventMapScreen(
    uiState: EventMapScreenUiState,
    onFloorClick: (EventMapFloor) -> Unit,
) {
    Scaffold(
        topBar = {
            KaigiTopAppBar(title = stringResource(Res.string.event_map_title))
        },
        contentWindowInsets = WindowInsets(),
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            FloorTabRow(selectedFloor = uiState.selectedFloor, onFloorClick = onFloorClick)

            Crossfade(targetState = uiState.selectedFloor) { floor ->
                Image(
                    painter = painterResource(floor.mapImage()),
                    contentDescription = stringResource(Res.string.event_map_content_description, floor.label),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )
            }
        }
    }
}

private fun EventMapFloor.mapImage(): DrawableResource = when (this) {
    EventMapFloor.Ground -> Res.drawable.event_map_1f
    EventMapFloor.Basement -> Res.drawable.event_map_b1f
}

@LocaleScreenPreviews
@Composable
private fun EventMapScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        EventMapScreen(
            uiState = EventMapScreenUiState(selectedFloor = EventMapFloor.Ground),
            onFloorClick = {},
        )
    }
}

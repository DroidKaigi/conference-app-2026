package io.github.droidkaigi.confsched.feature.eventmap.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.eventmap.EventMapFloor
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.Res
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_1f
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_b1f
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_content_description
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FloorMapCard(
    selectedFloor: EventMapFloor,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = selectedFloor,
        modifier = modifier,
    ) { targetFloor ->
        SketchCard(
            color = Color.White,
        ) {
            Image(
                painter = painterResource(targetFloor.mapImage()),
                contentDescription = stringResource(Res.string.event_map_content_description, targetFloor.label),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

private fun EventMapFloor.mapImage(): DrawableResource = when (this) {
    EventMapFloor.Ground -> Res.drawable.event_map_1f
    EventMapFloor.Basement -> Res.drawable.event_map_b1f
}

@LocalePreviews
@Composable
private fun FloorMapCardPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FloorMapCard(
            selectedFloor = EventMapFloor.Ground,
        )
    }
}

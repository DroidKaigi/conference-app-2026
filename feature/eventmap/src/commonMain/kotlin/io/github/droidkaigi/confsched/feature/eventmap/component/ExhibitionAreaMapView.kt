package io.github.droidkaigi.confsched.feature.eventmap.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchCard
import io.github.droidkaigi.confsched.core.ui.mapPainter
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.Res
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.stamp_collecting_map_content_description
import org.jetbrains.compose.resources.stringResource

// The enclosure is expressed in the basement map's own viewport units, so it scales with the map.
private const val MAP_VIEWPORT_WIDTH = 380f
private const val MAP_VIEWPORT_HEIGHT = 304f
private const val EXHIBITION_AREA_LEFT = 158.94f
private const val EXHIBITION_AREA_TOP = 141.44f
private const val EXHIBITION_AREA_WIDTH = 214f
private const val EXHIBITION_AREA_HEIGHT = 156f

@Composable
internal fun ExhibitionAreaMapView(modifier: Modifier = Modifier) {
    SketchCard(
        color = Color.White,
        modifier = modifier,
    ) {
        BoxWithConstraints(
            modifier = Modifier.padding(16.dp),
        ) {
            val scale = maxWidth / MAP_VIEWPORT_WIDTH

            Image(
                painter = Floor.Basement.mapPainter(),
                contentDescription = stringResource(Res.string.stamp_collecting_map_content_description),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(scale * MAP_VIEWPORT_WIDTH)
                    .height(scale * MAP_VIEWPORT_HEIGHT),
            )
            Box(
                modifier = Modifier
                    .offset(
                        x = scale * EXHIBITION_AREA_LEFT,
                        y = scale * EXHIBITION_AREA_TOP,
                    )
                    .size(
                        width = scale * EXHIBITION_AREA_WIDTH,
                        height = scale * EXHIBITION_AREA_HEIGHT,
                    )
                    .border(
                        width = 3.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(10.dp),
                    ),
            )
        }
    }
}

@LocalePreviews
@Composable
private fun ExhibitionAreaMapViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ExhibitionAreaMapView(
            modifier = Modifier.padding(16.dp),
        )
    }
}

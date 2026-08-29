package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density

/**
 * Records into [layer] whatever [content] draws through the modifier it is handed, without
 * painting it and without taking layout space — so a screen can hand out a picture of something it
 * never shows. [content] must apply that modifier to the one element being recorded, since the
 * element's own size is the size of the recording.
 *
 * The content is measured at one pixel per dp, which is what keeps the recorded image the same
 * size in pixels no matter what the device draws at.
 */
@Composable
fun RecordedOffScreen(layer: GraphicsLayer, content: @Composable (Modifier) -> Unit) {
    val recordingModifier = Modifier.drawWithContent {
        layer.record { this@drawWithContent.drawContent() }
    }
    Layout(
        content = {
            CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 1f)) {
                content(recordingModifier)
            }
        },
    ) { measurables, _ ->
        val placeables = measurables.map { it.measure(Constraints()) }
        layout(width = 0, height = 0) { placeables.forEach { it.place(0, 0) } }
    }
}

package io.github.droidkaigi.confsched.feature.about.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleStroke
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

/**
 * Draws [doodle] in the space its points are stored in: the y axis runs down from this layer's
 * top edge and the x axis out from its horizontal center, both in dp multiplied by [scale], so
 * the same doodle keeps its size and stays centered whatever the layer's width.
 */
@Composable
internal fun DoodleLayerView(
    doodle: Doodle,
    color: Color,
    scale: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val unit = density * scale
        val originX = size.width / 2f
        val stroke = Stroke(
            width = DoodleStrokeWidth.toPx() * scale,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        doodle.strokes.forEach { drawPath(path = it.toPath(originX, unit), color = color, style = stroke) }
    }
}

internal val DoodleStrokeWidth = 2.5.dp

// Each sampled point becomes the control point of a quadratic through its neighbours' midpoints,
// which rounds off the corners a finger's sampling rate leaves in a curve.
private fun DoodleStroke.toPath(originX: Float, unit: Float): Path {
    val offsets = points.map { Offset(x = originX + it.x * unit, y = it.y * unit) }
    val path = Path()
    val first = offsets.firstOrNull() ?: return path
    path.moveTo(first.x, first.y)
    if (offsets.size == 1) {
        // A round cap turns a zero-length segment into the dot a tap draws.
        path.lineTo(first.x, first.y)
        return path
    }
    for (index in 1 until offsets.size - 1) {
        val current = offsets[index]
        val next = offsets[index + 1]
        path.quadraticTo(current.x, current.y, (current.x + next.x) / 2f, (current.y + next.y) / 2f)
    }
    val last = offsets.last()
    path.lineTo(last.x, last.y)
    return path
}

@Preview
@Composable
private fun DoodleLayerViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleLayerView(
            doodle = Doodle.fake(),
            color = MaterialTheme.colorScheme.onPrimary,
            scale = 1f,
            modifier = Modifier
                .fillMaxWidth()
                .height(AboutHeroHeight)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}

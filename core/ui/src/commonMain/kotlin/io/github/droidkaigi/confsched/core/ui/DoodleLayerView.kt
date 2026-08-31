package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
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

/** Where a doodle's x axis starts within the layer that draws it. */
enum class DoodleOrigin {
    /** The layer's top-start corner, so a drawing keeps its distance from the leading edge. */
    TopStart,

    /** The middle of the layer's top edge, so a drawing stays centred whatever the layer's width. */
    TopCenter,
}

/**
 * Draws [doodle] in the space its points are stored in: the y axis runs down from this layer's
 * top edge and the x axis out from [origin], both in dp multiplied by [scale], so the same doodle
 * keeps its size wherever it is drawn. A [haloColor] rims every stroke, which keeps one ink
 * readable on a surface whose bands are darker and lighter than that ink; null draws bare ink.
 */
@Composable
fun DoodleLayerView(
    doodle: Doodle,
    color: Color,
    haloColor: Color?,
    origin: DoodleOrigin,
    scale: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val unit = density * scale
        val originX = origin.originX(size.width)
        val drawn = doodle.strokes.map { it.toPath(originX, unit) to it.width * unit }
        // Every halo is laid down before any ink, so one stroke's rim never covers a neighbour's ink.
        if (haloColor != null) {
            val rim = DoodleHaloWidth.toPx() * scale * 2
            drawn.forEach { (path, width) ->
                val halo = Stroke(width = width + rim, cap = StrokeCap.Round, join = StrokeJoin.Round)
                drawPath(path = path, color = haloColor, style = halo)
            }
        }
        drawn.forEach { (path, width) ->
            val stroke = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)
            drawPath(path = path, color = color, style = stroke)
        }
    }
}

internal fun DoodleOrigin.originX(width: Float): Float = when (this) {
    DoodleOrigin.TopStart -> 0f
    DoodleOrigin.TopCenter -> width / 2f
}

private val DoodleHaloWidth = 1.dp

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
            haloColor = null,
            origin = DoodleOrigin.TopCenter,
            scale = 1f,
            modifier = Modifier
                .size(AboutHeroSize)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}

package io.github.droidkaigi.confsched.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

/**
 * [text] with every run matching [mark] drawn over by a hand-drawn highlighter.
 *
 * The marker is a fill behind the glyphs and never an outline: an outline around a run reads as
 * one of the badges the cards already carry.
 *
 * A run reaching across a line break is drawn as one marker per line rather than one joined
 * shape, since a highlighter cannot cross the gap between two lines either. Each piece takes its
 * own seed, so two pieces of one word do not come out as the same stroke repeated.
 *
 * @param text the full text to render.
 * @param mark the run to mark, matched without regard to case. Blank marks nothing.
 * @param seed the value the markers are drawn from. Give the item's own identity, so a session's
 *   marker looks the same on every launch and every platform.
 * @param modifier the [Modifier] applied to the text.
 * @param style the style the text renders in.
 * @param color the colour the glyphs take.
 * @param markColor the colour filling the marker.
 * @param maxLines the most lines the text may occupy.
 * @param overflow how text past [maxLines] is treated.
 */
@Composable
fun SketchMarkedText(
    text: String,
    mark: String,
    seed: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    markColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val matches = remember(text, mark) { text.rangesOf(mark) }
    // Growth follows the run being typed rather than jumping to its new width.
    val grown by animateFloatAsState(
        targetValue = if (matches.isEmpty()) 0f else 1f,
        animationSpec = tween(durationMillis = SketchMarkedTextDefaults.GROWTH_MILLIS),
        label = "markerGrowth",
    )

    Text(
        text = text,
        modifier = modifier.drawBehind {
            val result = layout ?: return@drawBehind
            if (grown <= 0f) return@drawBehind
            for (range in matches) {
                drawMarkerRun(result, range, seed, markColor, grown)
            }
        },
        color = color,
        style = style,
        maxLines = maxLines,
        overflow = overflow,
        onTextLayout = { layout = it },
    )
}

/** Draws one match, split into a marker per line it reaches across. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMarkerRun(
    layout: TextLayoutResult,
    range: IntRange,
    seed: Int,
    markColor: Color,
    grown: Float,
) {
    val firstLine = layout.getLineForOffset(range.first)
    val lastLine = layout.getLineForOffset(range.last)
    for (line in firstLine..lastLine) {
        val start = maxOf(range.first, layout.getLineStart(line))
        val end = minOf(range.last + 1, layout.getLineEnd(line, visibleEnd = true))
        if (end <= start) continue

        val left = layout.getHorizontalPosition(start, usePrimaryDirection = true)
        val right = layout.getHorizontalPosition(end, usePrimaryDirection = true)
        val width = (right - left) * grown
        if (width <= 0f) continue

        val top = layout.getLineTop(line)
        val bottom = layout.getLineBottom(line)
        val height = (bottom - top).coerceAtMost(SketchMarkedTextDefaults.maxHeight.toPx())
        val centreY = (top + bottom) / 2f

        // The seed folds the line in, so a run split across two lines takes a different stroke on
        // each of them rather than the same one twice.
        val pieceSeed = SketchMarkedTextDefaults.SEED_BASE +
            (
                (
                    sketchStringHash(seed.toString()) +
                        range.first * SketchMarkedTextDefaults.START_MULTIPLIER +
                        line * SketchMarkedTextDefaults.LINE_MULTIPLIER
                    ).mod(SketchMarkedTextDefaults.SEED_SPREAD)
                )
        val path = sketchMarkerPath(width = width, height = height, seed = pieceSeed)
        translate(left = left, top = centreY) {
            drawPath(path, markColor)
        }
    }
}

/** Every range of this text matching [mark], ignoring case. A blank mark matches nothing. */
private fun String.rangesOf(mark: String): List<IntRange> {
    if (mark.isBlank()) return emptyList()
    val ranges = mutableListOf<IntRange>()
    var from = indexOf(mark, startIndex = 0, ignoreCase = true)
    while (from >= 0) {
        ranges += from until (from + mark.length)
        from = indexOf(mark, startIndex = from + mark.length, ignoreCase = true)
    }
    return ranges
}

object SketchMarkedTextDefaults {
    /** The band never grows past the run's x-height, however tall the line box is. */
    val maxHeight = 17.dp

    internal const val GROWTH_MILLIS = 150

    // The design fixes the seed's base and spread so a session's marker is reproducible.
    internal const val SEED_BASE = 700
    internal const val SEED_SPREAD = 293
    internal const val START_MULTIPLIER = 37
    internal const val LINE_MULTIPLIER = 101
}

@Preview
@Composable
private fun SketchMarkedTextPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        ) {
            SketchMarkedText(
                text = "Compose Multiplatform and Compose Runtime",
                mark = "Compose",
                seed = 41,
                style = MaterialTheme.typography.titleMedium,
            )
            SketchMarkedText(
                text = "Jetpack Compose で始めるアプリパフォーマンス計測と改善",
                mark = "Compose",
                seed = 42,
                style = MaterialTheme.typography.titleMedium,
            )
            SketchMarkedText(
                text = "Sample Session with a placeholder title long enough to wrap onto several lines",
                mark = "placeholder",
                seed = 43,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

package io.github.droidkaigi.confsched.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import kotlin.math.abs
import kotlin.math.floor

@Composable
fun SketchMarkedText(
    text: String,
    mark: String,
    seed: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    markColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    textDecoration: TextDecoration? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val normalizedMark = mark.trim()
    val matches = remember(text, normalizedMark) { text.rangesOf(normalizedMark) }
    if (matches.isEmpty()) {
        Text(
            text = text,
            modifier = modifier,
            color = color,
            style = style,
            textDecoration = textDecoration,
            maxLines = maxLines,
            overflow = overflow,
        )
        return
    }

    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    var previousMark by remember(text) { mutableStateOf("") }
    val revealedCharacters = remember(text) { Animatable(0f) }
    LaunchedEffect(text, normalizedMark) {
        val oldMark = previousMark
        val targetCharacterCount = normalizedMark.length.toFloat()
        val transition = markerRevealTransition(oldMark, normalizedMark)

        if (transition == MarkerRevealTransition.Restart) {
            revealedCharacters.snapTo(0f)
        }
        // Advance only after any required reset so cancellation retries an interrupted replacement.
        previousMark = normalizedMark

        if (transition == MarkerRevealTransition.Shorten && revealedCharacters.value > targetCharacterCount) {
            revealedCharacters.snapTo(targetCharacterCount)
        } else {
            revealedCharacters.animateTo(
                targetValue = targetCharacterCount,
                animationSpec = tween(
                    durationMillis = SketchMarkedTextDefaults.GROWTH_MILLIS,
                    easing = EaseOut,
                ),
            )
        }
    }

    Text(
        text = text,
        modifier = modifier.drawBehind {
            val result = layout ?: return@drawBehind
            if (revealedCharacters.value <= 0f) return@drawBehind
            for (range in matches) {
                drawMarkerRun(result, range, seed, markColor, revealedCharacters.value)
            }
        },
        color = color,
        style = style,
        textDecoration = textDecoration,
        maxLines = maxLines,
        overflow = overflow,
        onTextLayout = { layout = it },
    )
}

internal enum class MarkerRevealTransition {
    Continue,
    Shorten,
    Restart,
}

internal fun markerRevealTransition(
    previousMark: String,
    mark: String,
): MarkerRevealTransition = when {
    mark.equals(previousMark, ignoreCase = true) -> MarkerRevealTransition.Continue

    previousMark.isNotEmpty() &&
        mark.length > previousMark.length &&
        mark.startsWith(previousMark, ignoreCase = true) -> MarkerRevealTransition.Continue

    mark.length < previousMark.length &&
        previousMark.startsWith(mark, ignoreCase = true) -> MarkerRevealTransition.Shorten

    else -> MarkerRevealTransition.Restart
}

private fun DrawScope.drawMarkerRun(
    layout: TextLayoutResult,
    range: IntRange,
    seed: Int,
    markColor: Color,
    revealedCharacters: Float,
) {
    val revealedEnd = range.first + revealedCharacters.coerceIn(0f, range.count().toFloat())
    val firstLine = layout.getLineForOffset(range.first)
    val lastLine = layout.getLineForOffset(range.last)
    for (line in firstLine..lastLine) {
        val start = maxOf(range.first, layout.getLineStart(line))
        val end = minOf(range.last + 1, layout.getLineEnd(line, visibleEnd = true))
        if (end <= start || revealedEnd <= start) continue

        val startX = layout.getHorizontalPosition(start, usePrimaryDirection = true)
        val revealedX = layout.getRevealedHorizontalPosition(
            revealedEnd = revealedEnd,
            end = end,
        )
        val horizontalScale = if (revealedX >= startX) 1f else -1f
        val width = abs(revealedX - startX)
        if (width <= 0f) continue

        val top = layout.getLineTop(line)
        val bottom = layout.getLineBottom(line)
        val height = (bottom - top).coerceAtMost(SketchMarkedTextDefaults.maxHeight.toPx())
        val centreY = (top + bottom) / 2f

        val pieceSeed = SketchMarkedTextDefaults.SEED_BASE +
            (
                (
                    seed +
                        range.first * SketchMarkedTextDefaults.START_MULTIPLIER +
                        line * SketchMarkedTextDefaults.LINE_MULTIPLIER
                    ).mod(SketchMarkedTextDefaults.SEED_SPREAD)
                )
        val path = sketchMarkerPath(width = width, height = height, seed = pieceSeed)
        translate(left = startX, top = centreY) {
            scale(scaleX = horizontalScale, scaleY = 1f, pivot = Offset.Zero) {
                drawPath(path, markColor)
            }
        }
    }
}

private fun TextLayoutResult.getRevealedHorizontalPosition(
    revealedEnd: Float,
    end: Int,
): Float {
    if (revealedEnd >= end) {
        return getHorizontalPosition(end, usePrimaryDirection = true)
    }

    val offset = floor(revealedEnd).toInt()
    val fraction = revealedEnd - offset
    val currentX = getHorizontalPosition(offset, usePrimaryDirection = true)
    val nextX = getHorizontalPosition(offset + 1, usePrimaryDirection = true)
    return currentX + (nextX - currentX) * fraction
}

private fun String.rangesOf(normalizedMark: String): List<IntRange> {
    if (normalizedMark.isEmpty()) return emptyList()
    val ranges = mutableListOf<IntRange>()
    var from = indexOf(normalizedMark, startIndex = 0, ignoreCase = true)
    while (from >= 0) {
        ranges += from until (from + normalizedMark.length)
        from = indexOf(normalizedMark, startIndex = from + normalizedMark.length, ignoreCase = true)
    }
    return ranges
}

private object SketchMarkedTextDefaults {
    val maxHeight = 17.dp

    internal const val GROWTH_MILLIS = 150

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

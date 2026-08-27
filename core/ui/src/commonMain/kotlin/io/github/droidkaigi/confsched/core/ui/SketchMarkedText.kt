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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.floor

private val MarkerRoughness = 1.15.dp
private val MarkerTremor = 0.32.dp

@Composable
fun SketchMarkedText(
    text: String,
    mark: String,
    seed: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    markColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    markTextColor: Color = MaterialTheme.colorScheme.primary,
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

    val markedText = remember(text, matches, markTextColor) {
        buildAnnotatedString {
            append(text)
            for (range in matches) {
                addStyle(SpanStyle(color = markTextColor), range.first, range.last + 1)
            }
        }
    }
    val combinedSeed = combineSketchSeed(seed)
    val markerRoughness = scaleSketchAmplitude(MarkerRoughness)
    val markerTremor = scaleSketchAmplitude(MarkerTremor)
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    var previousMark by remember(text) { mutableStateOf("") }
    var previousMatches by remember(text) { mutableStateOf(emptyList<IntRange>()) }
    var outgoingMarkers by remember(text) { mutableStateOf(emptyList<OutgoingMarker>()) }
    val revealedCharacters = remember(text) { Animatable(0f) }
    val crossfade = remember(text) { Animatable(1f) }
    LaunchedEffect(text, normalizedMark) {
        val targetCharacterCount = normalizedMark.length.toFloat()

        val transition = markerRevealTransition(
            previousMark = previousMark,
            mark = normalizedMark,
            previousMatchStarts = previousMatches.map { it.first },
            matchStarts = matches.map { it.first },
        )
        when (transition) {
            MarkerRevealTransition.Start -> {
                outgoingMarkers = emptyList()
            }

            // A moved match cannot grow in place, so replace it by crossfading the two shapes.
            MarkerRevealTransition.Restart -> {
                val previousProgress = crossfade.value
                outgoingMarkers = buildList {
                    outgoingMarkers.forEach { marker ->
                        val alpha = marker.alpha * (1f - previousProgress)
                        if (alpha > 0f) add(marker.copy(alpha = alpha))
                    }
                    if (previousProgress > 0f) {
                        add(
                            OutgoingMarker(
                                matches = previousMatches,
                                revealedCharacters = revealedCharacters.value,
                                alpha = previousProgress,
                            ),
                        )
                    }
                }
            }

            MarkerRevealTransition.Shorten,
            MarkerRevealTransition.Continue,
            -> Unit
        }
        revealedCharacters.snapTo(
            markerRevealStart(
                transition = transition,
                targetCharacterCount = targetCharacterCount,
                revealedCharacters = revealedCharacters.value,
            ),
        )
        crossfade.snapTo(markerCrossfadeStart(transition, crossfade.value))
        // Record the mark after resetting so cancellation repeats an interrupted replacement.
        previousMark = normalizedMark
        previousMatches = matches

        coroutineScope {
            if (crossfade.value < 1f) {
                launch {
                    crossfade.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 120),
                    )
                    outgoingMarkers = emptyList()
                }
            }
            revealedCharacters.animateTo(
                targetValue = targetCharacterCount,
                animationSpec = tween(
                    durationMillis = 150,
                    easing = EaseOut,
                ),
            )
        }
    }

    Text(
        text = markedText,
        modifier = modifier.drawBehind {
            val result = layout ?: return@drawBehind
            val revealed = crossfade.value
            outgoingMarkers.forEach { previous ->
                drawMarkerRuns(
                    layout = result,
                    matches = previous.matches,
                    seed = combinedSeed,
                    roughness = markerRoughness,
                    tremor = markerTremor,
                    markColor = markColor.copy(
                        alpha = markColor.alpha * previous.alpha * (1f - revealed),
                    ),
                    revealedCharacters = previous.revealedCharacters,
                )
            }
            drawMarkerRuns(
                layout = result,
                matches = matches,
                seed = combinedSeed,
                roughness = markerRoughness,
                tremor = markerTremor,
                markColor = markColor.copy(alpha = markColor.alpha * revealed),
                revealedCharacters = revealedCharacters.value,
            )
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
    Start,
    Continue,
    Shorten,
    Restart,
}

internal fun markerRevealTransition(
    previousMark: String,
    mark: String,
    previousMatchStarts: List<Int>,
    matchStarts: List<Int>,
): MarkerRevealTransition = when {
    previousMark.isEmpty() -> MarkerRevealTransition.Start

    previousMatchStarts != matchStarts -> MarkerRevealTransition.Restart

    mark.equals(previousMark, ignoreCase = true) -> MarkerRevealTransition.Continue

    previousMark.isNotEmpty() &&
        mark.length > previousMark.length &&
        mark.startsWith(previousMark, ignoreCase = true) -> MarkerRevealTransition.Continue

    mark.length < previousMark.length &&
        previousMark.startsWith(mark, ignoreCase = true) -> MarkerRevealTransition.Shorten

    else -> MarkerRevealTransition.Restart
}

internal fun markerRevealStart(
    transition: MarkerRevealTransition,
    targetCharacterCount: Float,
    revealedCharacters: Float,
): Float = when (transition) {
    MarkerRevealTransition.Start -> 0f
    MarkerRevealTransition.Restart -> targetCharacterCount
    MarkerRevealTransition.Shorten -> minOf(revealedCharacters, targetCharacterCount)
    MarkerRevealTransition.Continue -> revealedCharacters
}

internal fun markerCrossfadeStart(
    transition: MarkerRevealTransition,
    crossfade: Float,
): Float = when (transition) {
    MarkerRevealTransition.Start -> 1f

    MarkerRevealTransition.Restart -> 0f

    MarkerRevealTransition.Shorten,
    MarkerRevealTransition.Continue,
    -> crossfade
}

private data class OutgoingMarker(
    val matches: List<IntRange>,
    val revealedCharacters: Float,
    val alpha: Float,
)

private fun DrawScope.drawMarkerRuns(
    layout: TextLayoutResult,
    matches: List<IntRange>,
    seed: Int,
    roughness: Dp,
    tremor: Dp,
    markColor: Color,
    revealedCharacters: Float,
) {
    if (revealedCharacters <= 0f || markColor.alpha <= 0f) return
    for (range in matches) {
        drawMarkerRun(
            layout = layout,
            range = range,
            seed = seed,
            roughness = roughness,
            tremor = tremor,
            markColor = markColor,
            revealedCharacters = revealedCharacters,
        )
    }
}

private fun DrawScope.drawMarkerRun(
    layout: TextLayoutResult,
    range: IntRange,
    seed: Int,
    roughness: Dp,
    tremor: Dp,
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
        val height = (bottom - top - 1.5.dp.toPx() * 2f)
            .coerceIn(0f, 17.2.dp.toPx())
        val centreY = (top + bottom) / 2f

        val path = sketchMarkerPath(
            width = width,
            height = height,
            startBleed = bleedBefore(layout, start, line),
            endBleed = bleedAfter(layout, end, line),
            seed = markerSeed(seed, range.first, line),
            roughness = roughness,
            tremor = tremor,
        )
        translate(left = startX, top = centreY) {
            scale(scaleX = horizontalScale, scaleY = 1f, pivot = Offset.Zero) {
                drawPath(path, markColor)
            }
        }
    }
}

private fun Density.bleedBefore(layout: TextLayoutResult, offset: Int, line: Int): Float = when {
    offset <= layout.getLineStart(line) -> 3.dp.toPx()

    layout.layoutInput.text[offset - 1].isWhitespace() ->
        (layout.spanWidth(offset - 1, offset) - 1.5.dp.toPx()).coerceAtLeast(0f)

    else -> 0.5.dp.toPx()
}

private fun Density.bleedAfter(layout: TextLayoutResult, offset: Int, line: Int): Float = when {
    offset >= layout.getLineEnd(line, visibleEnd = true) -> 3.dp.toPx()

    layout.layoutInput.text[offset].isWhitespace() ->
        (layout.spanWidth(offset, offset + 1) - 1.5.dp.toPx()).coerceAtLeast(0f)

    else -> 0.5.dp.toPx()
}

private const val MARKER_BASE_SEED = 700
private const val MARKER_SEED_COUNT = 293
private const val MATCH_START_SEED_MULTIPLIER = 37
private const val LINE_SEED_MULTIPLIER = 101

private fun markerSeed(seed: Int, rangeStart: Int, line: Int): Int {
    val mixedSeed = seed + rangeStart * MATCH_START_SEED_MULTIPLIER + line * LINE_SEED_MULTIPLIER
    return MARKER_BASE_SEED + mixedSeed.mod(MARKER_SEED_COUNT)
}

private fun TextLayoutResult.spanWidth(from: Int, to: Int): Float = abs(
    getHorizontalPosition(to, usePrimaryDirection = true) -
        getHorizontalPosition(from, usePrimaryDirection = true),
)

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

package io.github.droidkaigi.confsched.feature.contributors.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.contributors.generated.resources.Res
import io.github.droidkaigi.confsched.feature.contributors.generated.resources.contributors_count_label
import io.github.droidkaigi.confsched.feature.contributors.generated.resources.contributors_count_unit
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

internal const val CONTRIBUTORS_COUNT_TEXT_LABEL_TEST_TAG = "ContributorsCountTextLabelTestTag"
internal const val CONTRIBUTORS_COUNT_TEXT_COUNT_TEST_TAG = "ContributorsCountTextCountTestTag"
internal const val CONTRIBUTORS_COUNT_TEXT_UNIT_TEST_TAG = "ContributorsCountTextUnitTestTag"

@Composable
internal fun ContributorsCountText(
    count: Int,
    modifier: Modifier = Modifier,
) {
    var hasCountedUp by rememberSaveable { mutableStateOf(false) }
    val displayedCount = remember { Animatable(if (hasCountedUp) count else 0, Int.VectorConverter) }
    LaunchedEffect(count) {
        if (hasCountedUp) {
            displayedCount.snapTo(count)
        } else {
            displayedCount.animateTo(
                targetValue = count,
                animationSpec = tween(
                    durationMillis = ContributorsCountTextDefaults.COUNT_UP_DURATION_MILLIS,
                    delayMillis = ContributorsCountTextDefaults.COUNT_UP_START_DELAY_MILLIS,
                    easing = EaseOut,
                ),
            )
            hasCountedUp = true
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // The marks hug the count rather than the block, so they anchor to a box drawn around the
        // text and the block keeps the rest of its height outside them.
        Box(
            modifier = Modifier.padding(
                horizontal = ContributorsCountTextDefaults.blockHorizontalPadding,
                vertical = ContributorsCountTextDefaults.blockVerticalPadding,
            ),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(ContributorsCountTextDefaults.textSpacing),
                modifier = Modifier.padding(
                    horizontal = ContributorsCountTextDefaults.ornamentHorizontalPadding,
                    vertical = ContributorsCountTextDefaults.ornamentVerticalPadding,
                ).semantics(mergeDescendants = true) {},
            ) {
                Text(
                    text = stringResource(Res.string.contributors_count_label),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag(CONTRIBUTORS_COUNT_TEXT_LABEL_TEST_TAG).alignByBaseline(),
                )
                Text(
                    text = displayedCount.value.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFeatureSettings = ContributorsCountTextDefaults.TABULAR_FIGURES_FEATURE,
                    ),
                    modifier = Modifier
                        .testTag(CONTRIBUTORS_COUNT_TEXT_COUNT_TEST_TAG)
                        .width(ContributorsCountTextDefaults.countDigitsWidth)
                        .alignByBaseline(),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = pluralStringResource(Res.plurals.contributors_count_unit, count),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag(CONTRIBUTORS_COUNT_TEXT_UNIT_TEST_TAG).alignByBaseline(),
                )
            }
            OrnamentMark(OrnamentShape.BracketTopStart, Modifier.align(Alignment.TopStart))
            OrnamentMark(OrnamentShape.BracketTopEnd, Modifier.align(Alignment.TopEnd))
            OrnamentMark(OrnamentShape.BracketBottomStart, Modifier.align(Alignment.BottomStart))
            OrnamentMark(OrnamentShape.BracketBottomEnd, Modifier.align(Alignment.BottomEnd))
            OrnamentMark(
                shape = OrnamentShape.SparkLeading,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(
                        x = -ContributorsCountTextDefaults.sparkLeadingInset,
                        y = -ContributorsCountTextDefaults.sparkLeadingRise,
                    ),
            )
            OrnamentMark(
                shape = OrnamentShape.SparkTrailing,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(
                        x = ContributorsCountTextDefaults.sparkTrailingInset,
                        y = ContributorsCountTextDefaults.sparkTrailingDrop,
                    ),
            )
        }
    }
}

@Composable
private fun OrnamentMark(
    shape: OrnamentShape,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val path = remember(shape, shape::path)
    Canvas(modifier = modifier.size(shape.viewport.dp)) {
        val ratio = size.minDimension / shape.viewport
        scale(ratio, ratio, pivot = Offset.Zero) {
            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = ORNAMENT_STROKE_WIDTH,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
    }
}

/**
 * The marks framing the count, each transcribed from the vector the design file exports.
 * Coordinates stay in the square [viewport] the mark was drawn in, which is also the size it
 * is drawn at, and the four brackets differ rather than mirroring so that no two read alike.
 */
private enum class OrnamentShape(val viewport: Float) {
    BracketTopStart(BRACKET_VIEWPORT),
    BracketTopEnd(BRACKET_VIEWPORT),
    BracketBottomStart(BRACKET_VIEWPORT),
    BracketBottomEnd(BRACKET_VIEWPORT),
    SparkLeading(13f),
    SparkTrailing(11f),
    ;

    fun path(): Path = Path().apply {
        when (this@OrnamentShape) {
            BracketTopStart -> {
                moveTo(19.2f, 1.6f)
                cubicTo(13.2f, 0.4f, 7.2f, 2.2f, 1.8f, 1f)
                cubicTo(0.6f, 6.2f, 2.2f, 12.2f, 1f, 19.2f)
            }

            BracketTopEnd -> {
                moveTo(1f, 1f)
                cubicTo(7f, 2.2f, 13f, 0.4f, 18.4f, 1.6f)
                cubicTo(19.6f, 7.2f, 18f, 13.2f, 19.2f, 19.2f)
            }

            BracketBottomStart -> {
                moveTo(1f, 1f)
                cubicTo(2.2f, 7f, 0.4f, 13f, 1.6f, 18.6f)
                cubicTo(7.2f, 19.8f, 13.2f, 18f, 19.2f, 19.2f)
            }

            BracketBottomEnd -> {
                moveTo(1f, 19.2f)
                cubicTo(7f, 18f, 13f, 19.8f, 18.6f, 18.6f)
                cubicTo(19.8f, 13f, 18f, 7f, 19.2f, 1f)
            }

            SparkLeading -> {
                moveTo(6.4f, 1f)
                cubicTo(6.8f, 4.4f, 6.2f, 7.8f, 6.6f, 11.6f)
                moveTo(1f, 6.2f)
                cubicTo(4.4f, 6.6f, 8f, 6f, 11.8f, 6.4f)
            }

            SparkTrailing -> {
                moveTo(5.4f, 1f)
                cubicTo(5.8f, 3.6f, 5.2f, 6.2f, 5.6f, 9.2f)
                moveTo(1f, 5f)
                cubicTo(3.6f, 5.4f, 6.4f, 4.8f, 9.4f, 5.2f)
            }
        }
    }
}

private const val BRACKET_VIEWPORT = 21f
private const val ORNAMENT_STROKE_WIDTH = 2f

private object ContributorsCountTextDefaults {
    val textSpacing = 8.dp

    /** Skips the fade-in's near-invisible first stretch, so the count doesn't move unseen. */
    const val COUNT_UP_START_DELAY_MILLIS = 200

    const val COUNT_UP_DURATION_MILLIS = 600

    /** Tabular figures keep each digit the same width, so the count does not jitter while it animates. */
    const val TABULAR_FIGURES_FEATURE = "tnum"

    /** Wide enough for the total to reach three digits without the surrounding marks shifting. */
    val countDigitsWidth = 48.dp

    /** How far the brackets stand off the count they frame. */
    val ornamentHorizontalPadding = 13.dp
    val ornamentVerticalPadding = 7.dp

    /** What the block keeps around the brackets to reach the size the design gives it. */
    val blockHorizontalPadding = 16.dp
    val blockVerticalPadding = 11.dp

    // Where each spark sits beyond the bracket it accompanies. The two differ in the design.
    val sparkLeadingInset = 13.dp
    val sparkLeadingRise = 7.dp
    val sparkTrailingInset = 16.dp
    val sparkTrailingDrop = 7.dp
}

@LocalePreviews
@Composable
private fun ContributorsCountTextPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ContributorsCountText(count = 71)
    }
}

package io.github.droidkaigi.confsched.core.ui

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.live_badge
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TimetableLiveBadge(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Live badge dot")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = TimetableLiveBadgeDefaults.DOT_MAX_ALPHA,
        targetValue = TimetableLiveBadgeDefaults.DOT_MAX_ALPHA,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = TimetableLiveBadgeDefaults.DOT_BREATH_DURATION_MILLIS
                TimetableLiveBadgeDefaults.DOT_MAX_ALPHA at 0 using EaseInOut
                TimetableLiveBadgeDefaults.DOT_MIN_ALPHA at
                    (TimetableLiveBadgeDefaults.DOT_BREATH_DURATION_MILLIS / 2) using EaseInOut
                TimetableLiveBadgeDefaults.DOT_MAX_ALPHA at
                    TimetableLiveBadgeDefaults.DOT_BREATH_DURATION_MILLIS using EaseInOut
            },
        ),
        label = "Live badge dot alpha",
    )
    val combinedSeed = combineSketchSeed(TimetableLiveBadgeDefaults.SEED)
    val shape = remember(combinedSeed) {
        SketchRoundRectShape(
            seed = combinedSeed,
            roughness = TimetableLiveBadgeDefaults.roughness,
            tremor = TimetableLiveBadgeDefaults.tremor,
            sweepWavelength = TimetableLiveBadgeDefaults.sweepWavelength,
            tremorWavelength = TimetableLiveBadgeDefaults.tremorWavelength,
            cornerRadius = TimetableLiveBadgeDefaults.cornerRadius,
            borderThickness = TimetableLiveBadgeDefaults.borderThickness,
        )
    }
    val primary = MaterialTheme.colorScheme.primary
    Row(
        modifier = modifier
            .size(TimetableLiveBadgeDefaults.width, TimetableLiveBadgeDefaults.height)
            .background(primary, shape)
            .sketchBorder(shape, primary)
            .padding(
                start = TimetableLiveBadgeDefaults.startPadding,
                end = TimetableLiveBadgeDefaults.endPadding,
            ),
        horizontalArrangement = Arrangement.spacedBy(TimetableLiveBadgeDefaults.contentSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(
            Modifier
                .size(TimetableLiveBadgeDefaults.dotSize)
                .graphicsLayer { alpha = dotAlpha }
                .background(MaterialTheme.colorScheme.onPrimary, CircleShape),
        )
        Text(
            text = stringResource(Res.string.live_badge),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = MaterialTheme.typography.displaySmall.fontFamily,
                fontWeight = FontWeight.Normal,
            ),
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

private object TimetableLiveBadgeDefaults {
    const val SEED = 1300
    const val DOT_MIN_ALPHA = 0.35f
    const val DOT_MAX_ALPHA = 1f
    const val DOT_BREATH_DURATION_MILLIS = 1600
    val width = 54.dp
    val height = 22.dp
    val startPadding = 8.dp
    val endPadding = 9.dp
    val contentSpacing = 5.dp
    val dotSize = 4.dp
    val cornerRadius = 11.dp
    val borderThickness = 1.5.dp
    val roughness = 0.4.dp
    val tremor = 0.15.dp
    val sweepWavelength = 140.dp
    val tremorWavelength = 42.dp
}

@LocalePreviews
@Composable
private fun TimetableLiveBadgePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableLiveBadge()
    }
}

package io.github.droidkaigi.confsched.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.loading_description
import io.github.droidkaigi.confsched.core.ui.generated.resources.loading_headline
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LanternLoadingFallback(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LanternLoading")
    val cycleTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "LanternCycleTime",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .wrapContentSize(),
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            // Hanging wire: seed 5320 and 5321, 1.3dp thick, bleed 8dp each side
            val wireModifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    // scaleX to protrude 8dp on each side (16dp total)
                    if (size.width > 0f) {
                        scaleX = (size.width + 16.dp.toPx()) / size.width
                    }
                }

            SketchHorizontalDivider(
                seed = 5320,
                color = MaterialTheme.colorScheme.primary,
                thickness = 1.3.dp,
                sweepWavelength = 200.dp,
                modifier = wireModifier,
            )
            SketchHorizontalDivider(
                seed = 5321,
                color = MaterialTheme.colorScheme.primary,
                thickness = 1.3.dp,
                sweepWavelength = 42.dp,
                modifier = wireModifier,
            )

            Lanterns(cycleTime = cycleTime)
        }

        Spacer(modifier = Modifier.height(75.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(Res.string.loading_headline),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(Res.string.loading_description),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Lanterns(
    cycleTime: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier,
    ) {
        LanternStyle.entries.forEach { style ->
            val litProgress = style.calculateLitProgress(cycleTime)
            Lantern(
                style = style,
                litProgress = litProgress,
            )
        }
    }
}

@LocalePreviews
@Composable
private fun LanternLoadingFallbackPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        LanternLoadingFallback()
    }
}

package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.error_fallback_description
import io.github.droidkaigi.confsched.core.ui.generated.resources.failed_to_load
import io.github.droidkaigi.confsched.core.ui.generated.resources.reload
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ErrorFallback(
    reset: (() -> Unit)?,
    scene: ErrorScene,
    modifier: Modifier = Modifier,
) {
    val panelColor = MaterialTheme.colorScheme.primary
    val panel = remember { PathParser().parsePathString(SCENE_PANEL_PATH).toPath() }
    Column(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                scale(
                    scaleX = size.width / DESIGN_FRAME_WIDTH,
                    scaleY = size.height / DESIGN_FRAME_HEIGHT,
                    pivot = Offset.Zero,
                ) {
                    drawPath(panel, panelColor)
                }
            },
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(SCENE_PANEL_WEIGHT),
        ) {
            Image(
                imageVector = rememberErrorSceneVector(scene),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.79f).aspectRatio(1.01f),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f - SCENE_PANEL_WEIGHT)
                .padding(horizontal = 32.dp)
                // The design's room below the button is where the root tab bar floats, so the
                // cleared height follows the bar's contract rather than the frame's fixed gap.
                .padding(bottom = KaigiNavigationBarDefaults.occupiedHeightWithInset),
        ) {
            Spacer(modifier = Modifier.weight(45f))
            Text(
                text = stringResource(Res.string.failed_to_load),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.weight(27f))
            Text(
                text = stringResource(Res.string.error_fallback_description),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (reset != null) {
                Spacer(modifier = Modifier.weight(45f))
                KaigiButton(
                    onClick = reset,
                    seed = ErrorFallbackDefaults.BUTTON_SEED,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(Res.string.reload),
                        style = KaigiButtonDefaults.labelStyle,
                    )
                }
            }
        }
    }
}

// The design frame the panel path and the zone split are authored against.
private const val DESIGN_FRAME_WIDTH = 412f
private const val DESIGN_FRAME_HEIGHT = 892f
private const val SCENE_PANEL_WEIGHT = 596f / DESIGN_FRAME_HEIGHT

private const val SCENE_PANEL_PATH = "M-8 591.21L-4 590.83L0 591.11L4 591.9L8 593.07L12 594.49L16 596.02L20 597.53L24 598.87L28 599.92L32 600.55L36 600.6L40 599.97L44 598.82L48 597.33L52 595.69L56 594.08L60 592.69L64 591.71L68 591.31L72 591.66L76 592.66L80 594.14L84 595.9L88 597.75L92 599.5L96 600.96L100 601.95L104 602.28L108 601.92L112 601.04L116 599.8L120 598.35L124 596.86L128 595.48L132 594.38L136 593.72L140 593.62L144 594L148 594.73L152 595.68L156 596.71L160 597.7L164 598.51L168 599L172 599.06L176 598.56L180 597.6L184 596.33L188 594.92L192 593.5L196 592.24L200 591.29L204 590.81L208 590.95L212 591.77L216 593.11L220 594.77L224 596.57L228 598.3L232 599.8L236 600.85L240 601.27L244 600.93L248 599.94L252 598.48L256 596.75L260 594.92L264 593.18L268 591.72L272 590.73L276 590.38L280 590.65L284 591.38L288 592.43L292 593.65L296 594.91L300 596.06L304 596.95L308 597.44L312 597.43L316 597.01L320 596.29L324 595.4L328 594.44L332 593.53L336 592.79L340 592.34L344 592.29L348 592.7L352 593.47L356 594.49L360 595.64L364 596.81L368 597.87L372 598.71L376 599.22L380 599.29L384 599L388 598.43L392 597.67L396 596.81L400 595.92L404 595.09L408 594.4L412 593.95L416 593.81L420 594.06V-8H-8V591.21Z"

private object ErrorFallbackDefaults {
    const val BUTTON_SEED = 5340
}

@LocaleScreenPreviews
@Composable
private fun ErrorFallbackPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ErrorFallback(
            reset = {},
            scene = ErrorScene.UnpluggedCable,
        )
    }
}

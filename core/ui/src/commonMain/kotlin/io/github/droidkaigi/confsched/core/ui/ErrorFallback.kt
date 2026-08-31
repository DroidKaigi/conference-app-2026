package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            imageVector = rememberErrorSceneVector(scene),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize(),
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(SCENE_ZONE_HEIGHT))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(DESIGN_FRAME_HEIGHT - SCENE_ZONE_HEIGHT)
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
}

// The 412x892 design frame the scenes are authored against. The text zone starts where every
// scene's frame places the headline block, which is the same across the three scenes.
private const val DESIGN_FRAME_HEIGHT = 892f
private const val SCENE_ZONE_HEIGHT = 596f

private object ErrorFallbackDefaults {
    const val BUTTON_SEED = 5340
}

@LocaleScreenPreviews
@Composable
private fun ErrorFallbackUnpluggedCablePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ErrorFallback(
            reset = {},
            scene = ErrorScene.UnpluggedCable,
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun ErrorFallbackRainPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ErrorFallback(
            reset = {},
            scene = ErrorScene.Rain,
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun ErrorFallbackBackstagePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ErrorFallback(
            reset = {},
            scene = ErrorScene.Backstage,
        )
    }
}

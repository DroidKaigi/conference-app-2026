package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Constraints
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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
internal fun ErrorFallback(
    reset: (() -> Unit)?,
    scene: ErrorScene,
    modifier: Modifier = Modifier,
) {
    Layout(
        content = {
            ErrorSceneArt(scene)
            ErrorFallbackTextZone(reset)
        },
        modifier = modifier.fillMaxSize(),
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        // The text zone keeps its design share of the screen, grows with the font scale, and
        // scrolls once it alone would overflow; the scene takes what remains, sized so its wave
        // edge meets the text zone's top. The scene's size derives from the text's measured
        // height, which constraints alone cannot see — hence a Layout over BoxWithConstraints.
        val textWidth = min(width, TEXT_COLUMN_MAX_WIDTH.roundToPx())
        val text = measurables[1].measure(
            Constraints(minWidth = textWidth, maxWidth = textWidth, maxHeight = height),
        )
        val textZoneHeight = max(text.height, (height * TEXT_ZONE_MIN_FRACTION).roundToInt())
        val sceneZoneHeight = height - textZoneHeight
        val sceneImageHeight = (sceneZoneHeight * DESIGN_FRAME_HEIGHT / SCENE_ZONE_HEIGHT).roundToInt()
        val sceneArt = measurables[0].measure(Constraints.fixed(width, sceneImageHeight))
        layout(width, height) {
            sceneArt.placeRelative(x = 0, y = 0)
            text.placeRelative(x = (width - text.width) / 2, y = sceneZoneHeight)
        }
    }
}

@Composable
private fun ErrorFallbackTextZone(
    reset: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(45.dp))
        Text(
            text = stringResource(Res.string.failed_to_load),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(27.dp))
        Text(
            text = stringResource(Res.string.error_fallback_description),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (reset != null) {
            Spacer(modifier = Modifier.height(45.dp))
            KaigiButton(
                onClick = reset,
                seed = ErrorFallbackDefaults.BUTTON_SEED,
            ) {
                Text(
                    text = stringResource(Res.string.reload),
                    style = KaigiButtonDefaults.labelStyle,
                )
            }
        }
        // The design's room below the button is where the root tab bar floats, so the cleared
        // height follows the bar's contract rather than the frame's fixed gap.
        Spacer(modifier = Modifier.height(KaigiNavigationBarDefaults.occupiedHeightWithInset))
    }
}

// The 412x892 design frame the scenes are authored against. The text zone starts where every
// scene's frame places the headline block, which is the same across the three scenes.
private const val DESIGN_FRAME_HEIGHT = 892f
private const val SCENE_ZONE_HEIGHT = 596f
private const val TEXT_ZONE_MIN_FRACTION = (DESIGN_FRAME_HEIGHT - SCENE_ZONE_HEIGHT) / DESIGN_FRAME_HEIGHT

// The design lays the text section out in a 672 column on wide screens; the phone frame is
// narrower than that, so the cap only binds beyond it.
private val TEXT_COLUMN_MAX_WIDTH = 672.dp

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

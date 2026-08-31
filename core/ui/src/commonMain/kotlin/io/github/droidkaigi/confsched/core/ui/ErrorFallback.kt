package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 32.dp),
    ) {
        Image(
            imageVector = rememberErrorSceneVector(scene),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(0.7f).aspectRatio(1.01f),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(Res.string.failed_to_load),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.error_fallback_description),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        reset?.let {
            Spacer(modifier = Modifier.height(24.dp))
            KaigiButton(
                onClick = it,
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

private object ErrorFallbackDefaults {
    const val BUTTON_SEED = 5340
}

@LocalePreviews
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

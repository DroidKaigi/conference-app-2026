package io.github.droidkaigi.confsched.core.preview.wrapper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.designsystem.KaigiTheme
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.LocalPreviewImageResolver

/**
 * Everything a preview has to render inside: [KaigiTheme] plus the `PreviewImageResolver`, so
 * `RemoteImage` resolves `preview://` URLs to local drawables. [KaigiPreviewWrapper] applies it to
 * previews that take one fixed scheme; a preview that chooses its own scheme calls it directly.
 *
 * The resolver graph is created lazily so production code paths never construct it; its binding
 * lives in `:core:preview:impl`, a compileOnly dependency here, so that module must be present on
 * the classpath that renders previews (the IDE preview classpath, the screenshot-test runtime).
 */
@Composable
fun KaigiPreviewTheme(
    colorScheme: KaigiColorScheme,
    content: @Composable () -> Unit,
) {
    val resolver = remember { createGraph<PreviewGraph>().previewImageResolver }
    CompositionLocalProvider(LocalPreviewImageResolver provides resolver) {
        KaigiTheme(
            colorScheme = colorScheme,
            sketchBaseSeed = 0,
            content = content,
        )
    }
}

package io.github.droidkaigi.confsched.feature.about.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchHorizontalDivider

/** The hand-drawn rule the design draws only between rows, spanning the full section width. */
@Composable
internal fun AboutRowDivider(seed: Int, modifier: Modifier = Modifier) {
    SketchHorizontalDivider(
        seed = seed,
        modifier = modifier,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

@LocalePreviews
@Composable
private fun AboutRowDividerPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutRowDivider(seed = 1)
    }
}

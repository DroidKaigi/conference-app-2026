package io.github.droidkaigi.confsched.core.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme

class KaigiSchemeProvider : PreviewParameterProvider<KaigiColorScheme> {
    override val values: Sequence<KaigiColorScheme> = KaigiColorScheme.entries.asSequence()

    // Recorded screenshots are named after this, so a rename here renames every golden.
    override fun getDisplayName(index: Int): String = KaigiColorScheme.entries[index].name
}

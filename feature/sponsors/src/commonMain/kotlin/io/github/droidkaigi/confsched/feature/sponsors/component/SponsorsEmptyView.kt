package io.github.droidkaigi.confsched.feature.sponsors.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.sponsors.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sponsors.generated.resources.sponsors_empty
import org.jetbrains.compose.resources.stringResource

const val SPONSORS_EMPTY_VIEW_TEST_TAG = "SponsorsEmptyViewTestTag"

@Composable
internal fun SponsorsEmptyView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().testTag(SPONSORS_EMPTY_VIEW_TEST_TAG),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(Res.string.sponsors_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(32.dp),
        )
    }
}

@LocalePreviews
@Composable
private fun SponsorsEmptyViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SponsorsEmptyView()
    }
}

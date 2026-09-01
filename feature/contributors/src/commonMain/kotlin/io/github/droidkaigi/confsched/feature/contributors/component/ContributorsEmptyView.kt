package io.github.droidkaigi.confsched.feature.contributors.component

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
import io.github.droidkaigi.confsched.feature.contributors.generated.resources.Res
import io.github.droidkaigi.confsched.feature.contributors.generated.resources.contributors_empty
import org.jetbrains.compose.resources.stringResource

internal const val CONTRIBUTORS_EMPTY_VIEW_TEST_TAG = "ContributorsEmptyViewTestTag"

@Composable
internal fun ContributorsEmptyView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().testTag(CONTRIBUTORS_EMPTY_VIEW_TEST_TAG),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(Res.string.contributors_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(32.dp),
        )
    }
}

@LocalePreviews
@Composable
private fun ContributorsEmptyViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ContributorsEmptyView()
    }
}

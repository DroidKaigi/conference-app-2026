package io.github.droidkaigi.confsched.feature.search.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.Info
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Search
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.search.generated.resources.Res
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_clear_filters
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_initial_description
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_initial_title
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_no_match_filtered_description
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_no_match_query_description
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_no_match_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SearchStateView(
    uiState: SearchResultUiState.Empty,
    clearFiltersVisible: Boolean,
    onClearFiltersClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SearchStateViewDefaults.spacing),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
        ) {
            Icon(
                imageVector = when (uiState) {
                    SearchResultUiState.Empty.Initial -> KaigiIcons.Default.Search
                    SearchResultUiState.Empty.NoMatch -> KaigiIcons.Default.Info
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(SearchStateViewDefaults.markSize),
            )
            Text(
                text = when (uiState) {
                    SearchResultUiState.Empty.Initial -> stringResource(Res.string.search_initial_title)
                    SearchResultUiState.Empty.NoMatch -> stringResource(Res.string.search_no_match_title)
                },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Text(
                text = when (uiState) {
                    SearchResultUiState.Empty.Initial -> stringResource(Res.string.search_initial_description)

                    SearchResultUiState.Empty.NoMatch -> stringResource(
                        if (clearFiltersVisible) {
                            Res.string.search_no_match_filtered_description
                        } else {
                            Res.string.search_no_match_query_description
                        },
                    )
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (uiState == SearchResultUiState.Empty.NoMatch && clearFiltersVisible) {
                Text(
                    text = stringResource(Res.string.search_clear_filters),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable(role = Role.Button, onClick = onClearFiltersClick),
                )
            }
        }
    }
}

private object SearchStateViewDefaults {
    val markSize = 80.dp
    val spacing = 20.dp
}

@LocalePreviews
@Composable
private fun SearchStateViewInitialPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SearchStateView(
            uiState = SearchResultUiState.Empty.Initial,
            clearFiltersVisible = false,
            onClearFiltersClick = {},
        )
    }
}

@LocalePreviews
@Composable
private fun SearchStateViewNoMatchPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SearchStateView(
            uiState = SearchResultUiState.Empty.NoMatch,
            clearFiltersVisible = true,
            onClearFiltersClick = {},
        )
    }
}

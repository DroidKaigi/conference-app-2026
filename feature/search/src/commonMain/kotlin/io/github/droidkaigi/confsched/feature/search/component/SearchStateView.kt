package io.github.droidkaigi.confsched.feature.search.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.rememberListDetailSceneAwareLazyListState
import io.github.droidkaigi.confsched.feature.search.generated.resources.Res
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_clear_filters
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_initial_constellation_title
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_initial_description
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_initial_rummage_box_title
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_initial_title
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_no_match_description
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_no_match_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal const val SEARCH_STATE_VIEW_INITIAL_TEST_TAG = "SearchStateViewInitialTestTag"
internal const val SEARCH_STATE_VIEW_NO_MATCH_TEST_TAG = "SearchStateViewNoMatchTestTag"
internal const val SEARCH_STATE_VIEW_NO_MATCH_DESCRIPTION_TEST_TAG = "SearchStateViewNoMatchDescriptionTestTag"
internal const val SEARCH_STATE_VIEW_CLEAR_FILTERS_BUTTON_TEST_TAG = "SearchStateViewClearFiltersButtonTestTag"

@Composable
internal fun SearchStateView(
    uiState: SearchResultUiState.Empty,
    clearFiltersVisible: Boolean,
    onClearFiltersClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sceneSelection = rememberSearchSceneSelection()
    val sceneDirection = when (uiState) {
        SearchResultUiState.Empty.Initial -> sceneSelection.initialDirection
        SearchResultUiState.Empty.NoMatch -> sceneSelection.noMatchDirection
    }
    LazyColumn(
        state = rememberListDetailSceneAwareLazyListState(),
        modifier = modifier
            .fillMaxSize()
            .semantics { liveRegion = LiveRegionMode.Polite },
        contentPadding = PaddingValues(
            start = 40.dp,
            end = 40.dp,
            bottom = WindowInsets.safeDrawing
                .exclude(WindowInsets.ime)
                .asPaddingValues()
                .calculateBottomPadding(),
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillParentMaxHeight()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            ) {
                SearchScene(direction = sceneDirection, mascot = sceneSelection.mascot)
                Text(
                    text = stringResource(sceneDirection.titleResource),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag(
                        when (uiState) {
                            SearchResultUiState.Empty.Initial -> SEARCH_STATE_VIEW_INITIAL_TEST_TAG
                            SearchResultUiState.Empty.NoMatch -> SEARCH_STATE_VIEW_NO_MATCH_TEST_TAG
                        },
                    ),
                )
                Text(
                    text = when (uiState) {
                        SearchResultUiState.Empty.Initial -> stringResource(Res.string.search_initial_description)
                        SearchResultUiState.Empty.NoMatch -> stringResource(Res.string.search_no_match_description)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = when (uiState) {
                        SearchResultUiState.Empty.Initial -> Modifier
                        SearchResultUiState.Empty.NoMatch -> Modifier.testTag(SEARCH_STATE_VIEW_NO_MATCH_DESCRIPTION_TEST_TAG)
                    },
                )
                if (uiState == SearchResultUiState.Empty.NoMatch && clearFiltersVisible) {
                    Text(
                        text = stringResource(Res.string.search_clear_filters),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag(SEARCH_STATE_VIEW_CLEAR_FILTERS_BUTTON_TEST_TAG)
                            .clickable(role = Role.Button, onClick = onClearFiltersClick),
                    )
                }
            }
        }
    }
}

private val SearchSceneDirection.titleResource: StringResource
    get() = when (this) {
        SearchSceneDirection.RummageBox -> Res.string.search_initial_rummage_box_title

        SearchSceneDirection.Constellation -> Res.string.search_initial_constellation_title

        SearchSceneDirection.Magnifier -> Res.string.search_initial_title

        SearchSceneDirection.Signpost,
        SearchSceneDirection.EmptyBox,
        -> Res.string.search_no_match_title
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

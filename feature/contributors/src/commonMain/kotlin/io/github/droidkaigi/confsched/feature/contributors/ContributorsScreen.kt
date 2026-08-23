package io.github.droidkaigi.confsched.feature.contributors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiLargeTopAppBar
import io.github.droidkaigi.confsched.core.ui.SketchHorizontalDivider
import io.github.droidkaigi.confsched.feature.contributors.component.ContributorItem
import io.github.droidkaigi.confsched.feature.contributors.component.ContributorItemDefaults
import io.github.droidkaigi.confsched.feature.contributors.component.ContributorsCountText
import io.github.droidkaigi.confsched.feature.contributors.component.ContributorsEmptyView
import io.github.droidkaigi.confsched.feature.contributors.generated.resources.Res
import io.github.droidkaigi.confsched.feature.contributors.generated.resources.contributors
import org.jetbrains.compose.resources.stringResource

@Composable
fun ContributorsScreen(
    uiState: ContributorsScreenUiState,
    onContributorClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            KaigiLargeTopAppBar(title = stringResource(Res.string.contributors), onBackClick = onBackClick)
        },
    ) { innerPadding ->
        if (uiState.contributors.isEmpty()) {
            ContributorsEmptyView(modifier = Modifier.padding(innerPadding))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(ContributorItemDefaults.avatarSize),
                horizontalArrangement = Arrangement.spacedBy(ContributorsScreenDefaults.columnSpacing),
                verticalArrangement = Arrangement.spacedBy(ContributorsScreenDefaults.headerSpacing),
                contentPadding = PaddingValues(
                    start = ContributorsScreenDefaults.gridHorizontalPadding,
                    top = ContributorsScreenDefaults.gridTopPadding,
                    end = ContributorsScreenDefaults.gridHorizontalPadding,
                    bottom = ContributorsScreenDefaults.gridBottomPadding,
                ),
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) {
                item(key = "count", span = { GridItemSpan(maxLineSpan) }) {
                    ContributorsCountText(
                        count = uiState.contributors.size,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                item(key = "divider", span = { GridItemSpan(maxLineSpan) }) {
                    SketchHorizontalDivider(
                        seed = ContributorsScreenDefaults.DIVIDER_SEED,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = ContributorsScreenDefaults.dividerThickness,
                    )
                }

                items(items = uiState.contributors, key = { it.id.value }) { contributor ->
                    ContributorItem(
                        username = contributor.username,
                        iconUrl = contributor.iconUrl,
                        onContributorClick = { onContributorClick(contributor.profileUrl) },
                        modifier = Modifier.padding(bottom = ContributorsScreenDefaults.cellTrailingSpacing),
                    )
                }
            }
        }
    }
}

private object ContributorsScreenDefaults {
    const val DIVIDER_SEED = 1
    val columnSpacing = 32.dp
    val rowSpacing = 32.dp
    val headerSpacing = 20.dp
    val gridHorizontalPadding = 24.dp
    val gridTopPadding = 20.dp
    val gridBottomRun = 72.dp
    val dividerThickness = 1.3.dp

    // A grid spaces every row alike, so the arrangement carries the narrower gap the divider sits
    // in and each cell adds the rest of what a row needs.
    val cellTrailingSpacing = rowSpacing - headerSpacing

    // The last row keeps its own trailing space, which counts towards the run the design leaves
    // below the grid.
    val gridBottomPadding = gridBottomRun - cellTrailingSpacing
}

@LocaleScreenPreviews
@Composable
private fun ContributorsScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ContributorsScreen(
            uiState = ContributorsScreenUiState.fake(),
            onContributorClick = {},
            onBackClick = {},
        )
    }
}

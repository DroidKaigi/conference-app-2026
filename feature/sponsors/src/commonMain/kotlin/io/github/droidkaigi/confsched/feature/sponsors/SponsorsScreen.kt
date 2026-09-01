package io.github.droidkaigi.confsched.feature.sponsors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiLargeTopAppBar
import io.github.droidkaigi.confsched.core.ui.paneStartInset
import io.github.droidkaigi.confsched.core.ui.rememberListDetailSceneAwareLazyGridState
import io.github.droidkaigi.confsched.feature.sponsors.component.SPONSOR_GRID_COLUMNS
import io.github.droidkaigi.confsched.feature.sponsors.component.SponsorsEmptyView
import io.github.droidkaigi.confsched.feature.sponsors.component.sponsorPlanSection
import io.github.droidkaigi.confsched.feature.sponsors.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sponsors.generated.resources.sponsors
import org.jetbrains.compose.resources.stringResource

@Composable
fun SponsorsScreen(
    uiState: SponsorsScreenUiState,
    onSponsorClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    val paneSpacerInset = paneStartInset()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            KaigiLargeTopAppBar(
                title = stringResource(Res.string.sponsors),
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        if (uiState.groups.isEmpty()) {
            SponsorsEmptyView(modifier = Modifier.padding(innerPadding).padding(start = paneSpacerInset))
        } else {
            LazyVerticalGrid(
                state = rememberListDetailSceneAwareLazyGridState(),
                columns = GridCells.Fixed(SPONSOR_GRID_COLUMNS),
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 24.dp + paneSpacerInset,
                    top = 20.dp,
                    end = 24.dp,
                    bottom = 72.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                uiState.groups.forEach { group ->
                    sponsorPlanSection(group = group, onSponsorClick = onSponsorClick)
                }
            }
        }
    }
}

@LocaleScreenPreviews
@Composable
private fun SponsorsScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SponsorsScreen(
            uiState = SponsorsScreenUiState.fake(),
            onSponsorClick = {},
            onBackClick = {},
        )
    }
}

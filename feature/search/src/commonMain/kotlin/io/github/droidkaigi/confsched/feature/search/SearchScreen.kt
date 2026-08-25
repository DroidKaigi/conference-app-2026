package io.github.droidkaigi.confsched.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.SessionType
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.search.component.SearchFilterRow
import io.github.droidkaigi.confsched.feature.search.component.SearchResultSection
import io.github.droidkaigi.confsched.feature.search.component.SearchResultUiState
import io.github.droidkaigi.confsched.feature.search.component.SearchStateView
import io.github.droidkaigi.confsched.feature.search.component.SearchTopBar

@Composable
fun SearchScreen(
    uiState: SearchScreenUiState,
    onQueryTextChange: (String) -> Unit,
    onDayClick: (DroidKaigi2026Day) -> Unit,
    onCategoryClick: (Long) -> Unit,
    onSessionTypeClick: (SessionType) -> Unit,
    onLanguageClick: (Language) -> Unit,
    onClearFiltersClick: () -> Unit,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(contentWindowInsets = WindowInsets()) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).imePadding()) {
            SearchTopBar(
                queryText = uiState.queryText,
                onQueryTextChange = onQueryTextChange,
                onBackClick = onBackClick,
            )
            SearchFilterRow(
                uiState = uiState.filterRow,
                onDayClick = onDayClick,
                onCategoryClick = onCategoryClick,
                onSessionTypeClick = onSessionTypeClick,
                onLanguageClick = onLanguageClick,
            )
            when (val result = uiState.result) {
                is SearchResultUiState.Empty -> SearchStateView(
                    uiState = result,
                    clearFiltersVisible = uiState.hasActiveFilters,
                    onClearFiltersClick = onClearFiltersClick,
                    modifier = Modifier.weight(1f),
                )

                is SearchResultUiState.Found -> SearchResultSection(
                    uiState = result,
                    onBookmarkClick = onBookmarkClick,
                    onItemClick = onItemClick,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@LocaleScreenPreviews
@Composable
private fun SearchScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SearchScreen(
            uiState = SearchScreenUiState.fake(),
            onQueryTextChange = {},
            onDayClick = {},
            onCategoryClick = {},
            onSessionTypeClick = {},
            onLanguageClick = {},
            onClearFiltersClick = {},
            onBookmarkClick = {},
            onItemClick = {},
            onBackClick = {},
        )
    }
}

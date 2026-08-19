package io.github.droidkaigi.confsched.feature.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.feature.favorites.component.FavoriteDayFilterRow
import io.github.droidkaigi.confsched.feature.favorites.component.FavoritesEmptyView
import io.github.droidkaigi.confsched.feature.favorites.component.FavoritesListSection
import io.github.droidkaigi.confsched.feature.favorites.component.FavoritesListSectionUiState
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.Res
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.favorites
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun FavoritesScreen(
    uiState: FavoritesScreenUiState,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onDayFilterClick: (DroidKaigi2026Day?) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
) {
    Scaffold(
        topBar = {
            KaigiTopAppBar(title = stringResource(Res.string.favorites))
        },
        contentWindowInsets = WindowInsets(),
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            FavoriteDayFilterRow(selectedDayFilter = uiState.selectedDayFilter, onDayFilterClick = onDayFilterClick)

            if (uiState.favoritesListSection.timeSlots.isEmpty()) {
                FavoritesEmptyView(modifier = Modifier.weight(1f))
            } else {
                FavoritesListSection(
                    modifier = Modifier.weight(1f),
                    uiState = uiState.favoritesListSection,
                    onBookmarkClick = onBookmarkClick,
                    onItemClick = onItemClick,
                )
            }
        }
    }
}

@LocaleScreenPreviews
@Composable
private fun FavoritesScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FavoritesScreen(
            uiState = FavoritesScreenUiState.fake(),
            onBookmarkClick = {},
            onDayFilterClick = {},
            onItemClick = {},
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun FavoritesScreenEmptyPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FavoritesScreen(
            uiState = FavoritesScreenUiState.fake().copy(favoritesListSection = FavoritesListSectionUiState(timeSlots = persistentListOf())),
            onBookmarkClick = {},
            onDayFilterClick = {},
            onItemClick = {},
        )
    }
}

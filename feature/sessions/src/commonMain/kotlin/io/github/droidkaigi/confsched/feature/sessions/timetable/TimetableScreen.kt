package io.github.droidkaigi.confsched.feature.sessions.timetable

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
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableHeader
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableListSection

@Composable
fun TimetableScreen(
    uiState: TimetableScreenUiState,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onDayClick: (DroidKaigi2026Day) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
    onSearchClick: () -> Unit,
    onUiTypeChangeClick: () -> Unit,
) {
    Scaffold(contentWindowInsets = WindowInsets()) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TimetableHeader(
                selectedDay = uiState.day,
                onDayClick = onDayClick,
                onSearchClick = onSearchClick,
                onUiTypeChangeClick = onUiTypeChangeClick,
            )
            TimetableListSection(
                uiState = uiState.timetableListSection,
                onBookmarkClick = onBookmarkClick,
                onItemClick = onItemClick,
            )
        }
    }
}

@LocaleScreenPreviews
@Composable
private fun TimetableScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableScreen(
            uiState = TimetableScreenUiState.fake(),
            onBookmarkClick = {},
            onDayClick = {},
            onItemClick = {},
            onSearchClick = {},
            onUiTypeChangeClick = {},
        )
    }
}

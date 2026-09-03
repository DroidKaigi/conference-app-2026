package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.CollapsingHeaderLayout
import io.github.droidkaigi.confsched.core.ui.rememberCollapsingHeaderEnterAlwaysState
import io.github.droidkaigi.confsched.core.ui.rememberListDetailSceneAwareLazyListState
import io.github.droidkaigi.confsched.core.ui.rememberListDetailSceneAwarePagerState
import io.github.droidkaigi.confsched.core.ui.rememberPagerPosition
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.DayTabRow
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableGridSection
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableHeader
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableListSection
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@Composable
fun TimetableScreen(
    uiState: TimetableScreenUiState,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onDayClick: (DroidKaigi2026Day) -> Unit,
    onItemClick: (TimetableItemId) -> Unit,
    onSearchClick: () -> Unit,
    onUiTypeChangeClick: () -> Unit,
) {
    val collapsingHeaderState = rememberCollapsingHeaderEnterAlwaysState()
    val pagerPosition = rememberPagerPosition(initialPage = uiState.day.ordinal)
    val pagerState = rememberListDetailSceneAwarePagerState(
        position = pagerPosition,
        pageCount = { DroidKaigi2026Day.entries.size },
    )
    val coroutineScope = rememberCoroutineScope()
    val latestSemanticDay by rememberUpdatedState(uiState.day)
    val latestOnDayClick by rememberUpdatedState(onDayClick)
    val latestPagerState by rememberUpdatedState(pagerState)

    LaunchedEffect(pagerState, uiState.viewMode) {
        if (uiState.viewMode != TimetableViewMode.List) return@LaunchedEffect
        snapshotFlow { pagerState.settledPage }
            .drop(1)
            .collect { page ->
                pagerPosition.currentPage = page
                val day = DroidKaigi2026Day.entries[page]
                if (day != latestSemanticDay) latestOnDayClick(day)
            }
    }

    LaunchedEffect(uiState.day, uiState.viewMode) {
        if (uiState.viewMode != TimetableViewMode.List) return@LaunchedEffect
        val targetPage = uiState.day.ordinal
        pagerPosition.currentPage = targetPage
        if (latestPagerState.currentPage != targetPage) {
            latestPagerState.requestScrollToPage(targetPage)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            TimetableHeader(
                viewMode = uiState.viewMode,
                onSearchClick = onSearchClick,
                onUiTypeChangeClick = onUiTypeChangeClick,
            )
        },
        contentWindowInsets = WindowInsets(),
    ) { innerPadding ->
        CollapsingHeaderLayout(
            state = collapsingHeaderState,
            headerContent = {
                DayTabRow(
                    selectedDay = uiState.day,
                    onDayClick = { day ->
                        when (uiState.viewMode) {
                            TimetableViewMode.List -> {
                                val targetPage = day.ordinal
                                pagerPosition.currentPage = targetPage
                                if (pagerState.isScrollInProgress) {
                                    pagerState.requestScrollToPage(targetPage)
                                } else {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(targetPage)
                                    }
                                }
                            }

                            TimetableViewMode.Grid -> {
                                pagerPosition.currentPage = day.ordinal
                                onDayClick(day)
                            }
                        }
                    },
                )
            },
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) { contentPadding ->
            when (uiState.viewMode) {
                TimetableViewMode.List -> HorizontalPager(
                    state = pagerState,
                    key = { page -> DroidKaigi2026Day.entries[page].name },
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    val day = DroidKaigi2026Day.entries[page]
                    TimetableListSection(
                        uiState = requireNotNull(uiState.timetableListSections[day]),
                        contentPadding = contentPadding,
                        onBookmarkClick = onBookmarkClick,
                        onItemClick = { itemId ->
                            pagerPosition.currentPage = page
                            onItemClick(itemId)
                        },
                        listState = rememberListDetailSceneAwareLazyListState(),
                    )
                }

                TimetableViewMode.Grid -> Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
                    TimetableGridSection(
                        uiState = uiState.timetableGridSection,
                        onItemClick = { itemId ->
                            pagerPosition.currentPage = uiState.day.ordinal
                            onItemClick(itemId)
                        },
                    )
                }
            }
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

@LocaleScreenPreviews
@Composable
private fun TimetableScreenGridPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableScreen(
            uiState = TimetableScreenUiState.fake().copy(viewMode = TimetableViewMode.Grid),
            onBookmarkClick = {},
            onDayClick = {},
            onItemClick = {},
            onSearchClick = {},
            onUiTypeChangeClick = {},
        )
    }
}

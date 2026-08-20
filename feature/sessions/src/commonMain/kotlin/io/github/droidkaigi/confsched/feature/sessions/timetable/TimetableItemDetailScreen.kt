package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.Close
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBarBackButton
import io.github.droidkaigi.confsched.core.ui.LanguageToggleButton
import io.github.droidkaigi.confsched.core.ui.LocalPanePartitionSpacerSize
import io.github.droidkaigi.confsched.core.ui.SketchHorizontalDivider
import io.github.droidkaigi.confsched.core.ui.currentDisplayLanguage
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.close
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.SameSlotSessionsSection
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.SessionArchiveSection
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.SessionCancelledBanner
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.SessionDescriptionSection
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.SessionDetailToolbar
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.SessionHeaderView
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.SessionInfoCard
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.SessionMemoField
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.SessionTargetAudienceSection
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun TimetableItemDetailScreen(
    uiState: TimetableItemDetailScreenUiState,
    onBookmarkClick: (TimetableItemId) -> Unit,
    onDescriptionToggleClick: () -> Unit,
    onLanguageToggleClick: () -> Unit,
    onMemoCommit: (String) -> Unit,
    onArchiveClick: (String) -> Unit,
    onCalendarClick: () -> Unit,
    onShareClick: () -> Unit,
    onSessionClick: (TimetableItemId) -> Unit,
    onBack: () -> Unit,
) {
    val item = uiState.item
    val language = uiState.displayLanguage
    val isListDetailPane = LocalListDetailSceneScope.current != null
    val paneSpacerInset = if (isListDetailPane) LocalPanePartitionSpacerSize.current else 0.dp
    Scaffold(
        topBar = {
            KaigiTopAppBar(
                title = "",
                navigationIcon = {
                    if (isListDetailPane) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.padding(start = paneSpacerInset),
                        ) {
                            Icon(KaigiIcons.Default.Close, contentDescription = stringResource(Res.string.close))
                        }
                    } else {
                        KaigiTopAppBarBackButton(onClick = onBack)
                    }
                },
                // The header below carries the same background, so the two read as one surface.
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            ) {
                LanguageToggleButton(language = language, onClick = onLanguageToggleClick)
            }
        },
        floatingActionButton = {
            SessionDetailToolbar(
                isFavorite = uiState.isFavorite,
                isCancelled = item.isCancelled,
                onCalendarClick = onCalendarClick,
                onShareClick = onShareClick,
                onBookmarkClick = { onBookmarkClick(item.id) },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = TimetableItemDetailScreenDefaults.toolbarClearance),
        ) {
            if (item.isCancelled) {
                item { SessionCancelledBanner(startInset = paneSpacerInset) }
            }
            item {
                SessionHeaderView(
                    room = item.room,
                    title = item.title.of(language),
                    language = item.language,
                    hasInterpretation = item.hasInterpretation,
                    isCancelled = item.isCancelled,
                    speakers = item.speakers,
                    seed = TimetableItemDetailScreenDefaults.HEADER_SEED,
                    startInset = paneSpacerInset,
                )
            }
            item {
                SessionInfoCard(
                    day = item.day,
                    startsAt = item.startsAt,
                    endsAt = item.endsAt,
                    room = item.room,
                    language = item.language,
                    hasInterpretation = item.hasInterpretation,
                    category = item.category?.of(language),
                    seed = TimetableItemDetailScreenDefaults.INFO_CARD_SEED,
                    modifier = Modifier
                        .padding(start = paneSpacerInset)
                        .padding(
                            start = TimetableItemDetailScreenDefaults.contentInset,
                            end = TimetableItemDetailScreenDefaults.contentInset,
                            top = 20.dp,
                        ),
                )
            }
            if (!item.asset.isEmpty) {
                item {
                    SessionArchiveSection(
                        asset = item.asset,
                        seed = TimetableItemDetailScreenDefaults.ARCHIVE_CARD_SEED,
                        onArchiveClick = onArchiveClick,
                        modifier = Modifier
                            .padding(start = paneSpacerInset)
                            .padding(TimetableItemDetailScreenDefaults.sectionPadding),
                    )
                }
            }
            item {
                SessionDescriptionSection(
                    description = item.description.of(language),
                    isExpanded = uiState.isDescriptionExpanded,
                    seed = TimetableItemDetailScreenDefaults.SHOW_MORE_SEED,
                    onToggleClick = onDescriptionToggleClick,
                    modifier = Modifier
                        .padding(start = paneSpacerInset)
                        .padding(TimetableItemDetailScreenDefaults.sectionPadding),
                )
            }
            item {
                SessionTargetAudienceSection(
                    targetAudience = item.targetAudience.of(language),
                    modifier = Modifier
                        .padding(start = paneSpacerInset)
                        .padding(TimetableItemDetailScreenDefaults.sectionPadding),
                )
            }
            item {
                SessionMemoField(
                    memo = uiState.memo,
                    seed = TimetableItemDetailScreenDefaults.MEMO_SEED,
                    onMemoCommit = onMemoCommit,
                    modifier = Modifier
                        .padding(start = paneSpacerInset)
                        .padding(TimetableItemDetailScreenDefaults.sectionPadding),
                )
            }
            if (uiState.sameSlotItems.isNotEmpty()) {
                item {
                    SketchHorizontalDivider(
                        seed = TimetableItemDetailScreenDefaults.DIVIDER_SEED,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = paneSpacerInset)
                            .padding(
                                horizontal = TimetableItemDetailScreenDefaults.contentInset,
                                vertical = 12.dp,
                            ),
                    )
                }
                item {
                    SameSlotSessionsSection(
                        items = uiState.sameSlotItems,
                        bookmarks = uiState.bookmarks,
                        displayLanguage = language,
                        onBookmarkClick = onBookmarkClick,
                        onItemClick = onSessionClick,
                        modifier = Modifier
                            .padding(start = paneSpacerInset)
                            .padding(
                                start = TimetableItemDetailScreenDefaults.contentInset,
                                end = TimetableItemDetailScreenDefaults.contentInset,
                                bottom = 24.dp,
                            ),
                    )
                }
            }
        }
    }
}

// Seeds the design pins in its spec note, so a render matches the hand-drawn borders it shows.
private object TimetableItemDetailScreenDefaults {
    const val HEADER_SEED = 600
    const val INFO_CARD_SEED = 610
    const val ARCHIVE_CARD_SEED = 1301
    const val SHOW_MORE_SEED = 1201
    const val MEMO_SEED = 640
    const val DIVIDER_SEED = 672

    val contentInset = 24.dp
    val sectionPadding = PaddingValues(horizontal = contentInset, vertical = 12.dp)
    val toolbarClearance = 88.dp
}

@LocaleScreenPreviews
@Composable
private fun TimetableItemDetailScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableItemDetailScreen(
            uiState = TimetableItemDetailScreenUiState.fake(isCancelled = false, displayLanguage = currentDisplayLanguage()),
            onBookmarkClick = {},
            onDescriptionToggleClick = {},
            onLanguageToggleClick = {},
            onMemoCommit = {},
            onArchiveClick = {},
            onCalendarClick = {},
            onShareClick = {},
            onSessionClick = {},
            onBack = {},
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun TimetableItemDetailScreenCancelledPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableItemDetailScreen(
            uiState = TimetableItemDetailScreenUiState.fake(isCancelled = true, displayLanguage = currentDisplayLanguage()),
            onBookmarkClick = {},
            onDescriptionToggleClick = {},
            onLanguageToggleClick = {},
            onMemoCommit = {},
            onArchiveClick = {},
            onCalendarClick = {},
            onShareClick = {},
            onSessionClick = {},
            onBack = {},
        )
    }
}

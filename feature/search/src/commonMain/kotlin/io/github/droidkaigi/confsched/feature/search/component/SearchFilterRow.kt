package io.github.droidkaigi.confsched.feature.search.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.ArrowDropDown
import io.github.droidkaigi.confsched.core.designsystem.icon.Check
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.SessionType
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiFilterChip
import io.github.droidkaigi.confsched.core.ui.current
import io.github.droidkaigi.confsched.feature.search.generated.resources.Res
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_filter_category
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_filter_date
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_filter_language
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_filter_session_type
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_filter_with_count
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.stringResource

/**
 * The four filters narrowing a search, each a chip opening the options it offers.
 *
 * A single-pick chip shows the value picked in place of its label; a multi-pick chip keeps its
 * label and counts what is picked.
 */
@Composable
internal fun SearchFilterRow(
    uiState: SearchFilterRowUiState,
    onDayClick: (DroidKaigi2026Day?) -> Unit,
    onCategoryClick: (Long) -> Unit,
    onSessionTypeClick: (SessionType) -> Unit,
    onLanguageClick: (Language) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(SearchFilterRowDefaults.spacing),
    ) {
        SearchFilterChip(
            label = uiState.selectedDay?.label ?: stringResource(Res.string.search_filter_date),
            selected = uiState.selectedDay != null,
            seed = SearchFilterRowDefaults.FIRST_SEED,
        ) { dismiss ->
            for (day in DroidKaigi2026Day.entries) {
                FilterMenuItem(label = day.label, selected = uiState.selectedDay == day) {
                    // Picking the day already in effect clears it, so a single-pick chip needs no
                    // "all" option of its own.
                    onDayClick(day.takeIf { it != uiState.selectedDay })
                    dismiss()
                }
            }
        }

        SearchFilterChip(
            label = countedLabel(stringResource(Res.string.search_filter_category), uiState.selectedCategoryIds.size),
            selected = uiState.selectedCategoryIds.isNotEmpty(),
            seed = SearchFilterRowDefaults.FIRST_SEED + 1,
        ) {
            for (category in uiState.categories) {
                FilterMenuItem(
                    label = category.name.current(),
                    selected = category.id in uiState.selectedCategoryIds,
                ) { onCategoryClick(category.id) }
            }
        }

        SearchFilterChip(
            label = countedLabel(
                stringResource(Res.string.search_filter_session_type),
                uiState.selectedSessionTypes.size,
            ),
            selected = uiState.selectedSessionTypes.isNotEmpty(),
            seed = SearchFilterRowDefaults.FIRST_SEED + 2,
        ) {
            for (sessionType in uiState.sessionTypes) {
                FilterMenuItem(
                    label = stringResource(sessionType.labelResource()),
                    selected = sessionType in uiState.selectedSessionTypes,
                ) { onSessionTypeClick(sessionType) }
            }
        }

        SearchFilterChip(
            label = countedLabel(stringResource(Res.string.search_filter_language), uiState.selectedLanguages.size),
            selected = uiState.selectedLanguages.isNotEmpty(),
            seed = SearchFilterRowDefaults.FIRST_SEED + 3,
        ) {
            for (language in Language.entries) {
                FilterMenuItem(
                    label = stringResource(language.labelResource()),
                    selected = language in uiState.selectedLanguages,
                ) { onLanguageClick(language) }
            }
        }
    }
}

/**
 * One filter: a chip carrying what is picked, and the options it opens.
 *
 * The caret turns while the menu is open, so a chip reads as a control that opens rather than one
 * that toggles.
 */
@Composable
private fun SearchFilterChip(
    label: String,
    selected: Boolean,
    seed: Int,
    menuContent: @Composable (dismiss: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        KaigiFilterChip(
            selected = selected,
            onClick = { expanded = true },
            label = label,
            seed = seed,
        ) {
            Icon(
                imageVector = KaigiIcons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .size(SearchFilterRowDefaults.caretSize)
                    .rotate(if (expanded) SearchFilterRowDefaults.CARET_OPEN_ROTATION else 0f),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            menuContent { expanded = false }
        }
    }
}

/** One option of a filter, ticked while it is in effect. A multi-pick menu stays open on a pick. */
@Composable
private fun FilterMenuItem(label: String, selected: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(label) },
        onClick = onClick,
        leadingIcon = {
            if (selected) {
                Icon(imageVector = KaigiIcons.Default.Check, contentDescription = null)
            }
        },
    )
}

/** A multi-pick chip keeps its label and appends what is picked, as `Category 2`. */
@Composable
private fun countedLabel(label: String, count: Int): String =
    if (count == 0) label else stringResource(Res.string.search_filter_with_count, label, count)

private object SearchFilterRowDefaults {
    val spacing = 8.dp
    val caretSize = 13.dp

    const val CARET_OPEN_ROTATION = 180f

    // The favorites day filter draws its row from 821; a different start keeps these pills from
    // repeating those outlines.
    const val FIRST_SEED = 841
}

@LocalePreviews
@Composable
private fun SearchFilterRowPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SearchFilterRow(
            uiState = SearchFilterRowUiState(
                selectedDay = null,
                categories = Timetable.fake().categories,
                selectedCategoryIds = persistentSetOf(),
                sessionTypes = persistentListOf(SessionType.NORMAL, SessionType.CODELABS),
                selectedSessionTypes = persistentSetOf(),
                selectedLanguages = persistentSetOf(),
            ),
            onDayClick = {},
            onCategoryClick = {},
            onSessionTypeClick = {},
            onLanguageClick = {},
        )
    }
}

@LocalePreviews
@Composable
private fun SearchFilterRowPickedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SearchFilterRow(
            uiState = SearchFilterRowUiState(
                selectedDay = DroidKaigi2026Day.Day1,
                categories = Timetable.fake().categories,
                selectedCategoryIds = persistentSetOf(11L, 12L),
                sessionTypes = persistentListOf(SessionType.NORMAL, SessionType.CODELABS),
                selectedSessionTypes = persistentSetOf(SessionType.CODELABS),
                selectedLanguages = persistentSetOf(Language.ENGLISH),
            ),
            onDayClick = {},
            onCategoryClick = {},
            onSessionTypeClick = {},
            onLanguageClick = {},
        )
    }
}

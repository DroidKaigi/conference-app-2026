package io.github.droidkaigi.confsched.feature.search.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
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
import io.github.droidkaigi.confsched.core.ui.SketchDefaults
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.current
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.search.generated.resources.Res
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_filter_category
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_filter_collapsed
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_filter_date
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_filter_expanded
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_filter_language
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_filter_session_type
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_filter_with_count
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.stringResource

internal const val SEARCH_FILTER_DAY_CHIP_TEST_TAG = "SearchFilterDayChipTestTag"
internal const val SEARCH_FILTER_CATEGORY_CHIP_TEST_TAG = "SearchFilterCategoryChipTestTag"
internal const val SEARCH_FILTER_SESSION_TYPE_CHIP_TEST_TAG = "SearchFilterSessionTypeChipTestTag"
internal const val SEARCH_FILTER_LANGUAGE_CHIP_TEST_TAG = "SearchFilterLanguageChipTestTag"

internal fun searchFilterDayOptionTestTag(day: DroidKaigi2026Day) = "SearchFilterDayOption:${day.name}"

internal fun searchFilterCategoryOptionTestTag(categoryId: Long) = "SearchFilterCategoryOption:$categoryId"

internal fun searchFilterSessionTypeOptionTestTag(sessionType: SessionType) = "SearchFilterSessionTypeOption:${sessionType.name}"

internal fun searchFilterLanguageOptionTestTag(language: Language) = "SearchFilterLanguageOption:${language.name}"

@Composable
internal fun SearchFilterRow(
    uiState: SearchFilterRowUiState,
    onDayClick: (DroidKaigi2026Day) -> Unit,
    onCategoryClick: (Long) -> Unit,
    onSessionTypeClick: (SessionType) -> Unit,
    onLanguageClick: (Language) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SearchFilterChip(
            label = uiState.selectedDay?.label ?: stringResource(Res.string.search_filter_date),
            selected = uiState.selectedDay != null,
            seed = 841,
            modifier = Modifier.testTag(SEARCH_FILTER_DAY_CHIP_TEST_TAG),
        ) { dismiss ->
            for (day in DroidKaigi2026Day.entries) {
                FilterMenuItem(
                    label = day.label,
                    selected = uiState.selectedDay == day,
                    modifier = Modifier.testTag(searchFilterDayOptionTestTag(day)),
                ) {
                    onDayClick(day)
                    dismiss()
                }
            }
        }

        SearchFilterChip(
            label = countedLabel(stringResource(Res.string.search_filter_category), uiState.selectedCategoryIds.size),
            selected = uiState.selectedCategoryIds.isNotEmpty(),
            seed = 842,
            modifier = Modifier.testTag(SEARCH_FILTER_CATEGORY_CHIP_TEST_TAG),
        ) {
            for (category in uiState.categories) {
                FilterMenuItem(
                    label = category.name.current(),
                    selected = category.id in uiState.selectedCategoryIds,
                    modifier = Modifier.testTag(searchFilterCategoryOptionTestTag(category.id)),
                ) { onCategoryClick(category.id) }
            }
        }

        SearchFilterChip(
            label = countedLabel(
                stringResource(Res.string.search_filter_session_type),
                uiState.selectedSessionTypes.size,
            ),
            selected = uiState.selectedSessionTypes.isNotEmpty(),
            seed = 843,
            modifier = Modifier.testTag(SEARCH_FILTER_SESSION_TYPE_CHIP_TEST_TAG),
        ) {
            for (sessionType in uiState.sessionTypes) {
                FilterMenuItem(
                    label = stringResource(sessionType.labelResource()),
                    selected = sessionType in uiState.selectedSessionTypes,
                    modifier = Modifier.testTag(searchFilterSessionTypeOptionTestTag(sessionType)),
                ) { onSessionTypeClick(sessionType) }
            }
        }

        SearchFilterChip(
            label = countedLabel(stringResource(Res.string.search_filter_language), uiState.selectedLanguages.size),
            selected = uiState.selectedLanguages.isNotEmpty(),
            seed = 844,
            modifier = Modifier.testTag(SEARCH_FILTER_LANGUAGE_CHIP_TEST_TAG),
        ) {
            for (language in Language.entries) {
                FilterMenuItem(
                    label = stringResource(language.labelResource()),
                    selected = language in uiState.selectedLanguages,
                    modifier = Modifier.testTag(searchFilterLanguageOptionTestTag(language)),
                ) { onLanguageClick(language) }
            }
        }
    }
}

@Composable
private fun SearchFilterChip(
    label: String,
    selected: Boolean,
    seed: Int,
    modifier: Modifier = Modifier,
    menuContent: @Composable (dismiss: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val expansionState = stringResource(
        if (expanded) Res.string.search_filter_expanded else Res.string.search_filter_collapsed,
    )
    val menuBorderThickness = 2.dp
    val menuShape = SketchRoundRectShape(
        seed = combineSketchSeed(seed),
        roughness = SketchDefaults.roughness,
        tremor = SketchDefaults.tremor,
        cornerRadius = 24.dp,
        borderThickness = menuBorderThickness,
    )
    Box(modifier = modifier) {
        KaigiFilterChip(
            selected = selected,
            onClick = {
                focusManager.clearFocus()
                expanded = !expanded
            },
            label = label,
            seed = seed,
            role = Role.Button,
            modifier = Modifier.semantics { stateDescription = expansionState },
        ) {
            Icon(
                imageVector = KaigiIcons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .size(13.dp)
                    .rotate(if (expanded) 180f else 0f),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.sketchBorder(menuShape, MaterialTheme.colorScheme.outlineVariant),
            shape = menuShape,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier
                    .width(FilterMenuDefaults.width)
                    .padding(
                        horizontal = FilterMenuDefaults.horizontalPadding,
                        vertical = FilterMenuDefaults.verticalPadding - DropdownMenuVerticalPadding,
                    ),
                verticalArrangement = Arrangement.spacedBy(FilterMenuDefaults.itemGap),
            ) {
                menuContent { expanded = false }
            }
        }
    }
}

@Composable
private fun FilterMenuItem(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = FilterMenuDefaults.itemHeight)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(FilterMenuDefaults.itemPadding)
            .semantics { this.selected = selected },
        horizontalArrangement = Arrangement.spacedBy(FilterMenuDefaults.tickGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selected) {
            Icon(
                imageVector = KaigiIcons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(FilterMenuDefaults.tickSize),
            )
        } else {
            Spacer(modifier = Modifier.size(FilterMenuDefaults.tickSize))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.SemiBold else null,
        )
    }
}

/** Vertical inset [DropdownMenu] adds around its content, which it offers no parameter to change. */
private val DropdownMenuVerticalPadding = 8.dp

private object FilterMenuDefaults {
    val width = 228.dp
    val horizontalPadding = 12.dp
    val verticalPadding = 10.dp
    val itemGap = 4.dp
    val itemHeight = 32.dp
    val itemPadding = 6.dp
    val tickGap = 8.dp
    val tickSize = 20.dp
}

@Composable
private fun countedLabel(label: String, count: Int): String =
    if (count == 0) label else stringResource(Res.string.search_filter_with_count, label, count)

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

package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.designsystem.icon.GridView
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Search
import io.github.droidkaigi.confsched.core.designsystem.icon.ViewTimeline
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiIconButton
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.search
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.switch_to_grid_view
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.switch_to_list_view
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.timetable
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableViewMode
import org.jetbrains.compose.resources.stringResource

const val TIMETABLE_HEADER_SEARCH_BUTTON_TEST_TAG = "TimetableHeaderSearchButtonTestTag"
const val TIMETABLE_HEADER_GRID_VIEW_BUTTON_TEST_TAG = "TimetableHeaderGridViewButtonTestTag"
const val TIMETABLE_HEADER_LIST_VIEW_BUTTON_TEST_TAG = "TimetableHeaderListViewButtonTestTag"

/** The title and the two actions, on the dark band the day picker continues below them. */
@Composable
internal fun TimetableHeader(
    viewMode: TimetableViewMode,
    onSearchClick: () -> Unit,
    onUiTypeChangeClick: () -> Unit,
) {
    KaigiTopAppBar(title = stringResource(Res.string.timetable)) {
        KaigiIconButton(
            seed = 777,
            onClick = onSearchClick,
            modifier = Modifier.testTag(TIMETABLE_HEADER_SEARCH_BUTTON_TEST_TAG),
        ) {
            Icon(KaigiIcons.Default.Search, contentDescription = stringResource(Res.string.search))
        }
        val viewModeTestTag = when (viewMode) {
            TimetableViewMode.List -> TIMETABLE_HEADER_GRID_VIEW_BUTTON_TEST_TAG
            TimetableViewMode.Grid -> TIMETABLE_HEADER_LIST_VIEW_BUTTON_TEST_TAG
        }
        KaigiIconButton(
            seed = 778,
            onClick = onUiTypeChangeClick,
            modifier = Modifier.testTag(viewModeTestTag),
        ) {
            val (icon, description) = when (viewMode) {
                TimetableViewMode.List -> KaigiIcons.Default.GridView to Res.string.switch_to_grid_view
                TimetableViewMode.Grid -> KaigiIcons.Default.ViewTimeline to Res.string.switch_to_list_view
            }
            Icon(icon, contentDescription = stringResource(description))
        }
    }
}

@LocalePreviews
@Composable
private fun TimetableHeaderPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableHeader(viewMode = TimetableViewMode.List, onSearchClick = {}, onUiTypeChangeClick = {})
    }
}

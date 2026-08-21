package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.designsystem.icon.GridView
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Search
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiIconButton
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.search
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.switch_to_grid_view
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.timetable
import org.jetbrains.compose.resources.stringResource

/** The title, the two actions and the day picker, all on the one dark band. */
@Composable
internal fun TimetableHeader(
    selectedDay: DroidKaigi2026Day,
    onDayClick: (DroidKaigi2026Day) -> Unit,
    onSearchClick: () -> Unit,
    onUiTypeChangeClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.inverseSurface)) {
        KaigiTopAppBar(title = stringResource(Res.string.timetable)) {
            KaigiIconButton(seed = 777, onClick = onSearchClick) {
                Icon(KaigiIcons.Default.Search, contentDescription = stringResource(Res.string.search))
            }
            KaigiIconButton(seed = 778, onClick = onUiTypeChangeClick) {
                Icon(KaigiIcons.Default.GridView, contentDescription = stringResource(Res.string.switch_to_grid_view))
            }
        }
        DayTabRow(selectedDay = selectedDay, onDayClick = onDayClick)
    }
}

@LocalePreviews
@Composable
private fun TimetableHeaderPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableHeader(
            selectedDay = DroidKaigi2026Day.Day1,
            onDayClick = {},
            onSearchClick = {},
            onUiTypeChangeClick = {},
        )
    }
}

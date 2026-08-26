package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.designsystem.icon.GridView
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Search
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

/** The title and the two actions, on the dark band the day picker continues below them. */
@Composable
internal fun TimetableHeader(
    onSearchClick: () -> Unit,
    onUiTypeChangeClick: () -> Unit,
) {
    KaigiTopAppBar(title = stringResource(Res.string.timetable)) {
        KaigiIconButton(seed = 777, onClick = onSearchClick) {
            Icon(KaigiIcons.Default.Search, contentDescription = stringResource(Res.string.search))
        }
        KaigiIconButton(seed = 778, onClick = onUiTypeChangeClick) {
            Icon(KaigiIcons.Default.GridView, contentDescription = stringResource(Res.string.switch_to_grid_view))
        }
    }
}

@LocalePreviews
@Composable
private fun TimetableHeaderPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableHeader(
            onSearchClick = {},
            onUiTypeChangeClick = {},
        )
    }
}

package io.github.droidkaigi.confsched.feature.staff

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiLargeTopAppBar
import io.github.droidkaigi.confsched.core.ui.paneStartInset
import io.github.droidkaigi.confsched.core.ui.rememberListDetailSceneAwareLazyGridState
import io.github.droidkaigi.confsched.feature.staff.component.StaffItem
import io.github.droidkaigi.confsched.feature.staff.component.StaffItemDefaults
import io.github.droidkaigi.confsched.feature.staff.generated.resources.Res
import io.github.droidkaigi.confsched.feature.staff.generated.resources.staff
import org.jetbrains.compose.resources.stringResource

@Composable
fun StaffScreen(
    uiState: StaffScreenUiState,
    onBackClick: () -> Unit,
    onStaffClick: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            KaigiLargeTopAppBar(title = stringResource(Res.string.staff), onBackClick = onBackClick)
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            state = rememberListDetailSceneAwareLazyGridState(),
            columns = GridCells.Adaptive(StaffItemDefaults.avatarSize),
            horizontalArrangement = Arrangement.spacedBy(StaffScreenDefaults.cellSpacing),
            verticalArrangement = Arrangement.spacedBy(StaffScreenDefaults.cellSpacing),
            contentPadding = PaddingValues(
                start = StaffScreenDefaults.gridPadding + paneStartInset(),
                top = StaffScreenDefaults.gridPadding,
                end = StaffScreenDefaults.gridPadding,
                bottom = StaffScreenDefaults.gridPadding,
            ),
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            items(items = uiState.staff, key = { it.id.value }) { staff ->
                StaffItem(
                    username = staff.username,
                    iconUrl = staff.iconUrl,
                    onStaffClick = { onStaffClick(staff.profileUrl) },
                )
            }
        }
    }
}

private object StaffScreenDefaults {
    val cellSpacing = 32.dp
    val gridPadding = 24.dp
}

@LocaleScreenPreviews
@Composable
private fun StaffScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        StaffScreen(
            uiState = StaffScreenUiState.fake(),
            onBackClick = {},
            onStaffClick = {},
        )
    }
}

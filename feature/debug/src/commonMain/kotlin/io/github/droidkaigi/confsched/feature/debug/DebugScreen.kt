package io.github.droidkaigi.confsched.feature.debug

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.designsystem.icon.ArrowBack
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.SCREEN_PREVIEW_HEIGHT_DP
import io.github.droidkaigi.confsched.core.preview.SCREEN_PREVIEW_WIDTH_DP
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(
    uiState: DebugScreenUiState,
    onSoilErrorOverlayEnabledChange: (Boolean) -> Unit,
    onClockPresetClick: (DebugClockPreset) -> Unit,
    onClockShiftClick: (String) -> Unit,
    onClockResetClick: () -> Unit,
    onClockOverlayEnabledChange: (Boolean) -> Unit,
    onSoilErrorsClick: () -> Unit,
    onClearDataClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Debug menu") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(KaigiIcons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            SectionHeader("App")
            ListItem(
                headlineContent = { Text("Version") },
                trailingContent = { Text(uiState.appVersion) },
            )
            HorizontalDivider()

            SectionHeader("Clock")
            DebugClockSection(
                uiState = uiState.clock,
                onClockPresetClick = onClockPresetClick,
                onClockShiftClick = onClockShiftClick,
                onClockResetClick = onClockResetClick,
                onClockOverlayEnabledChange = onClockOverlayEnabledChange,
            )
            HorizontalDivider()

            SectionHeader("Soil")
            ListItem(
                headlineContent = { Text("Show Soil error sheet") },
                supportingContent = { Text("Pop up a bottom sheet whenever a query, mutation, or subscription fails") },
                trailingContent = {
                    Switch(
                        checked = uiState.soilErrorOverlayEnabled,
                        onCheckedChange = onSoilErrorOverlayEnabledChange,
                    )
                },
            )
            ListItem(
                modifier = Modifier.clickable(onClick = onSoilErrorsClick),
                headlineContent = { Text("Soil errors") },
                supportingContent = { Text("Errors relayed during this session") },
                trailingContent = { Text("${uiState.soilErrors.size}") },
            )
            HorizontalDivider()

            SectionHeader("Data")
            ListItem(
                modifier = Modifier.clickable(onClick = onClearDataClick),
                headlineContent = { Text("Clear persisted data") },
                supportingContent = {
                    Text(
                        if (uiState.dataCleared) {
                            "Persisted data cleared ✓"
                        } else {
                            "Removes settings, favorites, and cached responses"
                        },
                    )
                },
            )
        }
    }
}

@Preview(widthDp = SCREEN_PREVIEW_WIDTH_DP, heightDp = SCREEN_PREVIEW_HEIGHT_DP)
@Composable
private fun DebugScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DebugScreen(
            uiState = DebugScreenUiState(
                appVersion = "0.1.0",
                dataCleared = false,
                soilErrorOverlayEnabled = true,
                soilErrors = listOf(SoilError.fake()),
                clock = DebugClockUiState.fake(),
            ),
            onSoilErrorOverlayEnabledChange = {},
            onClockPresetClick = {},
            onClockShiftClick = {},
            onClockResetClick = {},
            onClockOverlayEnabledChange = {},
            onSoilErrorsClick = {},
            onClearDataClick = {},
            onBackClick = {},
        )
    }
}

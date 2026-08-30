package io.github.droidkaigi.confsched.feature.debug

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.runtime.getValue
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
import io.github.droidkaigi.confsched.core.ui.LocalDeviceTiltSource
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(
    uiState: DebugScreenUiState,
    toggleSoilErrorOverlay: (Boolean) -> Unit,
    applyClockPreset: (DebugClockPreset) -> Unit,
    shiftClockTo: (String) -> Unit,
    resetClock: () -> Unit,
    toggleClockOverlay: (Boolean) -> Unit,
    onOpenSoilErrors: () -> Unit,
    onClearData: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text("Debug menu") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                applyClockPreset = applyClockPreset,
                shiftClockTo = shiftClockTo,
                resetClock = resetClock,
                toggleClockOverlay = toggleClockOverlay,
            )
            HorizontalDivider()

            SectionHeader("Device tilt")
            val tilt by LocalDeviceTiltSource.current.tiltAsState()
            ListItem(
                headlineContent = { Text("Pitch") },
                supportingContent = { Text("Top edge toward the ground is positive") },
                trailingContent = { Text(formatDegrees(tilt.pitchDegrees)) },
            )
            ListItem(
                headlineContent = { Text("Roll") },
                supportingContent = { Text("Left edge toward the ground is positive") },
                trailingContent = { Text(formatDegrees(tilt.rollDegrees)) },
            )
            HorizontalDivider()

            SectionHeader("Soil")
            ListItem(
                headlineContent = { Text("Show Soil error sheet") },
                supportingContent = { Text("Pop up a bottom sheet whenever a query, mutation, or subscription fails") },
                trailingContent = {
                    Switch(
                        checked = uiState.soilErrorOverlayEnabled,
                        onCheckedChange = toggleSoilErrorOverlay,
                    )
                },
            )
            ListItem(
                modifier = Modifier.clickable(onClick = onOpenSoilErrors),
                headlineContent = { Text("Soil errors") },
                supportingContent = { Text("Errors relayed during this session") },
                trailingContent = { Text("${uiState.soilErrors.size}") },
            )
            HorizontalDivider()

            SectionHeader("Data")
            ListItem(
                modifier = Modifier.clickable(onClick = onClearData),
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

// Adding zero turns -0.0 into 0.0, which a device near level would otherwise flicker between.
private fun formatDegrees(degrees: Float): String = "${round(degrees * 10f) / 10f + 0f}°"

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
            toggleSoilErrorOverlay = {},
            applyClockPreset = {},
            shiftClockTo = {},
            resetClock = {},
            toggleClockOverlay = {},
            onOpenSoilErrors = {},
            onClearData = {},
            onBack = {},
        )
    }
}

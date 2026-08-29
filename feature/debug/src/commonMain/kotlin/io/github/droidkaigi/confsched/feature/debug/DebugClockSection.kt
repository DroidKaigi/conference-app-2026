package io.github.droidkaigi.confsched.feature.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

@Composable
internal fun ColumnScope.DebugClockSection(
    uiState: DebugClockUiState,
    applyClockPreset: (DebugClockPreset) -> Unit,
    shiftClockTo: (String) -> Unit,
    resetClock: () -> Unit,
    toggleClockOverlay: (Boolean) -> Unit,
) {
    var isoInstant by retain { mutableStateOf("") }

    ListItem(
        headlineContent = { Text("Now") },
        supportingContent = { Text(uiState.now) },
        trailingContent = { Text(uiState.offsetLabel) },
    )
    ListItem(
        headlineContent = { Text("Show the shifted time on every screen") },
        supportingContent = { Text("A badge in the top-right corner, while the clock is shifted") },
        trailingContent = {
            Switch(
                checked = uiState.overlayEnabled,
                onCheckedChange = toggleClockOverlay,
            )
        },
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DebugClockPreset.entries.forEach { preset ->
            SuggestionChip(
                onClick = { applyClockPreset(preset) },
                label = { Text(preset.label) },
            )
        }
    }
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = isoInstant,
            onValueChange = { isoInstant = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Shift to instant") },
            placeholder = { Text("2026-09-02T10:00:00+09:00") },
            supportingText = { Text(if (uiState.invalidInput) "Not an ISO-8601 instant" else "The clock keeps ticking from the instant you set") },
            isError = uiState.invalidInput,
            singleLine = true,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { shiftClockTo(isoInstant) }) {
                Text("Apply")
            }
            TextButton(onClick = resetClock, enabled = uiState.shifted) {
                Text("Back to system time")
            }
        }
    }
}

@Preview
@Composable
private fun DebugClockSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Column {
            DebugClockSection(
                uiState = DebugClockUiState.fake(),
                applyClockPreset = {},
                shiftClockTo = {},
                resetClock = {},
                toggleClockOverlay = {},
            )
        }
    }
}

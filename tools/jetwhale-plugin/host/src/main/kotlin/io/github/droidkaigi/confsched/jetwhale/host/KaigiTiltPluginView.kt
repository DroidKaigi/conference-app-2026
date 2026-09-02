package io.github.droidkaigi.confsched.jetwhale.host

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.jetwhale.protocol.KaigiTiltState

@Composable
internal fun KaigiTiltPluginView(
    state: KaigiTiltState?,
    onSet: (pitchDegrees: Float, rollDegrees: Float) -> Unit,
    onReset: () -> Unit,
) {
    // Keyed on the state so a reply re-seeds the sliders; the agent replies with the values it
    // applied, so a re-seed lands on what the last drag chose.
    var pitch by remember(state) { mutableFloatStateOf(state?.pitchDegrees ?: 0f) }
    var roll by remember(state) { mutableFloatStateOf(state?.rollDegrees ?: 0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = when {
                state == null -> "Waiting for the app…"

                state.overridden ->
                    "Tilt pinned at ${formatTiltDegrees(state.pitchDegrees)} pitch, ${formatTiltDegrees(state.rollDegrees)} roll"

                else -> "Tilt following the device sensor"
            },
            style = MaterialTheme.typography.titleMedium,
        )
        TiltSlider(
            label = "Pitch",
            value = pitch,
            valueRange = -90f..90f,
            enabled = state != null,
            onValueChange = { pitch = it },
            onValueChangeFinished = { onSet(pitch, roll) },
        )
        TiltSlider(
            label = "Roll",
            value = roll,
            valueRange = -180f..180f,
            enabled = state != null,
            onValueChange = { roll = it },
            onValueChangeFinished = { onSet(pitch, roll) },
        )
        TextButton(
            onClick = onReset,
            enabled = state?.overridden == true,
        ) {
            Text("Back to the device sensor")
        }
    }
}

@Composable
private fun TiltSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(formatTiltDegrees(value), style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            enabled = enabled,
            onValueChangeFinished = onValueChangeFinished,
        )
    }
}

private fun formatTiltDegrees(degrees: Float): String = "%.0f°".format(degrees)

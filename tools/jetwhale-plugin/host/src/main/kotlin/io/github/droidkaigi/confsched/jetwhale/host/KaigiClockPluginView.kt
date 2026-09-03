package io.github.droidkaigi.confsched.jetwhale.host

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.jetwhale.protocol.KaigiClockState
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

@Composable
internal fun KaigiClockPluginView(
    state: KaigiClockState?,
    onShiftTo: (Long) -> Unit,
    onReset: () -> Unit,
    onRefresh: () -> Unit,
) {
    var input by remember { mutableStateOf("") }
    var invalidInput by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = state?.let { formatConferenceTime(it.nowEpochMillis) } ?: "Waiting for the app…",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = state?.let { offsetLabel(it.offsetMillis) } ?: "",
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state?.presets.orEmpty().forEach { preset ->
                Button(
                    onClick = {
                        invalidInput = false
                        onShiftTo(preset.epochMillis)
                    },
                ) {
                    Text(preset.label)
                }
            }
        }
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Shift to instant") },
            placeholder = { Text("2026-09-02T10:00:00+09:00") },
            supportingText = { Text(if (invalidInput) "Not an ISO-8601 instant" else "The app keeps ticking from the instant you set") },
            isError = invalidInput,
            singleLine = true,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val target = Instant.parseOrNull(input)
                    invalidInput = target == null
                    if (target != null) {
                        onShiftTo(target.toEpochMilliseconds())
                    }
                },
            ) {
                Text("Apply")
            }
            TextButton(
                onClick = {
                    invalidInput = false
                    onReset()
                },
                enabled = state != null && state.offsetMillis != 0L,
            ) {
                Text("Back to system time")
            }
            TextButton(onClick = onRefresh) {
                Text("Refresh")
            }
        }
    }
}

private val conferenceTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.ofHours(9))

private fun formatConferenceTime(epochMillis: Long): String =
    "${conferenceTimeFormatter.format(java.time.Instant.ofEpochMilli(epochMillis))} JST"

private fun offsetLabel(offsetMillis: Long): String = when {
    offsetMillis == 0L -> "System time"
    offsetMillis > 0L -> "+${offsetMillis.milliseconds}"
    else -> offsetMillis.milliseconds.toString()
}

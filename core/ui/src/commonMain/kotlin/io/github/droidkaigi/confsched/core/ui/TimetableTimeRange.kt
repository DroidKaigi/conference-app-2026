package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

/**
 * When a slot runs, drawn down the left of the sessions in it.
 *
 * The two times sit either side of a wavy rule standing in for the stretch between them.
 */
@Composable
fun TimetableTimeRange(
    startsAt: String,
    endsAt: String,
    timeRangeState: TimetableLineState,
    seed: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(TimetableTimeRangeDefaults.width),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = startsAt,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        // Delegate the line drawing logic to a separate private function
        ProgressWavyLine(
            state = timeRangeState,
            seed = seed,
            modifier = Modifier.height(TimetableTimeRangeDefaults.ruleHeight),
        )
        Text(
            text = endsAt,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ProgressWavyLine(
    state: TimetableLineState,
    seed: Int,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is TimetableLineState.Upcoming -> {
            SketchVerticalWavyLine(
                seed = seed,
                modifier = modifier.fillMaxHeight(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                thickness = 1.dp,
            )
        }

        is TimetableLineState.InProgress -> {
            SketchVerticalWavyProgressLine(
                seed = seed,
                progress = state.progress,
                modifier = modifier.fillMaxHeight(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                passedThickness = 2.5.dp,
                upcomingThickness = 1.dp,
            )
        }

        is TimetableLineState.Passed -> {
            SketchVerticalWavyLine(
                seed = seed,
                modifier = modifier.fillMaxHeight(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                thickness = 2.5.dp,
            )
        }
    }
}

private object TimetableTimeRangeDefaults {
    val width = 56.dp
    val ruleHeight = 40.dp
}

@Preview
@Composable
private fun TimetableTimeRangePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TimetableTimeRange(
                startsAt = "10:00",
                endsAt = "10:20",
                timeRangeState = TimetableLineState.Passed,
                seed = 20,
            )
            TimetableTimeRange(
                startsAt = "10:00",
                endsAt = "10:20",
                timeRangeState = TimetableLineState.InProgress(0.5f),
                seed = 20,
            )
            TimetableTimeRange(
                startsAt = "10:00",
                endsAt = "10:20",
                timeRangeState = TimetableLineState.Upcoming,
                seed = 20,
            )
        }
    }
}

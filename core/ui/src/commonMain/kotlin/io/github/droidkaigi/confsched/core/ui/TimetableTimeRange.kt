package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
        SketchVerticalWavyLine(
            seed = seed,
            modifier = Modifier.height(TimetableTimeRangeDefaults.ruleHeight),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            thickness = 2.dp,
        )
        Text(
            text = endsAt,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
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
        TimetableTimeRange(startsAt = "10:00", endsAt = "10:20", seed = 20)
    }
}

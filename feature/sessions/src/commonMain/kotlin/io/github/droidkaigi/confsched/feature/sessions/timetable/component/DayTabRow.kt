package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.Check
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiSegmentedButton
import io.github.droidkaigi.confsched.core.ui.KaigiSingleChoiceSegmentedButtonRow

@Composable
internal fun DayTabRow(
    selectedDay: DroidKaigi2026Day,
    onDayClick: (DroidKaigi2026Day) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.inverseSurface)
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        KaigiSingleChoiceSegmentedButtonRow(outlineSeed = DayTabRowDefaults.OUTLINE_SEED) {
            DroidKaigi2026Day.entries.forEachIndexed { index, day ->
                KaigiSegmentedButton(
                    selected = selectedDay == day,
                    onClick = { onDayClick(day) },
                    dividerSeed = DayTabRowDefaults.DIVIDER_SEED.takeIf {
                        index < DroidKaigi2026Day.entries.lastIndex
                    },
                    leadingDividerSeed = DayTabRowDefaults.DIVIDER_SEED.takeIf { index > 0 },
                ) {
                    DayLabel(day = day, selected = selectedDay == day)
                }
            }
        }
    }
}

@Composable
private fun DayLabel(day: DroidKaigi2026Day, selected: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selected) {
            Icon(
                imageVector = KaigiIcons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(text = day.label, style = MaterialTheme.typography.labelLarge)
    }
}

private object DayTabRowDefaults {
    const val OUTLINE_SEED = 900
    const val DIVIDER_SEED = 902
}

@Preview
@Composable
private fun DayTabRowPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DayTabRow(selectedDay = DroidKaigi2026Day.Day1, onDayClick = {})
    }
}

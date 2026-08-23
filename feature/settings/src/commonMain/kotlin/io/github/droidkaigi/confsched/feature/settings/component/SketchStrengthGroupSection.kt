package io.github.droidkaigi.confsched.feature.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.SketchStrength
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.settings.generated.resources.Res
import io.github.droidkaigi.confsched.feature.settings.generated.resources.hand_drawn_strength
import io.github.droidkaigi.confsched.feature.settings.generated.resources.hand_drawn_strength_normal
import io.github.droidkaigi.confsched.feature.settings.generated.resources.hand_drawn_strength_playful
import io.github.droidkaigi.confsched.feature.settings.generated.resources.hand_drawn_strength_subtle
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SketchStrengthGroupSection(
    sketchStrength: SketchStrength,
    onSketchStrengthClick: (SketchStrength) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsGroupSection(title = stringResource(Res.string.hand_drawn_strength), modifier = modifier) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SketchStrength.entries.forEachIndexed { index, entry ->
                SketchStrengthOptionItem(
                    sketchStrength = entry,
                    label = stringResource(entry.label),
                    selected = entry == sketchStrength,
                    seed = SketchStrengthGroupDefaults.FIRST_OPTION_SEED + index,
                    onClick = { onSketchStrengthClick(entry) },
                )
            }
        }
    }
}

private val SketchStrength.label: StringResource
    get() = when (this) {
        SketchStrength.Subtle -> Res.string.hand_drawn_strength_subtle
        SketchStrength.Normal -> Res.string.hand_drawn_strength_normal
        SketchStrength.Playful -> Res.string.hand_drawn_strength_playful
    }

private object SketchStrengthGroupDefaults {
    const val FIRST_OPTION_SEED = 5820
}

@LocalePreviews
@Composable
private fun SketchStrengthGroupSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SketchStrengthGroupSection(
            sketchStrength = SketchStrength.Normal,
            onSketchStrengthClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

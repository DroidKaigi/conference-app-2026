package io.github.droidkaigi.confsched.feature.settings.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.ColorSchemeSetting
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.settings.generated.resources.Res
import io.github.droidkaigi.confsched.feature.settings.generated.resources.theme
import io.github.droidkaigi.confsched.feature.settings.generated.resources.theme_campfire_night
import io.github.droidkaigi.confsched.feature.settings.generated.resources.theme_deep_teal
import io.github.droidkaigi.confsched.feature.settings.generated.resources.theme_morning_mist
import io.github.droidkaigi.confsched.feature.settings.generated.resources.theme_random
import io.github.droidkaigi.confsched.feature.settings.generated.resources.theme_sakura_plum
import io.github.droidkaigi.confsched.feature.settings.generated.resources.theme_terracotta
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColorSchemeGroupSection(
    colorSchemeSetting: ColorSchemeSetting,
    onColorSchemeSettingClick: (ColorSchemeSetting) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsGroupSection(title = stringResource(Res.string.theme), modifier = modifier) {
        SwatchBoardView(seed = ColorSchemeGroupDefaults.BOARD_SEED) {
            RandomColorSchemeSwatchItem(
                label = stringResource(Res.string.theme_random),
                selected = colorSchemeSetting == ColorSchemeSetting.RandomPerLaunch,
                seed = ColorSchemeGroupDefaults.FIRST_SWATCH_SEED,
                onClick = { onColorSchemeSettingClick(ColorSchemeSetting.RandomPerLaunch) },
            )
            KaigiColorScheme.entries.forEachIndexed { index, entry ->
                ColorSchemeSwatchItem(
                    colorScheme = entry,
                    label = stringResource(entry.label),
                    selected = colorSchemeSetting == ColorSchemeSetting.Fixed(entry),
                    seed = ColorSchemeGroupDefaults.FIRST_SWATCH_SEED + index + 1,
                    onClick = { onColorSchemeSettingClick(ColorSchemeSetting.Fixed(entry)) },
                )
            }
        }
    }
}

private val KaigiColorScheme.label: StringResource
    get() = when (this) {
        KaigiColorScheme.MorningMist -> Res.string.theme_morning_mist
        KaigiColorScheme.DeepTeal -> Res.string.theme_deep_teal
        KaigiColorScheme.SakuraPlum -> Res.string.theme_sakura_plum
        KaigiColorScheme.Terracotta -> Res.string.theme_terracotta
        KaigiColorScheme.CampfireNight -> Res.string.theme_campfire_night
    }

private object ColorSchemeGroupDefaults {
    const val BOARD_SEED = 5830

    /** The random option takes this one; each scheme takes the next in entry order. */
    const val FIRST_SWATCH_SEED = 5840
}

@LocalePreviews
@Composable
private fun ColorSchemeGroupSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ColorSchemeGroupSection(
            colorSchemeSetting = ColorSchemeSetting.RandomPerLaunch,
            onColorSchemeSettingClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

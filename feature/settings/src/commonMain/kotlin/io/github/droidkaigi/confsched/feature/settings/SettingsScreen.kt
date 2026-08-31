package io.github.droidkaigi.confsched.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.ColorSchemeSetting
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.KaigiFontFamily
import io.github.droidkaigi.confsched.core.model.SketchStrength
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiLargeTopAppBar
import io.github.droidkaigi.confsched.core.ui.paneStartInset
import io.github.droidkaigi.confsched.feature.settings.component.ColorSchemeGroupSection
import io.github.droidkaigi.confsched.feature.settings.component.FontFamilyGroupSection
import io.github.droidkaigi.confsched.feature.settings.component.SketchStrengthGroupSection
import io.github.droidkaigi.confsched.feature.settings.generated.resources.Res
import io.github.droidkaigi.confsched.feature.settings.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    uiState: SettingsScreenUiState,
    onFontFamilyClick: (KaigiFontFamily) -> Unit,
    onSketchStrengthClick: (SketchStrength) -> Unit,
    onColorSchemeSettingClick: (ColorSchemeSetting) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            KaigiLargeTopAppBar(title = stringResource(Res.string.settings_title), onBackClick = onBackClick)
        },
        // Zero here so the bottom inset lands in the list's content padding and the groups
        // scroll under the system bar; the bar applies the top inset itself.
        contentWindowInsets = WindowInsets(),
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(
                start = SettingsScreenDefaults.horizontalPadding + paneStartInset(),
                top = SettingsScreenDefaults.verticalPadding,
                end = SettingsScreenDefaults.horizontalPadding,
                bottom = SettingsScreenDefaults.verticalPadding +
                    WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding(),
            ),
            verticalArrangement = Arrangement.spacedBy(SettingsScreenDefaults.groupSpacing),
        ) {
            item(key = "font") {
                FontFamilyGroupSection(
                    fontFamily = uiState.fontFamily,
                    onFontFamilyClick = onFontFamilyClick,
                )
            }
            item(key = "strength") {
                SketchStrengthGroupSection(
                    sketchStrength = uiState.sketchStrength,
                    onSketchStrengthClick = onSketchStrengthClick,
                )
            }
            item(key = "theme") {
                ColorSchemeGroupSection(
                    colorSchemeSetting = uiState.colorSchemeSetting,
                    onColorSchemeSettingClick = onColorSchemeSettingClick,
                )
            }
        }
    }
}

private object SettingsScreenDefaults {
    val horizontalPadding = 24.dp
    val verticalPadding = 24.dp
    val groupSpacing = 24.dp
}

@LocaleScreenPreviews
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SettingsScreen(
            uiState = SettingsScreenUiState(
                fontFamily = KaigiFontFamily.Default,
                sketchStrength = SketchStrength.Normal,
                colorSchemeSetting = ColorSchemeSetting.RandomPerLaunch,
            ),
            onFontFamilyClick = {},
            onSketchStrengthClick = {},
            onColorSchemeSettingClick = {},
            onBackClick = {},
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun SettingsScreenWithFixedColorSchemePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SettingsScreen(
            uiState = SettingsScreenUiState(
                fontFamily = KaigiFontFamily.CourierPrime,
                sketchStrength = SketchStrength.Playful,
                colorSchemeSetting = ColorSchemeSetting.Fixed(colorScheme),
            ),
            onFontFamilyClick = {},
            onSketchStrengthClick = {},
            onColorSchemeSettingClick = {},
            onBackClick = {},
        )
    }
}

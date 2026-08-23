package io.github.droidkaigi.confsched.feature.settings

import io.github.droidkaigi.confsched.core.model.ColorSchemeSetting
import io.github.droidkaigi.confsched.core.model.KaigiFontFamily
import io.github.droidkaigi.confsched.core.model.SketchStrength

data class SettingsScreenUiState(
    val fontFamily: KaigiFontFamily,
    val sketchStrength: SketchStrength,
    val colorSchemeSetting: ColorSchemeSetting,
)

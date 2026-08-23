package io.github.droidkaigi.confsched.feature.settings

import io.github.droidkaigi.confsched.core.model.ColorSchemeSetting
import io.github.droidkaigi.confsched.core.model.KaigiFontFamily
import io.github.droidkaigi.confsched.core.model.SketchStrength

sealed interface SettingsScreenAction {
    data class SelectFont(val fontFamily: KaigiFontFamily) : SettingsScreenAction

    data class SelectSketchStrength(val sketchStrength: SketchStrength) : SettingsScreenAction

    data class SelectColorScheme(val colorSchemeSetting: ColorSchemeSetting) : SettingsScreenAction
}

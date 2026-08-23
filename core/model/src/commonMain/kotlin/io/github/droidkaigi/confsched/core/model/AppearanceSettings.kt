package io.github.droidkaigi.confsched.core.model

data class AppearanceSettings(
    val colorSchemeSetting: ColorSchemeSetting,
    val fontFamily: KaigiFontFamily,
    val sketchStrength: SketchStrength,
) {
    companion object {
        val Default = AppearanceSettings(
            colorSchemeSetting = ColorSchemeSetting.RandomPerLaunch,
            fontFamily = KaigiFontFamily.Default,
            sketchStrength = SketchStrength.Normal,
        )
    }
}

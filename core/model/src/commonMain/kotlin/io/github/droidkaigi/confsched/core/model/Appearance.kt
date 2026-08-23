package io.github.droidkaigi.confsched.core.model

/**
 * The appearance the app draws with: the settings as chosen, and the scheme they come out as.
 *
 * The two differ where [AppearanceSettings.colorSchemeSetting] is [ColorSchemeSetting.RandomPerLaunch],
 * which names no scheme until a launch draws one.
 */
data class Appearance(
    val colorScheme: KaigiColorScheme,
    val settings: AppearanceSettings,
)

package io.github.droidkaigi.confsched.core.model

sealed interface ColorSchemeSetting {
    data class Fixed(val colorScheme: KaigiColorScheme) : ColorSchemeSetting

    data object RandomPerLaunch : ColorSchemeSetting
}

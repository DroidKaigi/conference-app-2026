package io.github.droidkaigi.confsched.feature.debug

sealed interface DebugScreenAction {
    data object ClearData : DebugScreenAction

    data class SetSoilErrorOverlayEnabled(val enabled: Boolean) : DebugScreenAction

    data class ApplyClockPreset(val preset: DebugClockPreset) : DebugScreenAction

    data class ShiftClockTo(val isoInstant: String) : DebugScreenAction

    data object ResetClock : DebugScreenAction

    data class SetClockOverlayEnabled(val enabled: Boolean) : DebugScreenAction
}

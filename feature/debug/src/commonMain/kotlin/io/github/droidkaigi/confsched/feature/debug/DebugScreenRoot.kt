package io.github.droidkaigi.confsched.feature.debug

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel

@Composable
context(screenContext: DebugScreenContext)
fun DebugScreenRoot(
    onNavigateToSoilErrors: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val screenChannel = retainScreenChannel<DebugScreenAction, DebugScreenActionResult>()

    val uiState = context(screenContext.presenterContext) {
        debugScreenPresenter(screenChannel = screenChannel)
    }

    DebugScreen(
        uiState = uiState,
        onSoilErrorOverlayEnabledChange = { enabled ->
            screenChannel.send(DebugScreenAction.SetSoilErrorOverlayEnabled(enabled))
        },
        onClockPresetClick = { preset ->
            screenChannel.send(DebugScreenAction.ApplyClockPreset(preset))
        },
        onClockShiftClick = { isoInstant ->
            screenChannel.send(DebugScreenAction.ShiftClockTo(isoInstant))
        },
        onClockResetClick = { screenChannel.send(DebugScreenAction.ResetClock) },
        onClockOverlayEnabledChange = { enabled ->
            screenChannel.send(DebugScreenAction.SetClockOverlayEnabled(enabled))
        },
        onSoilErrorsClick = onNavigateToSoilErrors,
        onClearDataClick = { screenChannel.send(DebugScreenAction.ClearData) },
        onBack = onNavigateBack,
    )
}

package io.github.droidkaigi.confsched.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.AppearanceSettings
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: SettingsPresenterContext)
fun settingsScreenPresenter(
    screenChannel: ScreenChannel<SettingsScreenAction, SettingsScreenActionResult>,
    appearanceSettings: AppearanceSettings,
): SettingsScreenUiState {
    val appearanceMutation = rememberMutation(presenterContext.appearanceSettingsMutationKey)

    // Each option writes the whole record, so the effect has to see the settings of the
    // composition it fires in rather than the ones it was launched with.
    val currentSettings by rememberUpdatedState(appearanceSettings)

    ActionEffect(screenChannel) { action ->
        when (action) {
            is SettingsScreenAction.SelectFont -> {
                val newSettings = currentSettings.copy(fontFamily = action.fontFamily)
                appearanceMutation.mutateAsync(newSettings)
            }

            is SettingsScreenAction.SelectSketchStrength -> {
                val newSettings = currentSettings.copy(sketchStrength = action.sketchStrength)
                appearanceMutation.mutateAsync(newSettings)
            }

            is SettingsScreenAction.SelectColorScheme -> {
                val newSettings = currentSettings.copy(colorSchemeSetting = action.colorSchemeSetting)
                appearanceMutation.mutateAsync(newSettings)
            }
        }
    }

    MutationErrorEffect(appearanceMutation) { error ->
        screenChannel.emit(SettingsScreenActionResult.ShowMessage(error.toUserMessage()))
        appearanceMutation.reset()
    }

    return SettingsScreenUiState(
        fontFamily = appearanceSettings.fontFamily,
        sketchStrength = appearanceSettings.sketchStrength,
        colorSchemeSetting = appearanceSettings.colorSchemeSetting,
    )
}

package io.github.droidkaigi.confsched.feature.settings

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import soil.query.compose.rememberSubscription

@Composable
context(screenContext: SettingsScreenContext)
fun SettingsScreenRoot(
    onNavigateBack: () -> Unit,
) {
    SoilDataBoundary(
        state = rememberSubscription(screenContext.appearanceSettingsSubscriptionKey),
    ) { appearanceSettings ->
        val screenChannel = retainScreenChannel<SettingsScreenAction, SettingsScreenActionResult>()
        val snackbarHostState = LocalSnackbarHostState.current

        ActionResultEffect(screenChannel) { result ->
            when (result) {
                is SettingsScreenActionResult.ShowMessage -> snackbarHostState.showSnackbar(result.message.text)
            }
        }

        val uiState = context(screenContext.presenterContext) {
            settingsScreenPresenter(
                screenChannel = screenChannel,
                appearanceSettings = appearanceSettings,
            )
        }
        SettingsScreen(
            uiState = uiState,
            onFontClick = { screenChannel.send(SettingsScreenAction.SelectFont(it)) },
            onSketchStrengthClick = { screenChannel.send(SettingsScreenAction.SelectSketchStrength(it)) },
            onColorSchemeClick = { screenChannel.send(SettingsScreenAction.SelectColorScheme(it)) },
            onBackClick = onNavigateBack,
        )
    }
}

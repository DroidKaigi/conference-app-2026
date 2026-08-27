package io.github.droidkaigi.confsched.feature.settings

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import io.github.droidkaigi.confsched.core.ui.showSnackbar
import soil.query.compose.rememberSubscription

@Composable
context(screenContext: SettingsScreenContext)
fun SettingsScreenRoot(
    onNavigateBack: () -> Unit,
) {
    SoilDataBoundary(
        state = rememberSubscription(screenContext.appearanceSubscriptionKey),
    ) { appearance ->
        val screenChannel = retainScreenChannel<SettingsScreenAction, SettingsScreenActionResult>()
        val snackbarHostState = LocalSnackbarHostState.current

        ActionResultEffect(screenChannel) { result ->
            when (result) {
                is SettingsScreenActionResult.ShowMessage -> snackbarHostState.showSnackbar(result.message)
            }
        }

        val uiState = context(screenContext.presenterContext) {
            settingsScreenPresenter(
                screenChannel = screenChannel,
                appearanceSettings = appearance.settings,
            )
        }
        SettingsScreen(
            uiState = uiState,
            onFontFamilyClick = { screenChannel.send(SettingsScreenAction.SelectFontFamily(it)) },
            onSketchStrengthClick = { screenChannel.send(SettingsScreenAction.SelectSketchStrength(it)) },
            onColorSchemeSettingClick = { screenChannel.send(SettingsScreenAction.SelectColorSchemeSetting(it)) },
            onBackClick = onNavigateBack,
        )
    }
}

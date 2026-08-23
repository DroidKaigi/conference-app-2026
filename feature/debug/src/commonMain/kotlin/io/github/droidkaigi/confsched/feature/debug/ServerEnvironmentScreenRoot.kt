package io.github.droidkaigi.confsched.feature.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel

@Composable
context(screenContext: ServerEnvironmentScreenContext)
fun ServerEnvironmentScreenRoot(
    onNavigateToTimetable: () -> Unit,
) {
    val screenChannel = retainScreenChannel<ServerEnvironmentScreenAction, ServerEnvironmentScreenActionResult>()

    ActionResultEffect(screenChannel) { result ->
        when (result) {
            ServerEnvironmentScreenActionResult.ServerSelected -> onNavigateToTimetable()
        }
    }

    val uiState = context(screenContext.presenterContext) {
        serverEnvironmentScreenPresenter(screenChannel = screenChannel)
    }

    // Preferences say to skip the picker: re-select the persisted server and move on.
    LaunchedEffect(uiState.autoSelectEnvironment) {
        uiState.autoSelectEnvironment?.let { environment ->
            screenChannel.send(ServerEnvironmentScreenAction.SelectServer(environment))
        }
    }

    ServerEnvironmentScreen(
        skipSelectionNextLaunch = uiState.skipSelectionNextLaunch,
        onSkipNextLaunchChange = { enabled ->
            screenChannel.send(ServerEnvironmentScreenAction.SetSkipNextLaunch(enabled))
        },
        onServerClick = { environment ->
            screenChannel.send(ServerEnvironmentScreenAction.SelectServer(environment))
        },
    )
}

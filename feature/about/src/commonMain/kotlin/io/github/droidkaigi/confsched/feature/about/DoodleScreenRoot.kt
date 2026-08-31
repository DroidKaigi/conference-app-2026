package io.github.droidkaigi.confsched.feature.about

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel

@Composable
context(screenContext: DoodleScreenContext)
fun DoodleScreenRoot(
    onNavigateBack: () -> Unit,
) {
    val screenChannel = retainScreenChannel<DoodleScreenAction, DoodleScreenActionResult>()
    val snackbarHostState = LocalSnackbarHostState.current

    ActionResultEffect(screenChannel) { result ->
        when (result) {
            DoodleScreenActionResult.Reloaded -> snackbarHostState.showSnackbar("Reloaded")
        }
    }

    val uiState = context(screenContext.presenterContext) {
        doodleScreenPresenter(screenChannel)
    }
    DoodleScreen(
        uiState = uiState,
        onReloadClick = { screenChannel.send(DoodleScreenAction.Reload) },
        onBackClick = onNavigateBack,
    )
}

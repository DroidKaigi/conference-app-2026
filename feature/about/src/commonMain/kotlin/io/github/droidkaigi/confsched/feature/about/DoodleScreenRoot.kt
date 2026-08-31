package io.github.droidkaigi.confsched.feature.about

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import io.github.droidkaigi.confsched.core.ui.showSnackbar
import soil.query.compose.rememberSubscription

@Composable
context(screenContext: DoodleScreenContext)
fun DoodleScreenRoot(
    onNavigateBack: () -> Unit,
) {
    SoilDataBoundary(
        state = rememberSubscription(screenContext.doodleSubscriptionKey),
    ) { doodle ->
        val screenChannel = retainScreenChannel<DoodleScreenAction, DoodleScreenActionResult>()
        val snackbarHostState = LocalSnackbarHostState.current

        ActionResultEffect(screenChannel) { result ->
            when (result) {
                DoodleScreenActionResult.Saved -> onNavigateBack()
                is DoodleScreenActionResult.ShowMessage -> snackbarHostState.showSnackbar(result.message)
            }
        }

        val uiState = context(screenContext.presenterContext) {
            doodleScreenPresenter(
                screenChannel = screenChannel,
                savedDoodle = doodle,
            )
        }
        DoodleScreen(
            uiState = uiState,
            onSaveClick = { screenChannel.send(DoodleScreenAction.Save(it)) },
            onBackClick = onNavigateBack,
        )
    }
}

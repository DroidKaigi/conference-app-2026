package io.github.droidkaigi.confsched.feature.doodle

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.core.model.DoodlePenSize
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import io.github.droidkaigi.confsched.core.ui.showSnackbar
import soil.query.compose.rememberSubscription

@Composable
context(screenContext: DoodleScreenContext)
fun DoodleScreenRoot(
    onNavigateBack: () -> Unit,
) {
    SoilDataBoundary(
        state1 = rememberSubscription(screenContext.doodlesSubscriptionKey),
        state2 = rememberSubscription(screenContext.profileCardSubscriptionKey),
    ) { savedDoodles, card ->
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
                savedDoodles = savedDoodles,
                card = card,
            )
        }
        DoodleScreen(
            uiState = uiState,
            initialPenSize = DoodlePenSize.Normal,
            onSaveWallClick = { screenChannel.send(DoodleScreenAction.SaveWall(it)) },
            onSaveCardClick = { front, back -> screenChannel.send(DoodleScreenAction.SaveCard(front, back)) },
            onBackClick = onNavigateBack,
        )
    }
}

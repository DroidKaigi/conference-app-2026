package io.github.droidkaigi.confsched.feature.favorites

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.ui.rememberNotificationPermissionGranted

@Composable
context(screenContext: FirstFavoriteNotificationScreenContext)
fun FirstFavoriteNotificationScreenRoot(
    mascot: Mascot,
    onNavigateToWidgetStep: () -> Unit,
) {
    val screenChannel = retainScreenChannel<FirstFavoriteNotificationScreenAction, FirstFavoriteNotificationScreenActionResult>()
    val areNotificationsOn = rememberNotificationPermissionGranted() == true

    ActionResultEffect(screenChannel) { result ->
        when (result) {
            FirstFavoriteNotificationScreenActionResult.Answered -> onNavigateToWidgetStep()
        }
    }

    val uiState = context(screenContext.presenterContext) {
        firstFavoriteNotificationScreenPresenter(
            screenChannel = screenChannel,
            areNotificationsOn = areNotificationsOn,
            mascot = mascot,
        )
    }
    FirstFavoriteNotificationScreen(
        uiState = uiState,
        onTurnOnNotificationsClick = { screenChannel.send(FirstFavoriteNotificationScreenAction.TurnOnNotifications) },
        onContinueClick = { screenChannel.send(FirstFavoriteNotificationScreenAction.Continue) },
    )
}

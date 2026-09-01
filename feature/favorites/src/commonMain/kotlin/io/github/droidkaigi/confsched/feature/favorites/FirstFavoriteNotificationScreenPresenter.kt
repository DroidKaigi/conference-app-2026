package io.github.droidkaigi.confsched.feature.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.model.Mascot
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: FirstFavoriteNotificationPresenterContext)
fun firstFavoriteNotificationScreenPresenter(
    screenChannel: ScreenChannel<FirstFavoriteNotificationScreenAction, FirstFavoriteNotificationScreenActionResult>,
    requestNotificationPermission: suspend () -> Unit,
    areNotificationsOn: Boolean,
    mascot: Mascot,
): FirstFavoriteNotificationScreenUiState {
    val guidanceMutation = rememberMutation(presenterContext.firstFavoriteGuidanceMutationKey)
    var isAnswering by retain { mutableStateOf(false) }

    ActionEffect(screenChannel) { action ->
        // An answer either way is the reader deciding, so the guidance is not offered again;
        // a dialog closed without one leaves the flag alone.
        when (action) {
            FirstFavoriteNotificationScreenAction.TurnOnNotifications -> {
                isAnswering = true
                guidanceMutation.mutateAsync(Unit)
                requestNotificationPermission()
                isAnswering = false
                screenChannel.emit(FirstFavoriteNotificationScreenActionResult.Answered)
            }

            FirstFavoriteNotificationScreenAction.Continue -> {
                isAnswering = true
                guidanceMutation.mutateAsync(Unit)
                isAnswering = false
                screenChannel.emit(FirstFavoriteNotificationScreenActionResult.Answered)
            }
        }
    }

    // A dialog has nowhere to show a message, and the guidance being offered twice is a smaller
    // fault than blocking the step that follows.
    MutationErrorEffect(guidanceMutation) { error ->
        presenterContext.logger.error(error) { "Failed to record the first-favorite guidance answer" }
        guidanceMutation.reset()
    }

    return FirstFavoriteNotificationScreenUiState(
        isAnswering = isAnswering,
        areNotificationsOn = areNotificationsOn,
        mascot = mascot,
    )
}

package io.github.droidkaigi.confsched.feature.favorites

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.MutationSuccessEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.model.Mascot
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: FirstFavoriteNotificationPresenterContext)
fun firstFavoriteNotificationScreenPresenter(
    screenChannel: ScreenChannel<FirstFavoriteNotificationScreenAction, FirstFavoriteNotificationScreenActionResult>,
    areNotificationsOn: Boolean,
    mascot: Mascot,
): FirstFavoriteNotificationScreenUiState {
    val guidanceMutation = rememberMutation(presenterContext.firstFavoriteGuidanceMutationKey)
    val permissionMutation = rememberMutation(presenterContext.notificationPermissionMutationKey)

    ActionEffect(screenChannel) { action ->
        when (action) {
            FirstFavoriteNotificationScreenAction.TurnOnNotifications -> permissionMutation.mutateAsync(Unit)
            FirstFavoriteNotificationScreenAction.Continue -> guidanceMutation.mutateAsync(Unit)
        }
    }

    // An answer either way is the reader deciding, so the guidance is not offered again; a dialog
    // closed without one leaves the flag alone. The platform answer is awaited first, so the step
    // that follows does not open over the permission dialog.
    MutationSuccessEffect(permissionMutation) {
        permissionMutation.reset()
        guidanceMutation.mutateAsync(Unit)
    }
    MutationErrorEffect(permissionMutation) { error ->
        presenterContext.logger.error(error) { "Failed to ask the platform to post notifications" }
        permissionMutation.reset()
        guidanceMutation.mutateAsync(Unit)
    }

    // Answering navigates away and can cancel this handler at its next suspension point, so the
    // state is reset before the result is emitted.
    // A dialog has nowhere to show a message, and the guidance being offered twice is a smaller
    // fault than blocking the step that follows.
    MutationSuccessEffect(guidanceMutation) {
        guidanceMutation.reset()
        screenChannel.emit(FirstFavoriteNotificationScreenActionResult.Answered)
    }
    MutationErrorEffect(guidanceMutation) { error ->
        presenterContext.logger.error(error) { "Failed to record the first-favorite guidance answer" }
        guidanceMutation.reset()
        screenChannel.emit(FirstFavoriteNotificationScreenActionResult.Answered)
    }

    return FirstFavoriteNotificationScreenUiState(
        isAnswering = permissionMutation.isPending || guidanceMutation.isPending,
        areNotificationsOn = areNotificationsOn,
        mascot = mascot,
    )
}

package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.FirstFavoriteNotificationScreenScope
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.NotificationPermissionMutationKey
import io.github.droidkaigi.confsched.core.model.NotificationPermissionResult
import io.github.droidkaigi.confsched.core.model.SoilIds
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import soil.query.buildMutationKey
import kotlin.coroutines.resume

@Inject
@ContributesBinding(FirstFavoriteNotificationScreenScope::class)
class DefaultNotificationPermissionMutationKey(
    extraTag: MutationTag,
) : NotificationPermissionMutationKey by buildMutationKey(
    id = SoilIds.notificationPermissionMutation(extraTag),
    mutate = { requestAuthorization() },
)

private suspend fun requestAuthorization(): NotificationPermissionResult = suspendCancellableCoroutine { continuation ->
    UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
        UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge,
    ) { granted, _ ->
        if (continuation.isActive) {
            continuation.resume(
                if (granted) NotificationPermissionResult.Enabled else NotificationPermissionResult.Disabled,
            )
        }
    }
}

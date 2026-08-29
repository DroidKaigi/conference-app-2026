package io.github.droidkaigi.confsched.app.notification

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.DeepLink
import io.github.droidkaigi.confsched.core.common.DeepLinkStore
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptionBanner
import platform.UserNotifications.UNNotificationPresentationOptionList
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

// The notification center holds its delegate weakly, so the graph keeps this instance alive.
@Inject
@SingleIn(AppScope::class)
internal class SessionReminderNotificationDelegate(
    private val deepLinkStore: DeepLinkStore,
) : NSObject(),
    UNUserNotificationCenterDelegateProtocol {

    fun install() {
        UNUserNotificationCenter.currentNotificationCenter().delegate = this
    }

    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        didReceiveNotificationResponse: UNNotificationResponse,
        withCompletionHandler: () -> Unit,
    ) {
        didReceiveNotificationResponse.notification.request.identifier
            .reminderSessionId()
            ?.let { deepLinkStore.submit(DeepLink.FavoriteSessionDetail(it)) }
        withCompletionHandler()
    }

    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit,
    ) {
        withCompletionHandler(
            UNNotificationPresentationOptionBanner or
                UNNotificationPresentationOptionList or
                UNNotificationPresentationOptionSound,
        )
    }
}

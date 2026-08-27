package io.github.droidkaigi.confsched.app.notification

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.app.ScheduledSessionReminderIds
import io.github.droidkaigi.confsched.app.SessionReminderScheduler
import io.github.droidkaigi.confsched.core.common.KaigiClock
import io.github.droidkaigi.confsched.core.model.DisplayLanguage
import io.github.droidkaigi.confsched.core.model.SessionReminder
import io.github.droidkaigi.confsched.core.model.locationText
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

internal const val IDENTIFIER_PREFIX = "session-reminder:"

/** iOS keeps at most 64 pending requests per app, so the reminders furthest out are left unscheduled. */
private const val MAX_PENDING_REQUESTS = 64

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class IosSessionReminderScheduler(
    private val kaigiClock: KaigiClock,
    private val scheduledIds: ScheduledSessionReminderIds,
) : SessionReminderScheduler {
    private val center = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun reschedule(reminders: List<SessionReminder>) {
        val old = scheduledIds.read()
        val wanted = reminders.take(MAX_PENDING_REQUESTS)
        val new = wanted.mapTo(mutableSetOf()) { it.itemId }
        scheduledIds.write(old + new)

        val now = kaigiClock.now()
        val due = wanted.mapNotNull { reminder ->
            val delay = reminder.notifyAt - now
            when {
                delay >= 1.seconds -> reminder to delay

                // Overdue but not started: post now, unless an earlier round already armed it.
                reminder.itemId !in old -> reminder to 1.seconds

                else -> null
            }
        }
        if (due.isNotEmpty()) requestAuthorization()

        val wantedIdentifiers = wanted.mapTo(mutableSetOf()) { it.identifier }
        (ownIdentifiers(::pendingIdentifiers) - wantedIdentifiers).takeIf { it.isNotEmpty() }
            ?.let { center.removePendingNotificationRequestsWithIdentifiers(it.toList()) }
        (ownIdentifiers(::deliveredIdentifiers) - wantedIdentifiers).takeIf { it.isNotEmpty() }
            ?.let { center.removeDeliveredNotificationsWithIdentifiers(it.toList()) }
        due.forEach { (reminder, delay) -> add(reminder, delay) }
        scheduledIds.write(new)
    }

    private suspend fun add(reminder: SessionReminder, delay: Duration) {
        val content = UNMutableNotificationContent().apply {
            setTitle(reminder.title.of(displayLanguage()))
            setBody("${reminder.startsAtText} · ${reminder.room.locationText}")
            setSound(UNNotificationSound.defaultSound)
        }
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = reminder.identifier,
            content = content,
            trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
                timeInterval = delay.toDouble(DurationUnit.SECONDS),
                repeats = false,
            ),
        )
        suspendCancellableCoroutine { continuation ->
            center.addNotificationRequest(request) { if (continuation.isActive) continuation.resume(Unit) }
        }
    }

    private suspend fun requestAuthorization() {
        suspendCancellableCoroutine { continuation ->
            center.requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge,
            ) { _, _ -> if (continuation.isActive) continuation.resume(Unit) }
        }
    }

    private suspend fun ownIdentifiers(source: suspend () -> List<String>): Set<String> =
        source().filterTo(mutableSetOf()) { it.startsWith(IDENTIFIER_PREFIX) }

    private suspend fun pendingIdentifiers(): List<String> = suspendCancellableCoroutine { continuation ->
        center.getPendingNotificationRequestsWithCompletionHandler { requests ->
            if (continuation.isActive) continuation.resume(requests.orEmpty().map { (it as UNNotificationRequest).identifier })
        }
    }

    private suspend fun deliveredIdentifiers(): List<String> = suspendCancellableCoroutine { continuation ->
        center.getDeliveredNotificationsWithCompletionHandler { notifications ->
            if (continuation.isActive) continuation.resume(notifications.orEmpty().map { (it as UNNotification).request.identifier })
        }
    }
}

private val SessionReminder.identifier: String get() = IDENTIFIER_PREFIX + itemId.value

private fun displayLanguage(): DisplayLanguage =
    if (NSLocale.currentLocale.languageCode == "ja") DisplayLanguage.Japanese else DisplayLanguage.English

internal fun String.reminderSessionId(): String? = removePrefix(IDENTIFIER_PREFIX).takeIf { it != this }

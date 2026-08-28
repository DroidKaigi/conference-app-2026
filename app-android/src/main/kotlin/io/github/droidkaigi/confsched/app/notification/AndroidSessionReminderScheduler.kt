package io.github.droidkaigi.confsched.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.app.ScheduledSessionReminderIds
import io.github.droidkaigi.confsched.app.SessionReminderScheduler
import io.github.droidkaigi.confsched.app.localized
import io.github.droidkaigi.confsched.core.common.KaigiClock
import io.github.droidkaigi.confsched.core.model.SessionReminder
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.locationText
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AndroidSessionReminderScheduler(
    private val context: Context,
    private val kaigiClock: KaigiClock,
    private val scheduledIds: ScheduledSessionReminderIds,
) : SessionReminderScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override suspend fun reschedule(reminders: List<SessionReminder>) {
        val old = scheduledIds.read()
        val new = reminders.mapTo(mutableSetOf()) { it.itemId }

        // Persisting the union first: a crash before the alarms are in step leaves them cancellable.
        scheduledIds.write(old + new)
        (old - new).forEach(::cancel)
        val now = kaigiClock.now()
        reminders
            .filterNot { it.notifyAt <= now && it.itemId in old }
            .forEach { schedule(it, now) }
        scheduledIds.write(new)
    }

    private fun schedule(reminder: SessionReminder, now: Instant) {
        val intent = Intent(context, SessionReminderReceiver::class.java)
            .setData(reminder.itemId.reminderUri())
            .putExtra(EXTRA_ITEM_ID, reminder.itemId.value)
            .putExtra(EXTRA_TITLE, context.localized(reminder.title))
            .putExtra(EXTRA_ROOM, reminder.room.locationText)
            .putExtra(EXTRA_STARTS_AT, reminder.startsAtText)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.itemId.reminderRequestCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        // Inexact on purpose: an exact alarm would cost the app the user-revocable SCHEDULE_EXACT_ALARM permission.
        // The delay is measured against the app clock, so the debug tooling's shifted time holds.
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + (reminder.notifyAt - now).inWholeMilliseconds,
            pendingIntent,
        )
    }

    private fun cancel(id: TimetableItemId) {
        NotificationManagerCompat.from(context).cancel(id.reminderRequestCode())
        val intent = Intent(context, SessionReminderReceiver::class.java).setData(id.reminderUri())
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.reminderRequestCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        ) ?: return
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}

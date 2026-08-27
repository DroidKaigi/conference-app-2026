package io.github.droidkaigi.confsched.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.app.SessionReminderScheduler
import io.github.droidkaigi.confsched.app.localized
import io.github.droidkaigi.confsched.core.common.KaigiClock
import io.github.droidkaigi.confsched.core.data.SessionRemindersDataStoreQualifier
import io.github.droidkaigi.confsched.core.model.SessionReminder
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.coroutines.flow.first
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AndroidSessionReminderScheduler(
    private val context: Context,
    private val kaigiClock: KaigiClock,
    @SessionRemindersDataStoreQualifier private val dataStore: DataStore<Preferences>,
) : SessionReminderScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override suspend fun reschedule(reminders: List<SessionReminder>) {
        val old = dataStore.data.first()[SCHEDULED_IDS_KEY].orEmpty()
        val new = reminders.mapTo(mutableSetOf()) { it.itemId.value }

        // Persisting the union first: a crash before the alarms are in step leaves them cancellable.
        persist(old + new)
        (old - new).forEach { cancel(TimetableItemId(it)) }
        val now = kaigiClock.now()
        reminders
            .filterNot { it.notifyAt <= now && it.itemId.value in old }
            .forEach { schedule(it, now) }
        persist(new)
    }

    private suspend fun persist(ids: Set<String>) {
        dataStore.edit { it[SCHEDULED_IDS_KEY] = ids }
    }

    private fun schedule(reminder: SessionReminder, now: Instant) {
        val intent = Intent(context, SessionReminderReceiver::class.java)
            .setData(reminder.itemId.reminderUri())
            .putExtra(EXTRA_ITEM_ID, reminder.itemId.value)
            .putExtra(EXTRA_TITLE, context.localized(reminder.title))
            .putExtra(EXTRA_ROOM, reminder.room.name)
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

    private companion object {
        val SCHEDULED_IDS_KEY = stringSetPreferencesKey("scheduledSessionIds")
    }
}

package io.github.droidkaigi.confsched.app.notification

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.github.droidkaigi.confsched.R
import io.github.droidkaigi.confsched.app.favoriteSessionDeepLinkIntent
import io.github.droidkaigi.confsched.core.model.TimetableItemId

private const val CHANNEL_ID = "session_reminders"

class SessionReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val itemId = intent.getStringExtra(EXTRA_ITEM_ID)?.let(::TimetableItemId) ?: return
        val title = intent.getStringExtra(EXTRA_TITLE) ?: return
        val room = intent.getStringExtra(EXTRA_ROOM) ?: return
        val startsAt = intent.getStringExtra(EXTRA_STARTS_AT) ?: return
        if (!context.canPostNotifications()) return

        val manager = NotificationManagerCompat.from(context)
        manager.createNotificationChannel(
            NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
                .setName(context.getString(R.string.session_reminder_channel_name))
                .build(),
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.widget_symbol_mark)
            .setContentTitle(title)
            .setContentText(context.getString(R.string.session_reminder_text, startsAt, room))
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    itemId.reminderRequestCode(),
                    favoriteSessionDeepLinkIntent(context, itemId),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                ),
            )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        manager.notify(itemId.reminderRequestCode(), notification)
    }
}

private fun Context.canPostNotifications(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

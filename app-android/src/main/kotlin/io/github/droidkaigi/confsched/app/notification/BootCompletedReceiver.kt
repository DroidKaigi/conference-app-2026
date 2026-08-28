package io.github.droidkaigi.confsched.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** A reboot clears every alarm the app set, so the reminders are scheduled again from persisted state. */
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val sync = context.sessionReminderDependencies.sessionReminderSync
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                sync.rescheduleNow()
            } finally {
                pendingResult.finish()
            }
        }
    }
}

package io.github.droidkaigi.confsched.app

import io.github.droidkaigi.confsched.core.model.SessionReminder

/**
 * Hands the platform notification service the reminders of the sessions that have not started,
 * replacing every reminder it scheduled before.
 */
interface SessionReminderScheduler {
    suspend fun reschedule(reminders: List<SessionReminder>)
}

package io.github.droidkaigi.confsched.app.notification

import androidx.core.net.toUri
import io.github.droidkaigi.confsched.core.model.TimetableItemId

internal const val EXTRA_ITEM_ID = "sessionReminderItemId"
internal const val EXTRA_TITLE = "sessionReminderTitle"
internal const val EXTRA_ROOM = "sessionReminderRoom"
internal const val EXTRA_STARTS_AT = "sessionReminderStartsAt"

/** Identifies one reminder's alarm and its notification; extras take no part in either. */
internal fun TimetableItemId.reminderRequestCode(): Int = value.hashCode()

// `PendingIntent` matching ignores extras, so the URI is what keeps two reminders' intents distinct.
internal fun TimetableItemId.reminderUri() = "session-reminder://$value".toUri()

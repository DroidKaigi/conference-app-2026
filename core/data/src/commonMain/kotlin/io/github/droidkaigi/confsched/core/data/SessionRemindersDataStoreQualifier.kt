package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.Qualifier

/** Qualifies the `DataStore<Preferences>` holding the session ids reminders are scheduled for. */
@Qualifier
annotation class SessionRemindersDataStoreQualifier

internal const val SESSION_REMINDERS_DATA_STORE_FILE_NAME = "confsched2026.sessionReminders.preferences_pb"

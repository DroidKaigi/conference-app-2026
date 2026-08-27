package io.github.droidkaigi.confsched.app.notification

import android.content.Context
import io.github.droidkaigi.confsched.app.SessionReminderSync
import io.github.droidkaigi.confsched.app.appGraph
import io.github.droidkaigi.confsched.core.data.FavoritesStore

interface SessionReminderDependencies {
    val sessionReminderSync: SessionReminderSync
    val favoritesStore: FavoritesStore
}

val Context.sessionReminderDependencies: SessionReminderDependencies get() = appGraph as SessionReminderDependencies

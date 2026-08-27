package io.github.droidkaigi.confsched.app

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.data.SessionRemindersDataStoreQualifier
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.coroutines.flow.first

/** The session ids the platform scheduler armed in its last round, so the next round can diff against them. */
@Inject
@SingleIn(AppScope::class)
class ScheduledSessionReminderIds(
    @SessionRemindersDataStoreQualifier private val dataStore: DataStore<Preferences>,
) {
    suspend fun read(): Set<TimetableItemId> =
        dataStore.data.first()[SCHEDULED_IDS_KEY].orEmpty().mapTo(mutableSetOf(), ::TimetableItemId)

    suspend fun write(ids: Set<TimetableItemId>) {
        dataStore.edit { preferences -> preferences[SCHEDULED_IDS_KEY] = ids.mapTo(mutableSetOf()) { it.value } }
    }

    private companion object {
        val SCHEDULED_IDS_KEY = stringSetPreferencesKey("scheduledSessionIds")
    }
}

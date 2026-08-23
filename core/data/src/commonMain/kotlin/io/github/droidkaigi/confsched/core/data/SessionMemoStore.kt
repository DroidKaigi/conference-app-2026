package io.github.droidkaigi.confsched.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Inject
@SingleIn(AppScope::class)
class SessionMemoStore(@SessionMemoDataStoreQualifier private val dataStore: DataStore<Preferences>) {

    fun memos(): Flow<PersistentMap<TimetableItemId, String>> = dataStore.data
        .map { it.readMemos() }
        .distinctUntilChanged()

    suspend fun write(id: TimetableItemId, text: String) {
        dataStore.edit { preferences ->
            val key = stringPreferencesKey(id.value)
            if (text.isEmpty()) preferences.remove(key) else preferences[key] = text
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    private fun Preferences.readMemos(): PersistentMap<TimetableItemId, String> = asMap()
        .mapNotNull { (key, value) ->
            val text = value as? String ?: return@mapNotNull null
            TimetableItemId(key.name) to text
        }
        .toMap()
        .toPersistentMap()
}

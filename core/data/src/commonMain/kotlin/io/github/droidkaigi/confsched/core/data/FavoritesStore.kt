package io.github.droidkaigi.confsched.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Inject
@SingleIn(AppScope::class)
class FavoritesStore(@FavoritesDataStoreQualifier private val dataStore: DataStore<Preferences>) {

    fun favoriteIds(): Flow<PersistentSet<TimetableItemId>> = dataStore.data
        .map { it.readFavoriteIds() }
        .distinctUntilChanged()

    suspend fun toggle(id: TimetableItemId): Boolean {
        var added = false
        dataStore.edit { preferences ->
            val current = preferences[FAVORITE_IDS_KEY].orEmpty()
            if (id.value in current) {
                preferences[FAVORITE_IDS_KEY] = current - id.value
                added = false
            } else {
                preferences[FAVORITE_IDS_KEY] = current + id.value
                added = true
            }
        }
        return added
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    private fun Preferences.readFavoriteIds(): PersistentSet<TimetableItemId> =
        this[FAVORITE_IDS_KEY]?.map(::TimetableItemId)?.toPersistentSet() ?: persistentSetOf()

    private companion object {
        val FAVORITE_IDS_KEY = stringSetPreferencesKey("favoriteIds")
    }
}

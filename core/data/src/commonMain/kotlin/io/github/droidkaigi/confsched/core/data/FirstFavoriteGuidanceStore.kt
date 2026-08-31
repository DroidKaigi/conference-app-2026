package io.github.droidkaigi.confsched.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Whether the first-favorite guidance has already been answered. A dialog the reader dismissed
 * without choosing leaves the flag unset, so the guidance is offered once more later.
 */
@Inject
@SingleIn(AppScope::class)
class FirstFavoriteGuidanceStore(@SettingsDataStoreQualifier private val dataStore: DataStore<Preferences>) {

    fun consumed(): Flow<Boolean> = dataStore.data
        .map { it[CONSUMED_KEY] == true }
        .distinctUntilChanged()

    suspend fun consume() {
        dataStore.edit { it[CONSUMED_KEY] = true }
    }

    private companion object {
        val CONSUMED_KEY = booleanPreferencesKey("firstFavorite.guidanceConsumed")
    }
}

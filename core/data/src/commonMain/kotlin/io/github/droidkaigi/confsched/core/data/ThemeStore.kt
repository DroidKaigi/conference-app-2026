package io.github.droidkaigi.confsched.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.ColorSchemeSetting
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
@SingleIn(AppScope::class)
class ThemeStore(@SettingsDataStoreQualifier private val dataStore: DataStore<Preferences>) {

    // A draw inside the flow would flicker the theme mid-session.
    private val colorSchemeForThisLaunch = KaigiColorScheme.entries.random()

    fun colorScheme(): Flow<KaigiColorScheme> = dataStore.data.map { preferences ->
        when (val setting = preferences.readColorSchemeSetting()) {
            is ColorSchemeSetting.Fixed -> setting.colorScheme
            ColorSchemeSetting.RandomPerLaunch -> colorSchemeForThisLaunch
        }
    }

    suspend fun saveColorSchemeSetting(setting: ColorSchemeSetting) {
        val persistedValue = when (setting) {
            is ColorSchemeSetting.Fixed -> setting.colorScheme.name
            ColorSchemeSetting.RandomPerLaunch -> RANDOM_PER_LAUNCH
        }
        dataStore.edit { it[KEY] = persistedValue }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    private fun Preferences.readColorSchemeSetting(): ColorSchemeSetting =
        this[KEY]
            ?.let { name -> KaigiColorScheme.entries.firstOrNull { it.name == name } }
            ?.let(ColorSchemeSetting::Fixed)
            ?: ColorSchemeSetting.RandomPerLaunch

    companion object {
        private val KEY = stringPreferencesKey("theme.colorScheme")

        private const val RANDOM_PER_LAUNCH = "random"
    }
}

package io.github.droidkaigi.confsched.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.AppearanceSettings
import io.github.droidkaigi.confsched.core.model.ColorSchemeSetting
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.KaigiFontFamily
import io.github.droidkaigi.confsched.core.model.SketchStrength
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Inject
@SingleIn(AppScope::class)
class AppearanceSettingsStore(@SettingsDataStoreQualifier private val dataStore: DataStore<Preferences>) {

    // Drawn once for the process: re-drawing per emission would flicker the app through
    // schemes every time an unrelated preference changes.
    private val launchColorScheme = KaigiColorScheme.entries.random()

    fun settings(): Flow<AppearanceSettings> = dataStore.data.map { it.readSettings() }.distinctUntilChanged()

    fun colorScheme(): Flow<KaigiColorScheme> = settings()
        .map { settings -> settings.colorSchemeSetting.resolve() }
        .distinctUntilChanged()

    suspend fun save(settings: AppearanceSettings) {
        dataStore.edit { preferences ->
            when (val selection = settings.colorSchemeSetting) {
                ColorSchemeSetting.RandomPerLaunch -> preferences[COLOR_SCHEME_KEY] = RANDOM_PER_LAUNCH
                is ColorSchemeSetting.Fixed -> preferences[COLOR_SCHEME_KEY] = selection.colorScheme.name
            }
            preferences[FONT_FAMILY_KEY] = settings.fontFamily.name
            preferences[SKETCH_STRENGTH_KEY] = settings.sketchStrength.name
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    private fun ColorSchemeSetting.resolve(): KaigiColorScheme = when (this) {
        ColorSchemeSetting.RandomPerLaunch -> launchColorScheme
        is ColorSchemeSetting.Fixed -> colorScheme
    }

    private fun Preferences.readSettings(): AppearanceSettings {
        val defaults = AppearanceSettings.Default
        return AppearanceSettings(
            colorSchemeSetting = this[COLOR_SCHEME_KEY]
                ?.let { name -> KaigiColorScheme.entries.firstOrNull { it.name == name } }
                ?.let(ColorSchemeSetting::Fixed)
                ?: defaults.colorSchemeSetting,
            fontFamily = this[FONT_FAMILY_KEY]
                ?.let { name -> KaigiFontFamily.entries.firstOrNull { it.name == name } }
                ?: defaults.fontFamily,
            sketchStrength = this[SKETCH_STRENGTH_KEY]
                ?.let { name -> SketchStrength.entries.firstOrNull { it.name == name } }
                ?: defaults.sketchStrength,
        )
    }

    companion object {
        private val COLOR_SCHEME_KEY = stringPreferencesKey("theme.colorScheme")
        private val FONT_FAMILY_KEY = stringPreferencesKey("appearance.fontFamily")
        private val SKETCH_STRENGTH_KEY = stringPreferencesKey("appearance.sketchStrength")

        private const val RANDOM_PER_LAUNCH = "random"
    }
}

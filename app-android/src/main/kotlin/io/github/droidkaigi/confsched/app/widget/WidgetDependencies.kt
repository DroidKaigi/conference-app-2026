package io.github.droidkaigi.confsched.app.widget

import android.content.Context
import io.github.droidkaigi.confsched.app.appGraph
import io.github.droidkaigi.confsched.core.common.KaigiClock
import io.github.droidkaigi.confsched.core.data.AppearanceSettingsStore
import io.github.droidkaigi.confsched.core.data.FavoritesStore
import io.github.droidkaigi.confsched.core.data.PersistedTimetableReader

/** What the favorites widget needs from the app graph; AndroidAppGraph implements it. */
interface WidgetDependencies {
    val favoritesStore: FavoritesStore
    val appearanceSettingsStore: AppearanceSettingsStore
    val persistedTimetableReader: PersistedTimetableReader
    val kaigiClock: KaigiClock
}

val Context.widgetDependencies: WidgetDependencies get() = appGraph as WidgetDependencies

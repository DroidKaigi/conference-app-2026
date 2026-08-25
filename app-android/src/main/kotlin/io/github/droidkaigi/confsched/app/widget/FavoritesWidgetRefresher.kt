package io.github.droidkaigi.confsched.app.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

/**
 * Pushes a widget update whenever favorites or the color scheme change while the app process is
 * alive; the provider's updatePeriodMillis carries the widget between app launches.
 */
fun startFavoritesWidgetRefresh(context: Context, scope: CoroutineScope) {
    val dependencies = context.widgetDependencies
    scope.launch {
        merge(
            dependencies.favoritesStore.favoriteIds().drop(1).map {},
            dependencies.appearanceSettingsStore.colorScheme().drop(1).map {},
        ).collect {
            FavoritesWidget().updateAll(context)
        }
    }
}

package io.github.droidkaigi.confsched.app.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.computeFavoritesWidgetState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.first

class FavoritesWidget : GlanceAppWidget() {
    // The hand-drawn frame is generated for the actual size, so every size gets its own pass.
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dependencies = context.widgetDependencies
        val colorScheme = dependencies.themeStore.colorScheme().first()
        val timetable = dependencies.persistedTimetableReader.read() ?: Timetable(items = persistentListOf())
        val favoriteIds = dependencies.favoritesStore.favoriteIds().first()
        val state = computeFavoritesWidgetState(
            now = dependencies.kaigiClock.now(),
            timetable = timetable,
            favoriteIds = favoriteIds,
        )
        val colors = colorScheme.toFavoritesWidgetColors()
        provideContent {
            FavoritesWidgetContent(state, colors)
        }
    }
}

class FavoritesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FavoritesWidget()
}

package io.github.droidkaigi.confsched.app.widget

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import kotlinx.coroutines.flow.first

class FavoritesWidget : GlanceAppWidget() {
    // The hand-drawn frame is generated for the actual size, so every size gets its own pass.
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val renders = context.widgetDependencies.favoritesWidgetRenders()
        // The composition has to start with a value, so the first render is awaited here; the
        // collection inside provideContent is what keeps a live session on the current state.
        val initial = renders.first()
        scheduleFavoritesWidgetRefresh(context, context.widgetDependencies.kaigiClock.now())
        provideContent {
            val render by renders.collectAsState(initial)
            FavoritesWidgetContent(render.state, render.colors, render.mascot)
        }
    }
}

class FavoritesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FavoritesWidget()
}

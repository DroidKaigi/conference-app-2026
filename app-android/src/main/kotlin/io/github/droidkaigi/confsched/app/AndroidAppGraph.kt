package io.github.droidkaigi.confsched.app

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.app.widget.WidgetDependencies

@DependencyGraph(scope = AppScope::class)
interface AndroidAppGraph :
    AppGraph,
    WidgetDependencies {
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides context: Context): AndroidAppGraph
    }
}

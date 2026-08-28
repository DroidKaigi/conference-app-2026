package io.github.droidkaigi.confsched.app

import android.app.Application
import android.content.Context
import dev.zacsweers.metro.createGraphFactory
import io.github.droidkaigi.confsched.app.notification.sessionReminderDependencies
import io.github.droidkaigi.confsched.app.widget.startFavoritesWidgetRefresh
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class KaigiApplication : Application() {
    val appGraph: AppGraph by lazy {
        createGraphFactory<AndroidAppGraph.Factory>().create(applicationContext)
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        appGraph.appInitializer.initialize()
        startFavoritesWidgetRefresh(this, applicationScope)
        sessionReminderDependencies.sessionReminderSync.start(applicationScope)
    }
}

val Context.appGraph: AppGraph get() = (applicationContext as KaigiApplication).appGraph

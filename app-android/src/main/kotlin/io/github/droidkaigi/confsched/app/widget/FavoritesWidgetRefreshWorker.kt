package io.github.droidkaigi.confsched.app.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import io.github.droidkaigi.confsched.core.model.nextFavoritesWidgetBoundary
import kotlinx.coroutines.flow.first
import kotlin.time.Instant
import kotlin.time.toJavaDuration

private const val REFRESH_WORK_NAME = "favorites-widget-refresh"

/**
 * Re-renders the widget at the next instant its state changes on its own (a countdown day
 * rolling over, a favorite starting or ending); each render schedules the one after it.
 */
class FavoritesWidgetRefreshWorker(
    context: Context,
    parameters: WorkerParameters,
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        FavoritesWidget().updateAll(applicationContext)
        return Result.success()
    }
}

internal suspend fun scheduleFavoritesWidgetRefresh(context: Context, now: Instant) {
    val dependencies = context.widgetDependencies
    val timetable = dependencies.persistedTimetableReader.read()
    val favoriteIds = dependencies.favoritesStore.favoriteIds().first()
    val boundary = timetable?.let { nextFavoritesWidgetBoundary(now, it, favoriteIds) }
    val workManager = WorkManager.getInstance(context)
    if (boundary == null) {
        workManager.cancelUniqueWork(REFRESH_WORK_NAME)
        return
    }
    val request = OneTimeWorkRequestBuilder<FavoritesWidgetRefreshWorker>()
        .setInitialDelay((boundary - now).toJavaDuration())
        .build()
    workManager.enqueueUniqueWork(REFRESH_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
}

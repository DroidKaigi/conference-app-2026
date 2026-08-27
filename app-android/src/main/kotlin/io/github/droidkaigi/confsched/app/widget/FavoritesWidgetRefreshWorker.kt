package io.github.droidkaigi.confsched.app.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.nextFavoritesWidgetBoundary
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.first
import kotlin.time.Instant
import kotlin.time.toJavaDuration

private const val REFRESH_WORK_NAME = "favorites-widget-refresh"

/**
 * Re-renders the widget at the next instant its state changes on its own (a countdown day
 * rolling over, a favorite starting or ending) and schedules the run after it.
 */
class FavoritesWidgetRefreshWorker(
    context: Context,
    parameters: WorkerParameters,
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        FavoritesWidget().updateAll(applicationContext)
        // updateAll leaves a running session's composition in place, so the next run is scheduled
        // here rather than only from provideGlance. This worker holds the unique name while it
        // runs, so the next run is appended rather than replaced, which would cancel this one.
        val now = applicationContext.widgetDependencies.kaigiClock.now()
        nextFavoritesWidgetRefreshRequest(applicationContext, now)?.let { request ->
            WorkManager.getInstance(applicationContext)
                .enqueueUniqueWork(REFRESH_WORK_NAME, ExistingWorkPolicy.APPEND_OR_REPLACE, request)
        }
        return Result.success()
    }
}

internal suspend fun scheduleFavoritesWidgetRefresh(context: Context, now: Instant) {
    val workManager = WorkManager.getInstance(context)
    val request = nextFavoritesWidgetRefreshRequest(context, now)
    if (request == null) {
        workManager.cancelUniqueWork(REFRESH_WORK_NAME)
    } else {
        workManager.enqueueUniqueWork(REFRESH_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }
}

private suspend fun nextFavoritesWidgetRefreshRequest(context: Context, now: Instant): OneTimeWorkRequest? {
    val dependencies = context.widgetDependencies
    val timetable = dependencies.persistedTimetableReader.read() ?: Timetable(items = persistentListOf())
    val favoriteIds = dependencies.favoritesStore.favoriteIds().first()
    val boundary = nextFavoritesWidgetBoundary(now, timetable, favoriteIds) ?: return null
    return OneTimeWorkRequestBuilder<FavoritesWidgetRefreshWorker>()
        .setInitialDelay((boundary - now).toJavaDuration())
        .build()
}

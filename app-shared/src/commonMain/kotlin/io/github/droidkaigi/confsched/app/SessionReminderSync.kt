package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiClock
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.data.FavoritesStore
import io.github.droidkaigi.confsched.core.data.PersistedTimetableReader
import io.github.droidkaigi.confsched.core.data.ServerEnvironmentStore
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.computeSessionReminders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@Inject
@SingleIn(AppScope::class)
class SessionReminderSync(
    private val favoritesStore: FavoritesStore,
    private val persistedTimetableReader: PersistedTimetableReader,
    private val serverEnvironmentStore: ServerEnvironmentStore,
    private val kaigiClock: KaigiClock,
    private val scheduler: SessionReminderScheduler,
    private val logger: KaigiLogger,
) {
    fun start(scope: CoroutineScope) {
        scope.launch {
            combine(favoritesStore.favoriteIds(), kaigiClock.offset, serverEnvironmentStore.environment) { favoriteIds, _, _ -> favoriteIds }
                .combine(persistedTimetableReader.updates.onStart { emit(Unit) }) { favoriteIds, _ -> favoriteIds }
                .collect { reschedule(it) }
        }
    }

    suspend fun rescheduleNow() {
        reschedule(favoritesStore.favoriteIds().first())
    }

    private suspend fun reschedule(favoriteIds: Set<TimetableItemId>) {
        try {
            // Without a readable timetable nothing can be computed, so the existing schedule is kept.
            val timetable = persistedTimetableReader.read() ?: return
            scheduler.reschedule(computeSessionReminders(kaigiClock.now(), timetable, favoriteIds))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            logger.error(e) { "Failed to reschedule the session reminders" }
        }
    }
}

package io.github.droidkaigi.confsched.app.widget

import io.github.droidkaigi.confsched.core.model.FavoritesWidgetState
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.computeFavoritesWidgetState
import io.github.droidkaigi.confsched.core.model.nextFavoritesWidgetBoundary
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transformLatest
import kotlin.time.Duration
import kotlin.time.Instant

/** Everything one widget render needs. */
internal data class FavoritesWidgetRender(
    val state: FavoritesWidgetState,
    val colors: FavoritesWidgetColors,
    val mascot: WidgetMascot,
)

/**
 * Re-renders on every input change and, while collected, again at each instant the state changes
 * on its own; [clockOffsets] is an input so the debug clock drives the widget too.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun favoritesWidgetRenders(
    favoriteIds: Flow<Set<TimetableItemId>>,
    colorSchemes: Flow<KaigiColorScheme>,
    clockOffsets: Flow<Duration>,
    timetables: Flow<Timetable?>,
    now: () -> Instant,
): Flow<FavoritesWidgetRender> = combine(favoriteIds, colorSchemes, clockOffsets, timetables) { ids, scheme, _, timetable ->
    Triple(ids, scheme, timetable ?: Timetable(items = persistentListOf()))
}.transformLatest { (ids, scheme, timetable) ->
    val colors = scheme.toFavoritesWidgetColors()
    while (true) {
        val current = now()
        emit(FavoritesWidgetRender(computeFavoritesWidgetState(current, timetable, ids), colors, randomWidgetMascot(current)))
        val boundary = nextFavoritesWidgetBoundary(current, timetable, ids) ?: break
        delay(boundary - current)
    }
}

internal fun WidgetDependencies.favoritesWidgetRenders(): Flow<FavoritesWidgetRender> =
    favoritesWidgetRenders(
        favoriteIds = favoritesStore.favoriteIds(),
        colorSchemes = appearanceSettingsStore.colorScheme(),
        clockOffsets = kaigiClock.offset,
        timetables = persistedTimetableReader.timetables(),
        now = kaigiClock::now,
    )

package io.github.droidkaigi.confsched.app.widget

import io.github.droidkaigi.confsched.core.model.FavoritesWidgetState
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.computeFavoritesWidgetState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.time.Instant

/** Everything one widget render needs. */
internal data class FavoritesWidgetRender(
    val state: FavoritesWidgetState,
    val colors: FavoritesWidgetColors,
)

internal fun favoritesWidgetRenders(
    favoriteIds: Flow<Set<TimetableItemId>>,
    colorSchemes: Flow<KaigiColorScheme>,
    readTimetable: suspend () -> Timetable?,
    now: () -> Instant,
): Flow<FavoritesWidgetRender> = combine(favoriteIds, colorSchemes) { ids, scheme ->
    FavoritesWidgetRender(
        state = computeFavoritesWidgetState(
            now = now(),
            timetable = readTimetable() ?: Timetable(items = persistentListOf()),
            favoriteIds = ids,
        ),
        colors = scheme.toFavoritesWidgetColors(),
    )
}

internal fun WidgetDependencies.favoritesWidgetRenders(): Flow<FavoritesWidgetRender> =
    favoritesWidgetRenders(
        favoriteIds = favoritesStore.favoriteIds(),
        colorSchemes = appearanceSettingsStore.colorScheme(),
        readTimetable = persistedTimetableReader::read,
        now = kaigiClock::now,
    )

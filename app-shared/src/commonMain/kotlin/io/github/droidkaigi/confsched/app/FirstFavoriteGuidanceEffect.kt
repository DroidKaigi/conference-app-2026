package io.github.droidkaigi.confsched.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.droidkaigi.confsched.core.common.TargetPlatform
import io.github.droidkaigi.confsched.core.common.currentPlatform
import io.github.droidkaigi.confsched.core.data.FavoritesStore
import io.github.droidkaigi.confsched.core.data.FirstFavoriteGuidanceStore
import io.github.droidkaigi.confsched.core.data.PersistedTimetableReader
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.mascot
import kotlinx.coroutines.flow.first

/**
 * Offers the first-favorite guidance the first time a favorite is added, on the platforms that
 * can act on it: the desktop and the web post no notifications and have no home screen widget.
 */
@Composable
internal fun FirstFavoriteGuidanceEffect(
    favoritesStore: FavoritesStore,
    firstFavoriteGuidanceStore: FirstFavoriteGuidanceStore,
    persistedTimetableReader: PersistedTimetableReader,
    onOfferGuidance: (Mascot) -> Unit,
) {
    LaunchedEffect(favoritesStore, firstFavoriteGuidanceStore, persistedTimetableReader, onOfferGuidance) {
        if (currentPlatform != TargetPlatform.Android && currentPlatform != TargetPlatform.Ios) return@LaunchedEffect
        // The first emission is what the previous run left behind; the guidance follows a
        // favorite the reader adds while looking at the app.
        var previousIds: Set<TimetableItemId>? = null
        favoritesStore.favoriteIds().collect { ids ->
            val added = previousIds?.let { ids - it }?.singleOrNull()
            previousIds = ids
            if (added != null && !firstFavoriteGuidanceStore.consumed().first()) {
                onOfferGuidance(persistedTimetableReader.roomMascotOf(added))
            }
        }
    }
}

private suspend fun PersistedTimetableReader.roomMascotOf(id: TimetableItemId): Mascot =
    (read()?.items?.firstOrNull { it.id == id }?.room ?: SessionRoom.UNKNOWN).mascot

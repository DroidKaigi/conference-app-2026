package io.github.droidkaigi.confsched.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.droidkaigi.confsched.core.common.TargetPlatform
import io.github.droidkaigi.confsched.core.common.currentPlatform
import io.github.droidkaigi.confsched.core.data.FavoritesStore
import io.github.droidkaigi.confsched.core.data.FirstFavoriteGuidanceStore
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.coroutines.flow.first

/**
 * Offers the first-favorite guidance the first time a favorite is added, on the platforms that
 * can act on it: the desktop and the web post no notifications and have no home screen widget.
 */
@Composable
internal fun FirstFavoriteGuidanceEffect(
    favoritesStore: FavoritesStore,
    firstFavoriteGuidanceStore: FirstFavoriteGuidanceStore,
    onOfferGuidance: () -> Unit,
) {
    LaunchedEffect(favoritesStore, firstFavoriteGuidanceStore, onOfferGuidance) {
        if (currentPlatform != TargetPlatform.Android && currentPlatform != TargetPlatform.Ios) return@LaunchedEffect
        // The first emission is what the previous run left behind; the guidance follows a
        // favorite the reader adds while looking at the app.
        var previousIds: Set<TimetableItemId>? = null
        favoritesStore.favoriteIds().collect { ids ->
            val added = previousIds?.let { ids.size > it.size } == true
            previousIds = ids
            if (added && !firstFavoriteGuidanceStore.consumed().first()) {
                onOfferGuidance()
            }
        }
    }
}

package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.TargetPlatform
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.currentPlatform
import io.github.droidkaigi.confsched.core.data.FirstFavoriteGuidanceStore
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.mascot
import io.github.droidkaigi.confsched.feature.favorites.FirstFavoriteNotificationNavKey
import kotlinx.coroutines.flow.first

/**
 * Offers the guidance that follows an added favorite, on the platforms that can act on it: the
 * desktop and the web post no notifications and have no home screen widget.
 */
@Inject
@SingleIn(UiScope::class)
class FirstFavoriteGuidance(
    private val appNavigator: AppNavigator,
    private val firstFavoriteGuidanceStore: FirstFavoriteGuidanceStore,
) {
    suspend fun offer(room: SessionRoom) {
        if (currentPlatform != TargetPlatform.Android && currentPlatform != TargetPlatform.Ios) return
        if (firstFavoriteGuidanceStore.consumed().first()) return
        appNavigator.goTo(FirstFavoriteNotificationNavKey(room.mascot))
    }
}

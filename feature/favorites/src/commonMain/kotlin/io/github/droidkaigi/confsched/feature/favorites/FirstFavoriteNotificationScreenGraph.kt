package io.github.droidkaigi.confsched.feature.favorites

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.FirstFavoriteNotificationScreenScope
import io.github.droidkaigi.confsched.core.model.MutationTag

@GraphExtension(FirstFavoriteNotificationScreenScope::class)
interface FirstFavoriteNotificationScreenGraph {
    val screenContext: FirstFavoriteNotificationScreenContext

    val screenNavigator: FirstFavoriteNotificationScreenNavigator

    @Provides
    private fun provideMutationTag(): MutationTag = MutationTag("FirstFavoriteNotificationScreen")

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createFirstFavoriteNotificationScreenGraph(): FirstFavoriteNotificationScreenGraph
    }
}

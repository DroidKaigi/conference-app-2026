package io.github.droidkaigi.confsched.feature.favorites

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.FirstFavoriteWidgetScreenScope

@GraphExtension(FirstFavoriteWidgetScreenScope::class)
interface FirstFavoriteWidgetScreenGraph {
    val screenContext: FirstFavoriteWidgetScreenContext

    val screenNavigator: FirstFavoriteWidgetScreenNavigator

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createFirstFavoriteWidgetScreenGraph(): FirstFavoriteWidgetScreenGraph
    }
}

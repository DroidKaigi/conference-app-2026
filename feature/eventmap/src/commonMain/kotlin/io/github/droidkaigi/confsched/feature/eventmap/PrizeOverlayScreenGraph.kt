package io.github.droidkaigi.confsched.feature.eventmap

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.PrizeOverlayScreenScope

@GraphExtension(PrizeOverlayScreenScope::class)
interface PrizeOverlayScreenGraph {
    val screenContext: PrizeOverlayScreenContext

    val screenNavigator: PrizeOverlayScreenNavigator

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createPrizeOverlayScreenGraph(@Provides navKey: PrizeOverlayNavKey): PrizeOverlayScreenGraph
    }
}

package io.github.droidkaigi.confsched.feature.eventmap

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.StampCollectingScreenScope

@GraphExtension(StampCollectingScreenScope::class)
interface StampCollectingScreenGraph {
    val screenContext: StampCollectingScreenContext

    val screenNavigator: StampCollectingScreenNavigator

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createStampCollectingScreenGraph(): StampCollectingScreenGraph
    }
}

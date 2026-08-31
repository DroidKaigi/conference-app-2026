package io.github.droidkaigi.confsched.feature.about

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.DoodleScreenScope

@GraphExtension(DoodleScreenScope::class)
interface DoodleScreenGraph {
    val screenContext: DoodleScreenContext

    val screenNavigator: DoodleScreenNavigator

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createDoodleScreenGraph(): DoodleScreenGraph
    }
}

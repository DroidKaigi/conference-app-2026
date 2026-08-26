package io.github.droidkaigi.confsched.feature.debug

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.SoilErrorsScreenScope

@GraphExtension(SoilErrorsScreenScope::class)
interface SoilErrorsScreenGraph {
    val screenContext: SoilErrorsScreenContext

    val screenNavigator: SoilErrorsScreenNavigator

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createSoilErrorsScreenGraph(): SoilErrorsScreenGraph
    }
}

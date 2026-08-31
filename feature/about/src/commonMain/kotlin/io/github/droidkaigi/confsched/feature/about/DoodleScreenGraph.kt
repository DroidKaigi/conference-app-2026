package io.github.droidkaigi.confsched.feature.about

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.DoodleScreenScope
import io.github.droidkaigi.confsched.core.model.MutationTag

@GraphExtension(DoodleScreenScope::class)
interface DoodleScreenGraph {
    val screenContext: DoodleScreenContext

    val screenNavigator: DoodleScreenNavigator

    @Provides
    private fun provideMutationTag(): MutationTag = MutationTag("DoodleScreen")

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createDoodleScreenGraph(): DoodleScreenGraph
    }
}

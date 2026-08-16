package io.github.droidkaigi.confsched.feature.debug

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.DebugScreenScope
import io.github.droidkaigi.confsched.core.model.MutationTag

@GraphExtension(DebugScreenScope::class)
interface DebugScreenGraph {
    val screenContext: DebugScreenContext

    val navigator: DebugScreenNavigator

    @Provides
    private fun provideMutationTag(): MutationTag = MutationTag("DebugScreen")

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createDebugScreenGraph(): DebugScreenGraph
    }
}

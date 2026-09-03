package io.github.droidkaigi.confsched.feature.about

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.AboutScreenScope
import io.github.droidkaigi.confsched.core.model.MutationTag

@GraphExtension(AboutScreenScope::class)
interface AboutScreenGraph {
    val screenContext: AboutScreenContext

    val screenNavigator: AboutScreenNavigator

    @Provides
    private fun provideMutationTag(): MutationTag = MutationTag("AboutScreen")

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createAboutScreenGraph(): AboutScreenGraph
    }
}

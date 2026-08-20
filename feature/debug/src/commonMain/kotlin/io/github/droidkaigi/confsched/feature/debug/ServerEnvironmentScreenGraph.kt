package io.github.droidkaigi.confsched.feature.debug

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.common.Navigator
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.ServerEnvironmentScreenScope

@GraphExtension(ServerEnvironmentScreenScope::class)
interface ServerEnvironmentScreenGraph {
    val screenContext: ServerEnvironmentScreenContext
    val screenNavigator: ServerEnvironmentScreenNavigator

    @Provides
    private fun provideMutationTag(): MutationTag = MutationTag("ServerEnvironmentScreen")

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createServerEnvironmentScreenGraph(): ServerEnvironmentScreenGraph
    }
}

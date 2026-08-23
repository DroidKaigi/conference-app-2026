package io.github.droidkaigi.confsched.feature.settings

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.SettingsScreenScope

@GraphExtension(SettingsScreenScope::class)
interface SettingsScreenGraph {
    val screenContext: SettingsScreenContext

    val screenNavigator: SettingsScreenNavigator

    @Provides
    private fun provideMutationTag(): MutationTag = MutationTag("SettingsScreen")

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createSettingsScreenGraph(): SettingsScreenGraph
    }
}

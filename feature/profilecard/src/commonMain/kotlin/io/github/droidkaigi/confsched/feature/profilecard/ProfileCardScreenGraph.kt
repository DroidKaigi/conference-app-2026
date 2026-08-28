package io.github.droidkaigi.confsched.feature.profilecard

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.ProfileCardScreenScope

@GraphExtension(ProfileCardScreenScope::class)
interface ProfileCardScreenGraph {
    val screenContext: ProfileCardScreenContext
    val screenNavigator: ProfileCardScreenNavigator

    @Provides
    private fun provideMutationTag(): MutationTag = MutationTag("ProfileCardScreen")

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createProfileCardScreenGraph(): ProfileCardScreenGraph
    }
}

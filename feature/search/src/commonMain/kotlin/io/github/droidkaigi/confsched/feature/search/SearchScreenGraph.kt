package io.github.droidkaigi.confsched.feature.search

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.SearchScreenScope

@GraphExtension(SearchScreenScope::class)
interface SearchScreenGraph {
    val screenContext: SearchScreenContext

    val screenNavigator: SearchScreenNavigator

    @Provides
    private fun provideMutationTag(): MutationTag = MutationTag("SearchScreen")

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createSearchScreenGraph(): SearchScreenGraph
    }
}

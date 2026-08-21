package io.github.droidkaigi.confsched.feature.search

import dev.zacsweers.metro.DependencyGraph
import io.github.droidkaigi.confsched.core.model.SearchScreenScope
import io.github.droidkaigi.confsched.core.testing.FakeFavoriteTimetableIdsSubscriptionKey
import io.github.droidkaigi.confsched.core.testing.FakeFavoriteTimetableItemIdMutationKey
import io.github.droidkaigi.confsched.core.testing.FakeTimetableQueryKey
import io.github.droidkaigi.confsched.core.testing.TestingScope

@DependencyGraph(scope = TestingScope::class, additionalScopes = [SearchScreenScope::class])
interface SearchScreenTestGraph {
    val screenContext: SearchScreenContext
    val presenterContext: SearchPresenterContext
    val timetableQueryKey: FakeTimetableQueryKey
    val favoriteIdsSubscriptionKey: FakeFavoriteTimetableIdsSubscriptionKey
    val favoriteMutationKey: FakeFavoriteTimetableItemIdMutationKey
}

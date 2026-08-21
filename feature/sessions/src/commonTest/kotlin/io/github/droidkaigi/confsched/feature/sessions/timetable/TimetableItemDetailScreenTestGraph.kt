package io.github.droidkaigi.confsched.feature.sessions.timetable

import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.model.TimetableItemDetailScreenScope
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.testing.FakeFavoriteTimetableItemIdMutationKey
import io.github.droidkaigi.confsched.core.testing.FakeSessionMemoMutationKey
import io.github.droidkaigi.confsched.core.testing.FakeTimetableQueryKey
import io.github.droidkaigi.confsched.core.testing.TestingScope

@DependencyGraph(scope = TestingScope::class, additionalScopes = [TimetableItemDetailScreenScope::class])
interface TimetableItemDetailScreenTestGraph {
    val screenContext: TimetableItemDetailScreenContext
    val presenterContext: TimetableItemDetailPresenterContext
    val timetableQueryKey: FakeTimetableQueryKey
    val favoriteMutationKey: FakeFavoriteTimetableItemIdMutationKey
    val memoMutationKey: FakeSessionMemoMutationKey

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides timetableItemId: TimetableItemId): TimetableItemDetailScreenTestGraph
    }
}

package io.github.droidkaigi.confsched.feature.sessions.timetable

import dev.zacsweers.metro.DependencyGraph
import io.github.droidkaigi.confsched.core.model.TimetableItemDetailScreenScope
import io.github.droidkaigi.confsched.core.testing.FakeFavoriteTimetableItemIdMutationKey
import io.github.droidkaigi.confsched.core.testing.FakeSessionMemoMutationKey
import io.github.droidkaigi.confsched.core.testing.TestingScope

@DependencyGraph(scope = TestingScope::class, additionalScopes = [TimetableItemDetailScreenScope::class])
interface TimetableItemDetailScreenTestGraph {
    val presenterContext: TimetableItemDetailPresenterContext
    val favoriteMutationKey: FakeFavoriteTimetableItemIdMutationKey
    val memoMutationKey: FakeSessionMemoMutationKey
}

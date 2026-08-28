package io.github.droidkaigi.confsched.feature.sessions.timetable

import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.TimetableScreenScope
import io.github.droidkaigi.confsched.core.testing.FakeClock
import io.github.droidkaigi.confsched.core.testing.FakeFavoriteTimetableIdsSubscriptionKey
import io.github.droidkaigi.confsched.core.testing.FakeFavoriteTimetableItemIdMutationKey
import io.github.droidkaigi.confsched.core.testing.FakeKaigiLogger
import io.github.droidkaigi.confsched.core.testing.FakeTimetableQueryKey
import io.github.droidkaigi.confsched.core.testing.TestingScope

@DependencyGraph(scope = TestingScope::class, additionalScopes = [TimetableScreenScope::class])
interface TimetableScreenTestGraph {
    val screenContext: TimetableScreenContext
    val presenterContext: TimetablePresenterContext
    val timetableQueryKey: FakeTimetableQueryKey
    val favoriteIdsSubscriptionKey: FakeFavoriteTimetableIdsSubscriptionKey
    val favoriteMutationKey: FakeFavoriteTimetableItemIdMutationKey
    val logger: FakeKaigiLogger
    val clock: FakeClock
    val dayRequestStore: TimetableDayRequestStore

    @Provides
    @SingleIn(TestingScope::class)
    fun provideTimetableDayRequestStore(): TimetableDayRequestStore = TimetableDayRequestStore()
}

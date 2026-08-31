package io.github.droidkaigi.confsched.feature.doodle

import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.model.DoodleScreenScope
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.testing.FakeDoodleMutationKey
import io.github.droidkaigi.confsched.core.testing.FakeDoodlesSubscriptionKey
import io.github.droidkaigi.confsched.core.testing.FakeProfileCardSubscriptionKey
import io.github.droidkaigi.confsched.core.testing.TestingScope

@DependencyGraph(scope = TestingScope::class, additionalScopes = [DoodleScreenScope::class])
interface DoodleScreenTestGraph {
    val screenContext: DoodleScreenContext
    val presenterContext: DoodlePresenterContext
    val doodlesSubscriptionKey: FakeDoodlesSubscriptionKey
    val profileCardSubscriptionKey: FakeProfileCardSubscriptionKey
    val doodleMutationKey: FakeDoodleMutationKey

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides target: DoodleTarget): DoodleScreenTestGraph
    }
}

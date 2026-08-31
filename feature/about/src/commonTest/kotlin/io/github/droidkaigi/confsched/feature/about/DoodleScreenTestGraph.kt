package io.github.droidkaigi.confsched.feature.about

import dev.zacsweers.metro.DependencyGraph
import io.github.droidkaigi.confsched.core.model.DoodleScreenScope
import io.github.droidkaigi.confsched.core.testing.FakeDoodleMutationKey
import io.github.droidkaigi.confsched.core.testing.FakeDoodleSubscriptionKey
import io.github.droidkaigi.confsched.core.testing.TestingScope

@DependencyGraph(scope = TestingScope::class, additionalScopes = [DoodleScreenScope::class])
interface DoodleScreenTestGraph {
    val screenContext: DoodleScreenContext
    val presenterContext: DoodlePresenterContext
    val doodleSubscriptionKey: FakeDoodleSubscriptionKey
    val doodleMutationKey: FakeDoodleMutationKey
}

package io.github.droidkaigi.confsched.feature.about

import dev.zacsweers.metro.DependencyGraph
import io.github.droidkaigi.confsched.core.model.AboutScreenScope
import io.github.droidkaigi.confsched.core.testing.FakeDoodleMutationKey
import io.github.droidkaigi.confsched.core.testing.FakeDoodlesSubscriptionKey
import io.github.droidkaigi.confsched.core.testing.TestingScope

@DependencyGraph(scope = TestingScope::class, additionalScopes = [AboutScreenScope::class])
interface AboutScreenTestGraph {
    val screenContext: AboutScreenContext
    val presenterContext: AboutPresenterContext
    val doodlesSubscriptionKey: FakeDoodlesSubscriptionKey
    val doodleMutationKey: FakeDoodleMutationKey
}

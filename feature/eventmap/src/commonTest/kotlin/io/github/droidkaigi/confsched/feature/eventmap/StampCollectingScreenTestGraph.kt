package io.github.droidkaigi.confsched.feature.eventmap

import dev.zacsweers.metro.DependencyGraph
import io.github.droidkaigi.confsched.core.model.StampCollectingScreenScope
import io.github.droidkaigi.confsched.core.testing.FakePrizesQueryKey
import io.github.droidkaigi.confsched.core.testing.TestingScope

@DependencyGraph(scope = TestingScope::class, additionalScopes = [StampCollectingScreenScope::class])
interface StampCollectingScreenTestGraph {
    val screenContext: StampCollectingScreenContext
    val presenterContext: StampCollectingPresenterContext
    val prizesQueryKey: FakePrizesQueryKey
}

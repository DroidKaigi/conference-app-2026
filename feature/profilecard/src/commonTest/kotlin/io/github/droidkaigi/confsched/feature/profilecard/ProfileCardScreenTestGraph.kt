package io.github.droidkaigi.confsched.feature.profilecard

import dev.zacsweers.metro.DependencyGraph
import io.github.droidkaigi.confsched.core.testing.TestingScope

@DependencyGraph(scope = TestingScope::class)
interface ProfileCardScreenTestGraph {
    val presenterContext: ProfileCardPresenterContext
}

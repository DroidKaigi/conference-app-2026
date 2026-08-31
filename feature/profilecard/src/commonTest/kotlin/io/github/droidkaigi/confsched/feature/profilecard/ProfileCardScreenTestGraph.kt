package io.github.droidkaigi.confsched.feature.profilecard

import dev.zacsweers.metro.DependencyGraph
import io.github.droidkaigi.confsched.core.model.ProfileCardScreenScope
import io.github.droidkaigi.confsched.core.testing.FakeDoodlesSubscriptionKey
import io.github.droidkaigi.confsched.core.testing.FakeProfileCardMutationKey
import io.github.droidkaigi.confsched.core.testing.FakeProfileCardSubscriptionKey
import io.github.droidkaigi.confsched.core.testing.TestingScope

@DependencyGraph(scope = TestingScope::class, additionalScopes = [ProfileCardScreenScope::class])
interface ProfileCardScreenTestGraph {
    val screenContext: ProfileCardScreenContext
    val presenterContext: ProfileCardPresenterContext
    val profileCardSubscriptionKey: FakeProfileCardSubscriptionKey
    val profileCardMutationKey: FakeProfileCardMutationKey
    val doodlesSubscriptionKey: FakeDoodlesSubscriptionKey
}

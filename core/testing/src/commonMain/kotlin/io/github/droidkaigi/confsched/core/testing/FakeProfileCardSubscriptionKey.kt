package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.model.ProfileCardSubscriptionKey
import kotlinx.coroutines.flow.flow
import soil.query.SubscriptionId
import soil.query.buildSubscriptionKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class, binding = binding<ProfileCardSubscriptionKey>())
class FakeProfileCardSubscriptionKey private constructor(
    fixture: FakeFixture<ProfileCard?>,
) : FakeKeyControl<ProfileCard?>(fixture),
    ProfileCardSubscriptionKey by buildSubscriptionKey(
        id = SubscriptionId("fake-profile-card"),
        subscribe = { flow { emit(fixture.await()) } },
    ) {
    @Inject
    constructor() : this(FakeFixture(null))
}

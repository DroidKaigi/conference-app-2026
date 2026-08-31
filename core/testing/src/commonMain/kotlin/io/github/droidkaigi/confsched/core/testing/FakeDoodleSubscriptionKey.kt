package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleSubscriptionKey
import kotlinx.coroutines.flow.flow
import soil.query.SubscriptionId
import soil.query.buildSubscriptionKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class, binding = binding<DoodleSubscriptionKey>())
class FakeDoodleSubscriptionKey private constructor(
    fixture: FakeFixture<Doodle>,
) : FakeKeyControl<Doodle>(fixture),
    DoodleSubscriptionKey by buildSubscriptionKey(
        id = SubscriptionId("fake-doodle"),
        subscribe = { flow { emit(fixture.await()) } },
    ) {
    @Inject
    constructor() : this(FakeFixture(Doodle.Empty))
}

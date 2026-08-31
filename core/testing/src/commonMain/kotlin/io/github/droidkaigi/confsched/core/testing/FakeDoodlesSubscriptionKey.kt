package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.DoodlesSubscriptionKey
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.flow
import soil.query.SubscriptionId
import soil.query.buildSubscriptionKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class, binding = binding<DoodlesSubscriptionKey>())
class FakeDoodlesSubscriptionKey private constructor(
    fixture: FakeFixture<PersistentMap<DoodleTarget, Doodle>>,
) : FakeKeyControl<PersistentMap<DoodleTarget, Doodle>>(fixture),
    DoodlesSubscriptionKey by buildSubscriptionKey(
        id = SubscriptionId("fake-doodles"),
        subscribe = { flow { emit(fixture.await()) } },
    ) {
    @Inject
    constructor() : this(FakeFixture(persistentMapOf()))
}

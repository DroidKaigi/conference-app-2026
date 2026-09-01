package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.model.FirstFavoriteGuidanceConsumedSubscriptionKey
import kotlinx.coroutines.flow.flow
import soil.query.SubscriptionId
import soil.query.buildSubscriptionKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class, binding = binding<FirstFavoriteGuidanceConsumedSubscriptionKey>())
class FakeFirstFavoriteGuidanceConsumedSubscriptionKey private constructor(
    fixture: FakeFixture<Boolean>,
) : FakeKeyControl<Boolean>(fixture),
    FirstFavoriteGuidanceConsumedSubscriptionKey by buildSubscriptionKey(
        id = SubscriptionId("fake-first-favorite-guidance-consumed"),
        subscribe = { flow { emit(fixture.await()) } },
    ) {
    @Inject
    constructor() : this(FakeFixture(false))
}

package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.model.FirstFavoriteGuidanceOfferableSubscriptionKey
import kotlinx.coroutines.flow.flow
import soil.query.SubscriptionId
import soil.query.buildSubscriptionKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class, binding = binding<FirstFavoriteGuidanceOfferableSubscriptionKey>())
class FakeFirstFavoriteGuidanceOfferableSubscriptionKey private constructor(
    fixture: FakeFixture<Boolean>,
) : FakeKeyControl<Boolean>(fixture),
    FirstFavoriteGuidanceOfferableSubscriptionKey by buildSubscriptionKey(
        id = SubscriptionId("fake-first-favorite-guidance-offerable"),
        subscribe = { flow { emit(fixture.await()) } },
    ) {
    @Inject
    constructor() : this(FakeFixture(false))
}

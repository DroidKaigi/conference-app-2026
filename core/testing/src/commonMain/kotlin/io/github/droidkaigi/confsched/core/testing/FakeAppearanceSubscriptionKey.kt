package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.model.Appearance
import io.github.droidkaigi.confsched.core.model.AppearanceSettings
import io.github.droidkaigi.confsched.core.model.AppearanceSubscriptionKey
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import kotlinx.coroutines.flow.flow
import soil.query.SubscriptionId
import soil.query.buildSubscriptionKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class, binding = binding<AppearanceSubscriptionKey>())
class FakeAppearanceSubscriptionKey private constructor(
    fixture: FakeFixture<Appearance>,
) : FakeKeyControl<Appearance>(fixture),
    AppearanceSubscriptionKey by buildSubscriptionKey(
        id = SubscriptionId("fake-appearance"),
        subscribe = { flow { emit(fixture.await()) } },
    ) {
    @Inject
    constructor() : this(
        FakeFixture(
            Appearance(
                // A scheme the fake pins, so a test that only sets the settings still draws one.
                colorScheme = KaigiColorScheme.MorningMist,
                settings = AppearanceSettings.Default,
            ),
        ),
    )
}

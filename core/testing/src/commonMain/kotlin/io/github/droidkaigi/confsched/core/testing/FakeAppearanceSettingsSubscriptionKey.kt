package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.model.AppearanceSettings
import io.github.droidkaigi.confsched.core.model.AppearanceSettingsSubscriptionKey
import kotlinx.coroutines.flow.flow
import soil.query.SubscriptionId
import soil.query.buildSubscriptionKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class, binding = binding<AppearanceSettingsSubscriptionKey>())
class FakeAppearanceSettingsSubscriptionKey private constructor(
    fixture: FakeFixture<AppearanceSettings>,
) : FakeKeyControl<AppearanceSettings>(fixture),
    AppearanceSettingsSubscriptionKey by buildSubscriptionKey(
        id = SubscriptionId("fake-appearance-settings"),
        subscribe = { flow { emit(fixture.await()) } },
    ) {
    @Inject
    constructor() : this(FakeFixture(AppearanceSettings.Default))
}

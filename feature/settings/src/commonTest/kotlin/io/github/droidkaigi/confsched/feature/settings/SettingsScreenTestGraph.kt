package io.github.droidkaigi.confsched.feature.settings

import dev.zacsweers.metro.DependencyGraph
import io.github.droidkaigi.confsched.core.model.SettingsScreenScope
import io.github.droidkaigi.confsched.core.testing.FakeAppearanceSettingsMutationKey
import io.github.droidkaigi.confsched.core.testing.FakeAppearanceSubscriptionKey
import io.github.droidkaigi.confsched.core.testing.TestingScope

@DependencyGraph(scope = TestingScope::class, additionalScopes = [SettingsScreenScope::class])
interface SettingsScreenTestGraph {
    val screenContext: SettingsScreenContext
    val presenterContext: SettingsPresenterContext
    val appearanceSubscriptionKey: FakeAppearanceSubscriptionKey
    val appearanceSettingsMutationKey: FakeAppearanceSettingsMutationKey
}

package io.github.droidkaigi.confsched.feature.settings

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.AppearanceSettingsMutationKey
import io.github.droidkaigi.confsched.core.model.AppearanceSettingsSubscriptionKey
import io.github.droidkaigi.confsched.core.model.SettingsScreenScope

@Inject
class SettingsPresenterContext(
    val appearanceSettingsMutationKey: AppearanceSettingsMutationKey,
) : PresenterContext

@Inject
@SingleIn(SettingsScreenScope::class)
class SettingsScreenContext(
    val appearanceSettingsSubscriptionKey: AppearanceSettingsSubscriptionKey,
    val presenterContext: SettingsPresenterContext,
) : ScreenContext

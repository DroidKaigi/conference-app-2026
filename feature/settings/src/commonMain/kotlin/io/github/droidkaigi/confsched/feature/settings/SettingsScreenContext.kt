package io.github.droidkaigi.confsched.feature.settings

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.AppearanceSettingsMutationKey
import io.github.droidkaigi.confsched.core.model.AppearanceSubscriptionKey
import io.github.droidkaigi.confsched.core.model.SettingsScreenScope

@Inject
class SettingsPresenterContext(
    val appearanceSettingsMutationKey: AppearanceSettingsMutationKey,
    override val logger: KaigiLogger,
) : PresenterContext

@Inject
@SingleIn(SettingsScreenScope::class)
class SettingsScreenContext(
    val appearanceSubscriptionKey: AppearanceSubscriptionKey,
    override val logger: KaigiLogger,
    val presenterContext: SettingsPresenterContext,
) : ScreenContext

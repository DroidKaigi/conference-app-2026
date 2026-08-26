package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.SettingsScreenScope
import io.github.droidkaigi.confsched.feature.settings.SettingsScreenNavigator

@Inject
@SingleIn(SettingsScreenScope::class)
@ContributesBinding(
    scope = SettingsScreenScope::class,
    binding = binding<SettingsScreenNavigator>(),
)
class DefaultSettingsScreenNavigator(
    appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator),
    SettingsScreenNavigator

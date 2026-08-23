package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.model.SettingsScreenScope
import io.github.droidkaigi.confsched.feature.settings.SettingsNavKey
import io.github.droidkaigi.confsched.feature.settings.SettingsScreenNavigator

@Inject
@SingleIn(SettingsScreenScope::class)
@ContributesBinding(SettingsScreenScope::class)
class DefaultSettingsScreenNavigator(
    private val appNavigator: AppNavigator,
) : SettingsScreenNavigator {
    // The origin is what lets NavigatorEffect drop a pop raised from an entry no longer on top.
    override fun back() {
        appNavigator.back(origin = SettingsNavKey)
    }
}

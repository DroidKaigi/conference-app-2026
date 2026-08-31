package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DebugNavKeyProvider
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.AboutScreenScope
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.feature.about.AboutScreenNavigator
import io.github.droidkaigi.confsched.feature.about.LicensesNavKey
import io.github.droidkaigi.confsched.feature.contributors.ContributorsNavKey
import io.github.droidkaigi.confsched.feature.doodle.DoodleNavKey
import io.github.droidkaigi.confsched.feature.settings.SettingsNavKey
import io.github.droidkaigi.confsched.feature.sponsors.SponsorsNavKey
import io.github.droidkaigi.confsched.feature.staff.StaffNavKey

@Inject
@SingleIn(AboutScreenScope::class)
@ContributesBinding(
    scope = AboutScreenScope::class,
    binding = binding<AboutScreenNavigator>(),
)
class DefaultAboutScreenNavigator(
    private val appNavigator: AppNavigator,
    private val debugNavKeyProvider: DebugNavKeyProvider,
) : DefaultScreenNavigator(appNavigator),
    AboutScreenNavigator {
    override fun openSettings() {
        appNavigator.goTo(SettingsNavKey)
    }

    override fun openSponsors() {
        appNavigator.goTo(SponsorsNavKey)
    }

    override fun openContributors() {
        appNavigator.goTo(ContributorsNavKey)
    }

    override fun openStaff() {
        appNavigator.goTo(StaffNavKey)
    }

    override fun openLicenses() {
        appNavigator.goTo(LicensesNavKey)
    }

    override fun openDoodle() {
        appNavigator.goTo(DoodleNavKey(DoodleTarget.AboutWall))
    }

    override val isDebugMenuAvailable: Boolean get() = debugNavKeyProvider.debugNavKey != null

    override fun openDebug() {
        debugNavKeyProvider.debugNavKey?.let(appNavigator::goTo)
    }
}

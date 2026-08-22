package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DebugNavKeyProvider
import io.github.droidkaigi.confsched.core.model.AboutScreenScope
import io.github.droidkaigi.confsched.feature.about.AboutScreenNavigator
import io.github.droidkaigi.confsched.feature.about.LicensesNavKey
import io.github.droidkaigi.confsched.feature.contributors.ContributorsNavKey
import io.github.droidkaigi.confsched.feature.eventmap.EventMapNavKey
import io.github.droidkaigi.confsched.feature.sponsors.SponsorsNavKey
import io.github.droidkaigi.confsched.feature.staff.StaffNavKey

@Inject
@SingleIn(AboutScreenScope::class)
@ContributesBinding(AboutScreenScope::class)
class DefaultAboutScreenNavigator(
    private val appNavigator: AppNavigator,
    private val debugNavKeyProvider: DebugNavKeyProvider,
) : AboutScreenNavigator {
    // EventMap is a root tab, so raise it rather than pushing a second copy onto the stack.
    override fun openEventMap() {
        appNavigator.moveToTop(EventMapNavKey)
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

    override val isDebugMenuAvailable: Boolean get() = debugNavKeyProvider.debugNavKey != null

    override fun openDebug() {
        debugNavKeyProvider.debugNavKey?.let(appNavigator::goTo)
    }
}

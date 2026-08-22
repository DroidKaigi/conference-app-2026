package io.github.droidkaigi.confsched.feature.about

import io.github.droidkaigi.confsched.core.common.Navigator

interface AboutScreenNavigator : Navigator {
    fun openEventMap()

    fun openSponsors()

    fun openContributors()

    fun openStaff()

    fun openLicenses()

    /** False when the build does not include the debug feature. */
    val isDebugMenuAvailable: Boolean
    fun openDebug()
}

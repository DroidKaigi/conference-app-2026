package io.github.droidkaigi.confsched.feature.favorites

import io.github.droidkaigi.confsched.core.common.Navigator

interface FirstFavoriteNotificationScreenNavigator : Navigator {
    /** Puts the widget step in this step's place, so back does not return to a dialog already answered. */
    fun openWidgetStep()
}

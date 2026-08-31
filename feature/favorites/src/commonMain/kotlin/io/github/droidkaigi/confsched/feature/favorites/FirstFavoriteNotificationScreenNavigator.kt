package io.github.droidkaigi.confsched.feature.favorites

import io.github.droidkaigi.confsched.core.common.Navigator
import io.github.droidkaigi.confsched.core.model.Mascot

interface FirstFavoriteNotificationScreenNavigator : Navigator {
    /** Puts the widget step in this step's place, so back does not return to a dialog already answered. */
    fun openWidgetStep(mascot: Mascot)
}

package io.github.droidkaigi.confsched.feature.profilecard

import io.github.droidkaigi.confsched.core.common.Navigator
import io.github.droidkaigi.confsched.core.model.DoodleTarget

interface ProfileCardScreenNavigator : Navigator {
    fun openDoodle(target: DoodleTarget)
}

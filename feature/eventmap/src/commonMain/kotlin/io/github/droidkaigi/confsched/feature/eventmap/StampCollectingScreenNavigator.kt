package io.github.droidkaigi.confsched.feature.eventmap

import io.github.droidkaigi.confsched.core.common.Navigator

interface StampCollectingScreenNavigator : Navigator {
    fun openPrize(page: Int)
}

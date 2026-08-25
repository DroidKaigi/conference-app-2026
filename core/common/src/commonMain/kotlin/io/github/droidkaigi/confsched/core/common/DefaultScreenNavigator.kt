package io.github.droidkaigi.confsched.core.common

import androidx.navigation3.runtime.NavKey

abstract class DefaultScreenNavigator(
    private val appNavigator: AppNavigator,
) : Navigator {
    override fun back(origin: NavKey?) {
        appNavigator.back()
    }
}

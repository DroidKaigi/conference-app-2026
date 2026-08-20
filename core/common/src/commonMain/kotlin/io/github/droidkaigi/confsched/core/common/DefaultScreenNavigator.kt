package io.github.droidkaigi.confsched.core.common

abstract class DefaultScreenNavigator(
    private val appNavigator: AppNavigator,
) : Navigator {
    override fun back() {
        appNavigator.back()
    }
}

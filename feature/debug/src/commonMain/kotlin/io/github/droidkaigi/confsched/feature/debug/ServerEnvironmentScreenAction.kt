package io.github.droidkaigi.confsched.feature.debug

import io.github.droidkaigi.confsched.core.data.ServerEnvironment

sealed interface ServerEnvironmentScreenAction {
    data class SelectServer(val environment: ServerEnvironment) : ServerEnvironmentScreenAction

    data class SetSkipNextLaunch(val enabled: Boolean) : ServerEnvironmentScreenAction
}

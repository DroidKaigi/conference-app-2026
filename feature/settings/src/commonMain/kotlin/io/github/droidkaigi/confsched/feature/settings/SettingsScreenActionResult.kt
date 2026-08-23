package io.github.droidkaigi.confsched.feature.settings

import io.github.droidkaigi.confsched.core.common.UserMessage

sealed interface SettingsScreenActionResult {
    data class ShowMessage(val message: UserMessage) : SettingsScreenActionResult
}

package io.github.droidkaigi.confsched.feature.about

import io.github.droidkaigi.confsched.core.common.UserMessage

sealed interface AboutScreenActionResult {
    data class ShowMessage(val message: UserMessage) : AboutScreenActionResult
}

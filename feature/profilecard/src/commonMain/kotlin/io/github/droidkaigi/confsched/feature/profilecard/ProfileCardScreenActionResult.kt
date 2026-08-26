package io.github.droidkaigi.confsched.feature.profilecard

import io.github.droidkaigi.confsched.core.common.UserMessage

sealed interface ProfileCardScreenActionResult {
    data class ShowMessage(val message: UserMessage) : ProfileCardScreenActionResult
}

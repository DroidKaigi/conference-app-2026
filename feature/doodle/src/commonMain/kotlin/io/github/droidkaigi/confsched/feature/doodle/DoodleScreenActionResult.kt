package io.github.droidkaigi.confsched.feature.doodle

import io.github.droidkaigi.confsched.core.common.UserMessage

sealed interface DoodleScreenActionResult {
    data object Saved : DoodleScreenActionResult

    data class ShowMessage(val message: UserMessage) : DoodleScreenActionResult
}

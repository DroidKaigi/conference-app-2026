package io.github.droidkaigi.confsched.feature.about

import io.github.droidkaigi.confsched.core.common.UserMessage

sealed interface DoodleScreenActionResult {
    data object Saved : DoodleScreenActionResult

    data class ShowMessage(val message: UserMessage) : DoodleScreenActionResult
}

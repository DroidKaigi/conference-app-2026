package io.github.droidkaigi.confsched.feature.search

import io.github.droidkaigi.confsched.core.common.UserMessage

sealed interface SearchScreenActionResult {
    data class ShowMessage(val message: UserMessage) : SearchScreenActionResult
}

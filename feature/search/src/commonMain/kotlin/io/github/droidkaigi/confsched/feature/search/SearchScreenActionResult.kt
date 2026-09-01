package io.github.droidkaigi.confsched.feature.search

import io.github.droidkaigi.confsched.core.common.UserMessage
import io.github.droidkaigi.confsched.core.model.SessionRoom

sealed interface SearchScreenActionResult {
    data class ShowMessage(val message: UserMessage) : SearchScreenActionResult

    data class FavoriteAdded(val room: SessionRoom) : SearchScreenActionResult
}

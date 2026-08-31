package io.github.droidkaigi.confsched.feature.favorites

import io.github.droidkaigi.confsched.core.common.UserMessage

sealed interface FavoritesScreenActionResult {
    data class ShowMessage(val message: UserMessage) : FavoritesScreenActionResult
}

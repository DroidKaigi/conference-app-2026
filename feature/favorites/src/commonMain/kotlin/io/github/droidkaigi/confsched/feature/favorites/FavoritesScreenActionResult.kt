package io.github.droidkaigi.confsched.feature.favorites

import io.github.droidkaigi.confsched.core.common.UserMessage
import io.github.droidkaigi.confsched.core.model.SessionRoom

sealed interface FavoritesScreenActionResult {
    data class ShowMessage(val message: UserMessage) : FavoritesScreenActionResult

    data class OfferFirstFavoriteGuidance(val room: SessionRoom) : FavoritesScreenActionResult
}

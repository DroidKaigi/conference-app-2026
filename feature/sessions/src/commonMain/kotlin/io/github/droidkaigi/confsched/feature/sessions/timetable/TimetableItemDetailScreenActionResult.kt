package io.github.droidkaigi.confsched.feature.sessions.timetable

import io.github.droidkaigi.confsched.core.common.UserMessage
import io.github.droidkaigi.confsched.core.model.SessionRoom

sealed interface TimetableItemDetailScreenActionResult {
    data class ShowMessage(val message: UserMessage) : TimetableItemDetailScreenActionResult

    data object FavoriteAdded : TimetableItemDetailScreenActionResult

    data class OfferFirstFavoriteGuidance(val room: SessionRoom) : TimetableItemDetailScreenActionResult
}

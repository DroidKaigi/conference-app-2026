package io.github.droidkaigi.confsched.feature.sessions.timetable

import io.github.droidkaigi.confsched.core.common.UserMessage

sealed interface TimetableScreenActionResult {
    data class ShowMessage(val message: UserMessage) : TimetableScreenActionResult

    data object FavoriteAdded : TimetableScreenActionResult
}

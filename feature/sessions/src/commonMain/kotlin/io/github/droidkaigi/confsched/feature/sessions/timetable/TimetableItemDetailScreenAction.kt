package io.github.droidkaigi.confsched.feature.sessions.timetable

import io.github.droidkaigi.confsched.core.model.TimetableItemId

sealed interface TimetableItemDetailScreenAction {
    data class ToggleBookmark(val id: TimetableItemId) : TimetableItemDetailScreenAction
}

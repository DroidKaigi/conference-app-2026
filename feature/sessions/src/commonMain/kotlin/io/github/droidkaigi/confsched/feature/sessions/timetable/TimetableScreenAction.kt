package io.github.droidkaigi.confsched.feature.sessions.timetable

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.TimetableItemId

sealed interface TimetableScreenAction {
    data class Bookmark(val id: TimetableItemId) : TimetableScreenAction
    data class SelectDay(val day: DroidKaigi2026Day) : TimetableScreenAction
    data object SwitchToGridView : TimetableScreenAction
}

package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.preview.fake
import kotlinx.collections.immutable.PersistentList

data class TimetableGridSectionUiState(
    val sessions: PersistentList<TimetableItem>,
    val nowMinute: Int?,
) {
    companion object
}

internal fun TimetableGridSectionUiState.Companion.fake(): TimetableGridSectionUiState {
    val timetable = Timetable.fake()
    return TimetableGridSectionUiState(
        sessions = timetable.itemsOn(DroidKaigi2026Day.Day1),
        nowMinute = TimetableGridDayStartMinutes + 40,
    )
}

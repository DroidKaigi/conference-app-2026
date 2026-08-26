package io.github.droidkaigi.confsched.feature.sessions.timetable

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableGridSectionUiState
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableListSectionUiState
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.fake
import kotlin.time.Instant

data class TimetableScreenUiState(
    val day: DroidKaigi2026Day,
    val viewMode: TimetableViewMode,
    val timetableListSection: TimetableListSectionUiState,
    val timetableGridSection: TimetableGridSectionUiState,
) {
    companion object
}

internal fun TimetableScreenUiState.Companion.fake(
    currentTime: Instant = Instant.parse("2026-09-02T12:00:00Z"),
): TimetableScreenUiState = TimetableScreenUiState(
    day = DroidKaigi2026Day.Day1,
    viewMode = TimetableViewMode.List,
    timetableListSection = TimetableListSectionUiState.fake(currentTime),
    timetableGridSection = TimetableGridSectionUiState.fake(),
)

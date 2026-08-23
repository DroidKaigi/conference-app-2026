package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.rememberCurrentTime
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.ConferenceTimeZone
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableGridSectionUiState
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableListSectionUiState
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.toTimeSlots
import kotlinx.datetime.toLocalDateTime
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: TimetablePresenterContext)
fun timetableScreenPresenter(
    screenChannel: ScreenChannel<TimetableScreenAction, TimetableScreenActionResult>,
    timetable: Timetable,
): TimetableScreenUiState {
    val favoriteMutation = rememberMutation(presenterContext.favoriteTimetableItemIdMutationKey)
    var selectedDay by retain { mutableStateOf(DroidKaigi2026Day.Day1) }
    var selectedViewMode by retain { mutableStateOf(TimetableViewMode.List) }
    val currentTime = presenterContext.clock.rememberCurrentTime()

    ActionEffect(screenChannel) { action ->
        when (action) {
            is TimetableScreenAction.Bookmark -> favoriteMutation.mutateAsync(action.id)

            is TimetableScreenAction.SelectDay -> selectedDay = action.day

            TimetableScreenAction.ToggleViewMode ->
                selectedViewMode = when (selectedViewMode) {
                    TimetableViewMode.List -> TimetableViewMode.Grid
                    TimetableViewMode.Grid -> TimetableViewMode.List
                }
        }
    }

    MutationErrorEffect(favoriteMutation) { error ->
        screenChannel.emit(TimetableScreenActionResult.ShowMessage(error.toUserMessage()))
        favoriteMutation.reset()
    }

    return TimetableScreenUiState(
        day = selectedDay,
        viewMode = selectedViewMode,
        timetableListSection = TimetableListSectionUiState(
            timeSlots = timetable.itemsOn(selectedDay).toTimeSlots(currentTime),
            bookmarks = timetable.bookmarks,
        ),
        timetableGridSection = TimetableGridSectionUiState(
            sessions = timetable.itemsOn(selectedDay),
            nowMinute = currentTime.toTimetableGridNowMinuteOn(selectedDay),
        ),
    )
}

private fun kotlin.time.Instant.toTimetableGridNowMinuteOn(day: DroidKaigi2026Day): Int? {
    val localDateTime = toLocalDateTime(ConferenceTimeZone)
    if (localDateTime.date != day.date) return null

    return localDateTime.hour * 60 + localDateTime.minute
}

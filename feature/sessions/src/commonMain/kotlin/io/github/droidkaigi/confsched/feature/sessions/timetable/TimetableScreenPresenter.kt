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
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableListSectionUiState
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.toTimeSlots
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: TimetablePresenterContext)
fun timetableScreenPresenter(
    screenChannel: ScreenChannel<TimetableScreenAction, TimetableScreenActionResult>,
    timetable: Timetable,
): TimetableScreenUiState {
    val favoriteMutation = rememberMutation(presenterContext.favoriteTimetableItemIdMutationKey)
    var selectedDay by retain { mutableStateOf(DroidKaigi2026Day.Day1) }
    val currentTime = presenterContext.clock.rememberCurrentTime()

    ActionEffect(screenChannel) { action ->
        when (action) {
            is TimetableScreenAction.Bookmark -> favoriteMutation.mutateAsync(action.id)

            is TimetableScreenAction.SelectDay -> selectedDay = action.day

            is TimetableScreenAction.SwitchToGridView ->
                presenterContext.logger.debug { "TODO: render the grid view" }
        }
    }

    MutationErrorEffect(favoriteMutation) { error ->
        screenChannel.emit(TimetableScreenActionResult.ShowMessage(error.toUserMessage()))
        favoriteMutation.reset()
    }

    return TimetableScreenUiState(
        day = selectedDay,
        timetableListSection = TimetableListSectionUiState(
            timeSlots = timetable.itemsOn(selectedDay).toTimeSlots(currentTime),
            bookmarks = timetable.bookmarks,
        ),
    )
}

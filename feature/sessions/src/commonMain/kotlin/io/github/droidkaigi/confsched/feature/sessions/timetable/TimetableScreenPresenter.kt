package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: TimetablePresenterContext)
fun timetableScreenPresenter(
    screenChannel: ScreenChannel<TimetableScreenAction, TimetableScreenActionResult>,
    timetable: Timetable,
): TimetableScreenUiState {
    val favoriteMutation = rememberMutation(presenterContext.favoriteTimetableItemIdMutationKey)
    var selectedDay by retain { mutableStateOf(DroidKaigi2026Day.Day1) }
    var isRawResponseExpanded by retain { mutableStateOf(false) }

    ActionEffect(screenChannel) { action ->
        when (action) {
            is TimetableScreenAction.Bookmark -> favoriteMutation.mutateAsync(action.id)
            is TimetableScreenAction.SelectDay -> selectedDay = action.day
            is TimetableScreenAction.ToggleRawResponse -> isRawResponseExpanded = !isRawResponseExpanded
        }
    }

    MutationErrorEffect(favoriteMutation) { error ->
        screenChannel.emit(TimetableScreenActionResult.ShowMessage(error.toUserMessage()))
        favoriteMutation.reset()
    }

    return TimetableScreenUiState(
        day = selectedDay,
        sessions = timetable.itemsOn(selectedDay),
        bookmarks = timetable.bookmarks,
        rawResponse = timetable.rawResponse,
        isRawResponseExpanded = isRawResponseExpanded,
    )
}

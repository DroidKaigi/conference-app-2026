package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import io.github.droidkaigi.confsched.core.model.startInstant
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableCountdownBannerUiState
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableGridSectionUiState
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableListSectionUiState
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.toTimeSlots
import kotlinx.collections.immutable.toPersistentList
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

    LaunchedEffect(presenterContext.dayRequestStore) {
        presenterContext.dayRequestStore.requests.collect { day -> selectedDay = day }
    }

    ActionEffect(screenChannel) { action ->
        when (action) {
            is TimetableScreenAction.Bookmark -> favoriteMutation.mutateAsync(action.id)

            is TimetableScreenAction.SelectDay -> selectedDay = action.day

            TimetableScreenAction.SwitchToGridView ->
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

    val timeSlots = remember(timetable, selectedDay, currentTime) {
        timetable.itemsOn(selectedDay).toTimeSlots(currentTime)
    }

    val countdownBannerUiState = remember(timetable, selectedDay, currentTime) {
        val currentDay = DroidKaigi2026Day.ofOrNull(currentTime)
        if (currentDay == null || selectedDay != currentDay) {
            return@remember null
        }

        val favoritedItemsOnDay = timetable.itemsOn(selectedDay)
            .filter { it.id in timetable.bookmarks }

        val nextFavoritedItems = favoritedItemsOnDay
            .filter { it.startInstant > currentTime }

        if (nextFavoritedItems.isNotEmpty()) {
            val firstStartInstant = nextFavoritedItems.minOf { it.startInstant }
            val sessionsAtFirstStartTime = nextFavoritedItems.filter { it.startInstant == firstStartInstant }

            val diff = firstStartInstant - currentTime
            TimetableCountdownBannerUiState(
                nextSessions = sessionsAtFirstStartTime.toPersistentList(),
                remainingDuration = diff,
            )
        } else {
            null
        }
    }

    return TimetableScreenUiState(
        day = selectedDay,
        viewMode = selectedViewMode,
        timetableListSection = TimetableListSectionUiState(
            timeSlots = timeSlots,
            bookmarks = timetable.bookmarks,
            countdownBannerUiState = countdownBannerUiState,
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

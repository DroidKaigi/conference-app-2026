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
import io.github.droidkaigi.confsched.core.common.MutationSuccessEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.rememberCurrentTime
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.ConferenceTimeZone
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.startInstant
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableCountdownBannerUiState
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableGridSectionUiState
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.TimetableListSectionUiState
import io.github.droidkaigi.confsched.feature.sessions.timetable.component.toTimeSlots
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.datetime.toLocalDateTime
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: TimetablePresenterContext)
fun timetableScreenPresenter(
    screenChannel: ScreenChannel<TimetableScreenAction, TimetableScreenActionResult>,
    timetable: Timetable,
): TimetableScreenUiState {
    val favoriteMutation = rememberMutation(presenterContext.favoriteTimetableItemIdMutationKey)
    // The mutation reports only the direction of the toggle, so the session it applied to is kept here.
    var toggledFavoriteId by retain { mutableStateOf<TimetableItemId?>(null) }
    var selectedDay by retain { mutableStateOf(DroidKaigi2026Day.Day1) }
    var selectedViewMode by retain { mutableStateOf(TimetableViewMode.List) }
    val currentTime = presenterContext.clock.rememberCurrentTime()

    LaunchedEffect(presenterContext.dayRequestStore) {
        presenterContext.dayRequestStore.requests.collect { day -> selectedDay = day }
    }

    ActionEffect(screenChannel) { action ->
        when (action) {
            is TimetableScreenAction.Bookmark -> {
                toggledFavoriteId = action.id
                favoriteMutation.mutateAsync(action.id)
            }

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

    MutationSuccessEffect(favoriteMutation) { added ->
        val addedId = toggledFavoriteId.takeIf { added }
        if (addedId != null) {
            screenChannel.emit(TimetableScreenActionResult.FavoriteAdded(timetable.roomOf(addedId)))
        }
        favoriteMutation.reset()
    }

    val timetableListSections = remember(timetable, currentTime) {
        val currentDay = DroidKaigi2026Day.ofOrNull(currentTime)
        DroidKaigi2026Day.entries.associateWith { day ->
            TimetableListSectionUiState(
                timeSlots = timetable.itemsOn(day).toTimeSlots(currentTime),
                bookmarks = timetable.bookmarks,
                countdownBannerUiState = if (day == currentDay) {
                    timetable.countdownBannerUiState(day, currentTime)
                } else {
                    null
                },
            )
        }.toPersistentMap()
    }

    return TimetableScreenUiState(
        day = selectedDay,
        viewMode = selectedViewMode,
        timetableListSections = timetableListSections,
        timetableGridSection = TimetableGridSectionUiState(
            sessions = timetable.itemsOn(selectedDay),
            nowMinute = currentTime.toTimetableGridNowMinuteOn(selectedDay),
        ),
    )
}

private fun Timetable.countdownBannerUiState(
    day: DroidKaigi2026Day,
    currentTime: kotlin.time.Instant,
): TimetableCountdownBannerUiState? {
    val nextFavoritedItems = itemsOn(day)
        .asSequence()
        .filter { it.id in bookmarks }
        .filter { it.startInstant > currentTime }
        .toList()
    if (nextFavoritedItems.isEmpty()) return null

    val firstStartInstant = nextFavoritedItems.minOf { it.startInstant }
    return TimetableCountdownBannerUiState(
        nextSessions = nextFavoritedItems
            .filter { it.startInstant == firstStartInstant }
            .toPersistentList(),
        remainingDuration = firstStartInstant - currentTime,
    )
}

private fun kotlin.time.Instant.toTimetableGridNowMinuteOn(day: DroidKaigi2026Day): Int? {
    val localDateTime = toLocalDateTime(ConferenceTimeZone)
    if (localDateTime.date != day.date) return null

    return localDateTime.hour * 60 + localDateTime.minute
}

package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.fake
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentList
import kotlin.time.Duration.Companion.minutes

data class TimetableListSectionUiState(
    val timeSlots: PersistentList<TimeSlot>,
    val bookmarks: PersistentSet<TimetableItemId>,
    val countdownBannerUiState: TimetableCountdownBannerUiState? = null,
) {
    data class TimeSlot(
        val startsAt: String,
        val endsAt: String,
        val items: PersistentList<TimetableItem>,
    )

    companion object
}

internal fun PersistentList<TimetableItem>.toTimeSlots(): PersistentList<TimetableListSectionUiState.TimeSlot> =
    groupBy { it.startsAt to it.endsAt }
        .map { (time, items) ->
            TimetableListSectionUiState.TimeSlot(
                startsAt = time.first,
                endsAt = time.second,
                items = items.toPersistentList(),
            )
        }
        .toPersistentList()

internal fun TimetableListSectionUiState.Companion.fake(): TimetableListSectionUiState {
    val timetable = Timetable.fake()
    return TimetableListSectionUiState(
        timeSlots = timetable.itemsOn(DroidKaigi2026Day.Day1).toTimeSlots(),
        bookmarks = timetable.bookmarks,
        countdownBannerUiState = TimetableCountdownBannerUiState(
            nextSessions = timetable.itemsOn(DroidKaigi2026Day.Day1).take(1).toPersistentList(),
            remainingDuration = 25.minutes,
        ),
    )
}

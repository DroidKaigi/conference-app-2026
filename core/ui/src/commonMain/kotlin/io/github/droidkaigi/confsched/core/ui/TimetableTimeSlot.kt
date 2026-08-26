package io.github.droidkaigi.confsched.core.ui

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.TimetableItem
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlin.time.Instant

data class TimetableTimeSlot(
    val day: DroidKaigi2026Day,
    val startsAt: String,
    val endsAt: String,
    val timeRangeState: TimetableLineState,
    val items: PersistentList<TimetableItem>,
)

fun List<TimetableItem>.toTimetableTimeSlots(
    currentTime: Instant,
): PersistentList<TimetableTimeSlot> =
    groupBy { item -> Triple(item.day, item.startsAt, item.endsAt) }
        .map { entry ->
            val representativeItem = entry.value.first()
            TimetableTimeSlot(
                day = entry.key.first,
                startsAt = entry.key.second,
                endsAt = entry.key.third,
                timeRangeState = representativeItem.lineState(currentTime),
                items = entry.value.sortedBy { item -> item.room }.toPersistentList(),
            )
        }
        .sortedWith(compareBy({ slot -> slot.day }, { slot -> slot.startsAt }, { slot -> slot.endsAt }))
        .toPersistentList()

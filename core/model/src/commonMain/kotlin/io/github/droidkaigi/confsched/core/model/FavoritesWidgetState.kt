package io.github.droidkaigi.confsched.core.model

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

/** What the home-screen favorites widget shows, in precedence order. */
sealed interface FavoritesWidgetState {
    /** Before the event: the number of days until Day 1, shown regardless of favorites. */
    data class Countdown(val daysUntilStart: Int) : FavoritesWidgetState

    /** The event day before Day 1, which holds no timetable sessions. */
    data object EventDay : FavoritesWidgetState

    /** A conference day with no favorite on it; [otherDayFavorites] counts Day 2's favorites on Day 1 and is 0 on Day 2. */
    data class Empty(
        val day: DroidKaigi2026Day,
        val otherDayFavorites: Int,
    ) : FavoritesWidgetState

    /** The slots of [day] that have not ended yet, in start order. */
    data class Schedule(
        val day: DroidKaigi2026Day,
        val slots: List<FavoritesWidgetSlot>,
    ) : FavoritesWidgetState

    /** Every favorite of [day] has ended while the day's programme runs on; [otherDayFavorites] as in [Empty]. */
    data class TodayDone(
        val day: DroidKaigi2026Day,
        val otherDayFavorites: Int,
    ) : FavoritesWidgetState

    /** Day 1's programme is over and Day 2 is still ahead. */
    data class DayWrapUp(val tomorrowFavorites: Int) : FavoritesWidgetState

    /** After the conference end, or once Day 2's programme is over. */
    data object PostConference : FavoritesWidgetState
}

/** Favorited sessions sharing one start–end pair on one day. */
data class FavoritesWidgetSlot(
    val startsAt: String,
    val endsAt: String,
    val isLive: Boolean,
    val sessions: List<TimetableItem>,
)

/** One row of the medium widget's schedule list. */
sealed interface FavoritesWidgetRow {
    /** [showsTime] is false for the second and later sessions of a shared slot. */
    data class Session(
        val session: TimetableItem,
        val showsTime: Boolean,
        val isLive: Boolean,
    ) : FavoritesWidgetRow

    /** Replaces the last row when more sessions remain than the list holds. */
    data class More(val count: Int) : FavoritesWidgetRow
}

// ConferenceTimeZone is a fixed offset, so adding wall-clock hours is exact.
private val EventDayStart = DroidKaigi2026Day.Day1.at(0, 0) - 24.hours

private val ConferenceStart = DroidKaigi2026Day.Day1.at(0, 0)

private val Day2Start = DroidKaigi2026Day.Day2.at(0, 0)

private val ConferenceEnd = Day2Start + 24.hours

fun computeFavoritesWidgetState(
    now: Instant,
    timetable: Timetable,
    favoriteIds: Set<TimetableItemId>,
): FavoritesWidgetState {
    if (now < EventDayStart) {
        val today = now.toLocalDateTime(ConferenceTimeZone).date
        return FavoritesWidgetState.Countdown(today.daysUntil(DroidKaigi2026Day.Day1.date))
    }
    if (now < ConferenceStart) return FavoritesWidgetState.EventDay
    if (now >= ConferenceEnd) return FavoritesWidgetState.PostConference

    val day = conferenceDayAt(now)
    val itemsOnDay = timetable.items.filter { it.day == day }
    val otherDayFavorites = if (day == DroidKaigi2026Day.Day1) {
        timetable.items.count { it.day == DroidKaigi2026Day.Day2 && it.id in favoriteIds }
    } else {
        0
    }
    // A day with no timetable item leaves its programme's end unknown, so it is not over.
    val programmeOver = itemsOnDay.isNotEmpty() && itemsOnDay.all { it.endInstant <= now }
    if (programmeOver) {
        return when (day) {
            DroidKaigi2026Day.Day1 -> FavoritesWidgetState.DayWrapUp(tomorrowFavorites = otherDayFavorites)
            DroidKaigi2026Day.Day2 -> FavoritesWidgetState.PostConference
        }
    }

    val favorites = itemsOnDay.filter { it.id in favoriteIds }
    val remaining = favorites.filter { it.endInstant > now }
    if (remaining.isEmpty()) {
        return if (favorites.isEmpty()) {
            FavoritesWidgetState.Empty(day = day, otherDayFavorites = otherDayFavorites)
        } else {
            FavoritesWidgetState.TodayDone(day = day, otherDayFavorites = otherDayFavorites)
        }
    }
    val slots = remaining
        .groupBy { it.startsAt to it.endsAt }
        .map { (_, sessions) ->
            FavoritesWidgetSlot(
                startsAt = sessions.first().startsAt,
                endsAt = sessions.first().endsAt,
                isLive = sessions.first().startInstant <= now,
                sessions = sessions,
            )
        }
        .sortedBy { it.sessions.first().startInstant }
    return FavoritesWidgetState.Schedule(day = day, slots = slots)
}

/**
 * The earliest instant after [now] at which the widget state computed from the same inputs can
 * change on its own, or null when only new inputs can change it.
 */
fun nextFavoritesWidgetBoundary(
    now: Instant,
    timetable: Timetable,
    favoriteIds: Set<TimetableItemId>,
): Instant? {
    if (now >= ConferenceEnd) return null
    if (now < EventDayStart) {
        val today = now.toLocalDateTime(ConferenceTimeZone).date
        return today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(ConferenceTimeZone)
    }
    if (now < ConferenceStart) return ConferenceStart

    val day = conferenceDayAt(now)
    val itemsOnDay = timetable.items.filter { it.day == day }
    val favoriteBoundaries = itemsOnDay
        .filter { it.id in favoriteIds }
        .flatMap { listOf(it.startInstant, it.endInstant) }
    val lastSessionEnd = itemsOnDay.maxOfOrNull { it.endInstant }
    val candidates = favoriteBoundaries +
        listOfNotNull(lastSessionEnd) +
        (day.at(0, 0) + 24.hours) +
        ConferenceEnd
    return candidates.filter { it > now }.min()
}

private fun conferenceDayAt(now: Instant): DroidKaigi2026Day =
    if (now < Day2Start) DroidKaigi2026Day.Day1 else DroidKaigi2026Day.Day2

/**
 * Flattens slots into at most [maxRows] rows; when sessions overflow, the last row becomes the
 * remaining count.
 */
fun List<FavoritesWidgetSlot>.toFavoritesWidgetRows(maxRows: Int): List<FavoritesWidgetRow> {
    require(maxRows > 0) { "maxRows must be positive but was $maxRows" }
    val sessionRows = flatMap { slot ->
        slot.sessions.mapIndexed { index, session ->
            FavoritesWidgetRow.Session(
                session = session,
                showsTime = index == 0,
                isLive = slot.isLive,
            )
        }
    }
    if (sessionRows.size <= maxRows) return sessionRows
    val kept = sessionRows.take(maxRows - 1)
    return kept + FavoritesWidgetRow.More(sessionRows.size - kept.size)
}

val TimetableItem.startInstant: Instant get() = instantOf(startsAt)

val TimetableItem.endInstant: Instant get() = instantOf(endsAt)

private fun TimetableItem.instantOf(time: String): Instant {
    val (hour, minute) = time.split(":").map(String::toInt)
    return day.at(hour, minute)
}

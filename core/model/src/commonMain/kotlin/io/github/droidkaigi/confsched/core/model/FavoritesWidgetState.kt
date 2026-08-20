package io.github.droidkaigi.confsched.core.model

import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

/** What the home-screen favorites widget shows, in precedence order. */
sealed interface FavoritesWidgetState {
    /** Before the conference: the number of days until Day 1, shown regardless of favorites. */
    data class Countdown(val daysUntilStart: Int) : FavoritesWidgetState

    /** Conference days without favorites: the prompt to add some. */
    data object Empty : FavoritesWidgetState

    /** Conference days with favorites: the slots that have not ended yet, in start order. */
    data class Schedule(val slots: List<FavoritesWidgetSlot>) : FavoritesWidgetState

    /** After the conference, or after every favorited session has ended. */
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

fun computeFavoritesWidgetState(
    now: Instant,
    timetable: Timetable,
    favoriteIds: Set<TimetableItemId>,
): FavoritesWidgetState {
    val conferenceStart = DroidKaigi2026Day.Day1.at(0, 0)
    // ConferenceTimeZone is a fixed offset, so adding wall-clock hours is exact.
    val conferenceEnd = DroidKaigi2026Day.Day2.at(0, 0) + 24.hours
    if (now < conferenceStart) {
        val today = now.toLocalDateTime(ConferenceTimeZone).date
        return FavoritesWidgetState.Countdown(today.daysUntil(DroidKaigi2026Day.Day1.date))
    }
    if (now >= conferenceEnd) return FavoritesWidgetState.PostConference
    val favorites = timetable.items.filter { it.id in favoriteIds }
    if (favorites.isEmpty()) return FavoritesWidgetState.Empty
    val remaining = favorites.filter { it.endInstant > now }
    if (remaining.isEmpty()) return FavoritesWidgetState.PostConference
    val slots = remaining
        .groupBy { Triple(it.day, it.startsAt, it.endsAt) }
        .map { (_, sessions) ->
            FavoritesWidgetSlot(
                startsAt = sessions.first().startsAt,
                endsAt = sessions.first().endsAt,
                isLive = sessions.first().startInstant <= now,
                sessions = sessions,
            )
        }
        .sortedBy { it.sessions.first().startInstant }
    return FavoritesWidgetState.Schedule(slots)
}

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

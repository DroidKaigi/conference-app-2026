package io.github.droidkaigi.confsched.core.model

import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/** A favorited session and the moment its "starts soon" notification is due. */
data class SessionReminder(
    val itemId: TimetableItemId,
    val title: MultiLangText,
    val room: SessionRoom,
    val startsAt: Instant,
    val startsAtText: String,
    val notifyAt: Instant,
)

/** How long before a session starts its reminder fires. */
val SessionReminderLeadTime = 15.minutes

/** The reminders for [favoriteIds] whose session has not started yet, earliest first. */
fun computeSessionReminders(
    now: Instant,
    timetable: Timetable,
    favoriteIds: Set<TimetableItemId>,
): List<SessionReminder> = timetable.items
    .filter { it.id in favoriteIds && !it.isCancelled }
    .map { item ->
        SessionReminder(
            itemId = item.id,
            title = item.title,
            room = item.room,
            startsAt = item.startsAtInstant,
            startsAtText = item.startsAt,
            notifyAt = item.startsAtInstant - SessionReminderLeadTime,
        )
    }
    .filter { it.startsAt > now }
    .sortedBy(SessionReminder::notifyAt)

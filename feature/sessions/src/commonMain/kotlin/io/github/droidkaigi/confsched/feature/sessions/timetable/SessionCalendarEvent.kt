package io.github.droidkaigi.confsched.feature.sessions.timetable

import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.sessionUrl
import io.github.droidkaigi.confsched.core.ui.CalendarEvent

internal fun TimetableItem.toCalendarEvent(): CalendarEvent = CalendarEvent(
    title = title.en,
    startsAt = startsAtInstant,
    endsAt = endsAtInstant,
    location = room.name,
    url = sessionUrl(id),
)

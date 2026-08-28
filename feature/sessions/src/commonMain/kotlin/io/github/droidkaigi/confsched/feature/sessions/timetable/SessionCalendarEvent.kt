package io.github.droidkaigi.confsched.feature.sessions.timetable

import io.github.droidkaigi.confsched.core.model.DisplayLanguage
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.sessionUrl
import io.github.droidkaigi.confsched.core.ui.CalendarEvent

internal fun TimetableItem.toCalendarEvent(language: DisplayLanguage): CalendarEvent = CalendarEvent(
    title = title.of(language),
    startsAt = startsAtInstant,
    endsAt = endsAtInstant,
    location = room.name,
    url = sessionUrl(id),
)

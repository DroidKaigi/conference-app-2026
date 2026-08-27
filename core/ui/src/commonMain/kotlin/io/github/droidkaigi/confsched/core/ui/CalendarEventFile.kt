package io.github.droidkaigi.confsched.core.ui

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

internal const val CALENDAR_EVENT_FILE_NAME = "session.ics"

internal fun CalendarEvent.toICalendar(): String = listOf(
    "BEGIN:VCALENDAR",
    "VERSION:2.0",
    "PRODID:-//DroidKaigi//DroidKaigi 2026//EN",
    "BEGIN:VEVENT",
    "UID:${url.escapeICalendarText()}",
    "DTSTAMP:${startsAt.toICalendarUtc()}",
    "DTSTART:${startsAt.toICalendarUtc()}",
    "DTEND:${endsAt.toICalendarUtc()}",
    "SUMMARY:${title.escapeICalendarText()}",
    "LOCATION:${location.escapeICalendarText()}",
    "DESCRIPTION:${url.escapeICalendarText()}",
    "URL:${url.escapeICalendarText()}",
    "END:VEVENT",
    "END:VCALENDAR",
).joinToString(separator = "\r\n", postfix = "\r\n")

private fun Instant.toICalendarUtc(): String {
    val time = toLocalDateTime(TimeZone.UTC)
    return buildString {
        append(time.year.toString().padStart(4, '0'))
        append(time.month.number.toString().padStart(2, '0'))
        append(time.day.toString().padStart(2, '0'))
        append('T')
        append(time.hour.toString().padStart(2, '0'))
        append(time.minute.toString().padStart(2, '0'))
        append(time.second.toString().padStart(2, '0'))
        append('Z')
    }
}

private fun String.escapeICalendarText(): String = this
    .replace("\\", "\\\\")
    .replace(";", "\\;")
    .replace(",", "\\,")
    .replace("\n", "\\n")

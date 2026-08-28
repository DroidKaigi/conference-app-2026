package io.github.droidkaigi.confsched.core.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class CalendarEventFileTest {
    @Test
    fun writesTimestampsInUtcWithCrlfLineEndings() {
        val event = CalendarEvent(
            title = "Session 1",
            startsAt = Instant.parse("2026-09-10T10:00:00+09:00"),
            endsAt = Instant.parse("2026-09-10T10:40:00+09:00"),
            location = "Room A",
            url = "https://example.com/sessions/1",
        )

        val expected = listOf(
            "BEGIN:VCALENDAR",
            "VERSION:2.0",
            "PRODID:-//DroidKaigi//DroidKaigi 2026//EN",
            "BEGIN:VEVENT",
            "UID:https://example.com/sessions/1",
            "DTSTAMP:20260910T010000Z",
            "DTSTART:20260910T010000Z",
            "DTEND:20260910T014000Z",
            "SUMMARY:Session 1",
            "LOCATION:Room A",
            "DESCRIPTION:https://example.com/sessions/1",
            "URL:https://example.com/sessions/1",
            "END:VEVENT",
            "END:VCALENDAR",
        ).joinToString(separator = "\r\n", postfix = "\r\n")
        assertEquals(expected, event.toICalendar())
    }

    @Test
    fun escapesTextValues() {
        val event = CalendarEvent(
            title = "A; B, C\\D\r\nE\nF",
            startsAt = Instant.fromEpochSeconds(0),
            endsAt = Instant.fromEpochSeconds(0),
            location = "",
            url = "",
        )

        val summary = event.toICalendar().lineSequence().first { it.startsWith("SUMMARY:") }
        assertEquals("""SUMMARY:A\; B\, C\\D\nE\nF""", summary)
    }
}

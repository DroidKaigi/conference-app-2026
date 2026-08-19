package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import kotlin.test.Test
import kotlin.test.assertEquals

class TimetableGridTimeTest {

    @Test
    fun session_position_uses_start_and_end_minutes_at_default_scale() {
        assertEquals(34f, timetableGridSessionOffsetY("10:00").value)
        assertEquals(78f, timetableGridSessionOffsetY("10:20").value)
        assertEquals(40f, timetableGridSessionHeight(startsAt = "10:00", endsAt = "10:20").value)
    }

    @Test
    fun session_position_uses_current_scale() {
        assertEquals(
            274f,
            timetableGridSessionOffsetY(
                startsAt = "11:00",
                hourHeight = TimetableGridMaxHourHeight,
            ).value,
        )
        assertEquals(
            236f,
            timetableGridSessionHeight(
                startsAt = "10:00",
                endsAt = "11:00",
                hourHeight = TimetableGridMaxHourHeight,
            ).value,
        )
    }
}

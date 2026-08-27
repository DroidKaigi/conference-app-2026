package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class TimetableGridTimeTest {

    @Test
    fun session_position_uses_start_and_end_minutes_at_default_scale() {
        assertEquals(38f, timetableGridSessionOffsetY("10:00").value)
        assertEquals(86f, timetableGridSessionOffsetY("10:20").value)
        assertEquals(44f, timetableGridSessionHeight(startsAt = "10:00", endsAt = "10:20").value)
    }

    @Test
    fun session_position_uses_current_scale() {
        assertEquals(
            258f,
            timetableGridSessionOffsetY(
                startsAt = "11:00",
                hourHeight = TimetableGridMaxHourHeight,
            ).value,
        )
        assertEquals(
            216f,
            timetableGridSessionHeight(
                startsAt = "10:00",
                endsAt = "11:00",
                hourHeight = TimetableGridMaxHourHeight,
            ).value,
        )
    }

    @Test
    fun column_keeps_design_width_when_the_viewport_is_narrower_than_the_grid() {
        assertEquals(TimetableGridRoomColumnWidth, timetableGridColumnWidth(availableWidth = 400.dp, roomCount = 5))
    }

    @Test
    fun column_stretches_evenly_when_the_viewport_is_wider_than_the_grid() {
        assertEquals(200.dp, timetableGridColumnWidth(availableWidth = 1032.dp, roomCount = 5))
        assertEquals(1032.dp, timetableGridContentWidth(roomCount = 5, columnWidth = 200.dp))
    }

    @Test
    fun content_width_sums_columns_and_the_gaps_between_them() {
        assertEquals(812.dp, timetableGridContentWidth(roomCount = 5, columnWidth = TimetableGridRoomColumnWidth))
    }
}

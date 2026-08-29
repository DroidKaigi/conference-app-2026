package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SessionReminderTest {
    private fun item(
        id: String,
        day: DroidKaigi2026Day = DroidKaigi2026Day.Day1,
        startsAt: String,
        endsAt: String,
        isCancelled: Boolean = false,
    ): TimetableItem {
        val startHour = startsAt.substringBefore(':').toInt()
        val startMinute = startsAt.substringAfter(':').toInt()
        val endHour = endsAt.substringBefore(':').toInt()
        val endMinute = endsAt.substringAfter(':').toInt()

        return TimetableItem(
            id = TimetableItemId(id),
            title = MultiLangText(ja = "セッション $id", en = "Session $id"),
            room = SessionRoom.OTTER,
            speakers = persistentListOf(),
            language = Language.JAPANESE,
            day = day,
            startsAt = startsAt,
            endsAt = endsAt,
            sessionType = SessionType.NORMAL,
            startsAtInstant = day.at(hour = startHour, minute = startMinute),
            endsAtInstant = day.at(hour = endHour, minute = endMinute),
            description = MultiLangText(ja = "", en = ""),
            targetAudience = MultiLangText(ja = "", en = ""),
            category = null,
            asset = TimetableItemAsset.Empty,
            hasInterpretation = false,
            isCancelled = isCancelled,
            message = null,
        )
    }

    private fun timetable(vararg items: TimetableItem) = Timetable(items = items.toList().toPersistentList())

    private fun ids(vararg values: String) = values.map(::TimetableItemId).toSet()

    @Test
    fun a_reminder_falls_the_lead_time_before_the_start() {
        val reminder = computeSessionReminders(
            now = DroidKaigi2026Day.Day1.at(9, 0),
            timetable = timetable(item("a", startsAt = "10:00", endsAt = "10:40")),
            favoriteIds = ids("a"),
        ).single()
        assertEquals(DroidKaigi2026Day.Day1.at(10, 0), reminder.startsAt)
        assertEquals("10:00", reminder.startsAtText)
        assertEquals(DroidKaigi2026Day.Day1.at(9, 45), reminder.notifyAt)
    }

    @Test
    fun a_session_that_already_started_is_dropped() {
        val reminders = computeSessionReminders(
            now = DroidKaigi2026Day.Day1.at(10, 30),
            timetable = timetable(
                item("started", startsAt = "10:00", endsAt = "10:40"),
                item("later", startsAt = "11:00", endsAt = "11:40"),
            ),
            favoriteIds = ids("started", "later"),
        )
        assertEquals(listOf("later"), reminders.map { it.itemId.value })
    }

    @Test
    fun a_favorite_added_within_the_lead_time_still_gets_a_reminder() {
        val reminder = computeSessionReminders(
            now = DroidKaigi2026Day.Day1.at(9, 50),
            timetable = timetable(item("soon", startsAt = "10:00", endsAt = "10:40")),
            favoriteIds = ids("soon"),
        ).single()
        assertEquals(DroidKaigi2026Day.Day1.at(9, 45), reminder.notifyAt)
    }

    @Test
    fun reminders_run_earliest_first_across_days() {
        val reminders = computeSessionReminders(
            now = DroidKaigi2026Day.Day1.at(9, 0),
            timetable = timetable(
                item("d2", day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40"),
                item("late", startsAt = "15:00", endsAt = "15:40"),
                item("early", startsAt = "10:00", endsAt = "10:40"),
            ),
            favoriteIds = ids("d2", "late", "early"),
        )
        assertEquals(listOf("early", "late", "d2"), reminders.map { it.itemId.value })
    }

    @Test
    fun sessions_outside_the_favorites_are_ignored() {
        val reminders = computeSessionReminders(
            now = DroidKaigi2026Day.Day1.at(9, 0),
            timetable = timetable(
                item("favorited", startsAt = "10:00", endsAt = "10:40"),
                item("unfavorited", startsAt = "11:00", endsAt = "11:40"),
            ),
            favoriteIds = ids("favorited"),
        )
        assertEquals(listOf("favorited"), reminders.map { it.itemId.value })
    }

    @Test
    fun a_cancelled_session_is_not_reminded() {
        val reminders = computeSessionReminders(
            now = DroidKaigi2026Day.Day1.at(9, 0),
            timetable = timetable(item("cancelled", startsAt = "10:00", endsAt = "10:40", isCancelled = true)),
            favoriteIds = ids("cancelled"),
        )
        assertTrue(reminders.isEmpty())
    }

    @Test
    fun no_favorites_leave_nothing_to_schedule() {
        val reminders = computeSessionReminders(
            now = DroidKaigi2026Day.Day1.at(9, 0),
            timetable = timetable(item("a", startsAt = "10:00", endsAt = "10:40")),
            favoriteIds = emptySet(),
        )
        assertTrue(reminders.isEmpty())
    }
}

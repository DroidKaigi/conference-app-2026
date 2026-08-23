package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.days

class FavoritesWidgetStateTest {
    private fun item(
        id: String,
        day: DroidKaigi2026Day = DroidKaigi2026Day.Day1,
        startsAt: String,
        endsAt: String,
        room: Room = Room.OTTER,
    ): TimetableItem {
        val startHour = startsAt.substringBefore(':').toInt()
        val startMinute = startsAt.substringAfter(':').toInt()
        val endHour = endsAt.substringBefore(':').toInt()
        val endMinute = endsAt.substringAfter(':').toInt()

        return TimetableItem(
            id = TimetableItemId(id),
            title = MultiLangText(ja = "セッション $id", en = "Session $id"),
            room = room,
            speakers = persistentListOf(
                TimetableSpeaker(
                    id = TimetableSpeakerId("speaker-$id"),
                    name = "Speaker A",
                    tagLine = "",
                    iconUrl = null,
                ),
            ),
            language = Language.JAPANESE,
            day = day,
            startsAt = startsAt,
            endsAt = endsAt,
            startsAtInstant = day.at(hour = startHour, minute = startMinute),
            endsAtInstant = day.at(hour = endHour, minute = endMinute),
            description = MultiLangText(ja = "", en = ""),
            targetAudience = MultiLangText(ja = "", en = ""),
            category = null,
            asset = TimetableItemAsset.Empty,
            hasInterpretation = false,
            isCancelled = false,
        )
    }

    private fun timetable(vararg items: TimetableItem) = Timetable(items = items.toList().toPersistentList())

    private fun ids(vararg values: String) = values.map(::TimetableItemId).toSet()

    @Test
    fun before_the_conference_counts_days_regardless_of_favorites() {
        val now = DroidKaigi2026Day.Day1.at(9, 0) - 14.days
        val state = computeFavoritesWidgetState(now, timetable(item("a", startsAt = "10:00", endsAt = "10:40")), ids("a"))
        assertEquals(FavoritesWidgetState.Countdown(daysUntilStart = 14), state)
    }

    @Test
    fun the_night_before_day1_counts_one_day() {
        val now = DroidKaigi2026Day.Day1.at(0, 0) - 1.days
        val state = computeFavoritesWidgetState(now, timetable(), emptySet())
        assertEquals(FavoritesWidgetState.Countdown(daysUntilStart = 1), state)
    }

    @Test
    fun conference_days_without_favorites_prompt_to_add_them() {
        val now = DroidKaigi2026Day.Day1.at(9, 0)
        val state = computeFavoritesWidgetState(now, timetable(item("a", startsAt = "10:00", endsAt = "10:40")), emptySet())
        assertEquals(FavoritesWidgetState.Empty, state)
    }

    @Test
    fun after_the_conference_thanks_the_visitor() {
        val now = DroidKaigi2026Day.Day2.at(9, 0) + 1.days
        val state = computeFavoritesWidgetState(now, timetable(item("a", startsAt = "10:00", endsAt = "10:40")), ids("a"))
        assertEquals(FavoritesWidgetState.PostConference, state)
    }

    @Test
    fun favorites_that_all_ended_read_as_post_conference() {
        val now = DroidKaigi2026Day.Day2.at(20, 0)
        val state = computeFavoritesWidgetState(now, timetable(item("a", day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40")), ids("a"))
        assertEquals(FavoritesWidgetState.PostConference, state)
    }

    @Test
    fun upcoming_favorites_form_slots_in_start_order_across_days() {
        val now = DroidKaigi2026Day.Day1.at(9, 0)
        val state = computeFavoritesWidgetState(
            now,
            timetable(
                item("d2", day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40"),
                item("late", startsAt = "15:00", endsAt = "15:40"),
                item("early", startsAt = "10:00", endsAt = "10:40"),
                item("unfavorited", startsAt = "11:00", endsAt = "11:40"),
            ),
            ids("d2", "late", "early"),
        )
        val schedule = assertIs<FavoritesWidgetState.Schedule>(state)
        assertEquals(
            listOf("early", "late", "d2"),
            schedule.slots.map { slot -> slot.sessions.single().id.value },
        )
        assertEquals(listOf(false, false, false), schedule.slots.map(FavoritesWidgetSlot::isLive))
    }

    @Test
    fun a_running_favorite_marks_its_slot_live() {
        val now = DroidKaigi2026Day.Day1.at(10, 10)
        val state = computeFavoritesWidgetState(
            now,
            timetable(
                item("live", startsAt = "10:00", endsAt = "10:40"),
                item("next", startsAt = "11:00", endsAt = "11:40"),
            ),
            ids("live", "next"),
        )
        val schedule = assertIs<FavoritesWidgetState.Schedule>(state)
        assertEquals(listOf(true, false), schedule.slots.map(FavoritesWidgetSlot::isLive))
    }

    @Test
    fun parallel_favorites_share_one_slot() {
        val now = DroidKaigi2026Day.Day1.at(9, 0)
        val state = computeFavoritesWidgetState(
            now,
            timetable(
                item("a", startsAt = "10:00", endsAt = "10:40", room = Room.OTTER),
                item("b", startsAt = "10:00", endsAt = "10:40", room = Room.PANDA),
            ),
            ids("a", "b"),
        )
        val schedule = assertIs<FavoritesWidgetState.Schedule>(state)
        val slot = schedule.slots.single()
        assertEquals(listOf("a", "b"), slot.sessions.map { it.id.value })
    }

    @Test
    fun rows_fit_without_a_count_when_sessions_do_not_overflow() {
        val slots = listOf(
            FavoritesWidgetSlot("10:00", "10:40", isLive = false, sessions = listOf(item("a", startsAt = "10:00", endsAt = "10:40"))),
            FavoritesWidgetSlot("11:00", "11:40", isLive = false, sessions = listOf(item("b", startsAt = "11:00", endsAt = "11:40"))),
        )
        val rows = slots.toFavoritesWidgetRows(maxRows = 3)
        assertEquals(2, rows.size)
        assertEquals(listOf(true, true), rows.map { (it as FavoritesWidgetRow.Session).showsTime })
    }

    @Test
    fun a_shared_slot_prints_its_time_once() {
        val slots = listOf(
            FavoritesWidgetSlot(
                "10:00",
                "10:40",
                isLive = true,
                sessions = listOf(
                    item("a", startsAt = "10:00", endsAt = "10:40"),
                    item("b", startsAt = "10:00", endsAt = "10:40"),
                ),
            ),
        )
        val rows = slots.toFavoritesWidgetRows(maxRows = 3)
        assertEquals(listOf(true, false), rows.map { (it as FavoritesWidgetRow.Session).showsTime })
        assertEquals(listOf(true, true), rows.map { (it as FavoritesWidgetRow.Session).isLive })
    }

    @Test
    fun overflowing_sessions_collapse_into_a_count_row() {
        val slots = listOf(
            FavoritesWidgetSlot(
                "10:00",
                "10:40",
                isLive = false,
                sessions = listOf(
                    item("a", startsAt = "10:00", endsAt = "10:40"),
                    item("b", startsAt = "10:00", endsAt = "10:40"),
                ),
            ),
            FavoritesWidgetSlot("11:00", "11:40", isLive = false, sessions = listOf(item("c", startsAt = "11:00", endsAt = "11:40"))),
            FavoritesWidgetSlot("12:00", "12:40", isLive = false, sessions = listOf(item("d", startsAt = "12:00", endsAt = "12:40"))),
        )
        val rows = slots.toFavoritesWidgetRows(maxRows = 3)
        assertEquals(3, rows.size)
        assertEquals(FavoritesWidgetRow.More(count = 2), rows.last())
    }

    @Test
    fun an_empty_timetable_on_conference_days_reads_as_empty() {
        val now = DroidKaigi2026Day.Day1.at(9, 0)
        val state = computeFavoritesWidgetState(now, Timetable(items = persistentListOf()), ids("a"))
        assertEquals(FavoritesWidgetState.Empty, state)
    }
}

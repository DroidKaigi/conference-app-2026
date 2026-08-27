package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class FavoritesWidgetStateTest {
    private fun item(
        id: String,
        day: DroidKaigi2026Day = DroidKaigi2026Day.Day1,
        startsAt: String,
        endsAt: String,
        room: SessionRoom = SessionRoom.OTTER,
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
            sessionType = SessionType.NORMAL,
            startsAtInstant = day.at(hour = startHour, minute = startMinute),
            endsAtInstant = day.at(hour = endHour, minute = endMinute),
            description = MultiLangText(ja = "", en = ""),
            targetAudience = MultiLangText(ja = "", en = ""),
            category = null,
            asset = TimetableItemAsset.Empty,
            hasInterpretation = false,
            isCancelled = false,
            message = null,
        )
    }

    private fun timetable(vararg items: TimetableItem) = Timetable(items = items.toList().toPersistentList())

    private fun ids(vararg values: String) = values.map(::TimetableItemId).toSet()

    private val conferenceEnd = DroidKaigi2026Day.Day2.at(0, 0) + 24.hours
    private val eventDayStart = DroidKaigi2026Day.Day1.at(0, 0) - 24.hours

    @Test
    fun before_the_conference_counts_days_regardless_of_favorites() {
        val now = DroidKaigi2026Day.Day1.at(9, 0) - 14.days
        val state = computeFavoritesWidgetState(now, timetable(item("a", startsAt = "10:00", endsAt = "10:40")), ids("a"))
        assertEquals(FavoritesWidgetState.Countdown(daysUntilStart = 14), state)
    }

    @Test
    fun the_night_before_the_event_day_counts_two_days() {
        val state = computeFavoritesWidgetState(eventDayStart - 1.hours, timetable(), emptySet())
        assertEquals(FavoritesWidgetState.Countdown(daysUntilStart = 2), state)
    }

    @Test
    fun the_day_before_day1_reads_as_the_event_day() {
        assertEquals(FavoritesWidgetState.EventDay, computeFavoritesWidgetState(eventDayStart, timetable(), emptySet()))
        assertEquals(
            FavoritesWidgetState.EventDay,
            computeFavoritesWidgetState(
                DroidKaigi2026Day.Day1.at(0, 0) - 1.minutes,
                timetable(item("a", startsAt = "10:00", endsAt = "10:40")),
                ids("a"),
            ),
        )
    }

    @Test
    fun a_day_without_favorites_prompts_with_the_other_day_count() {
        val now = DroidKaigi2026Day.Day1.at(9, 0)
        val state = computeFavoritesWidgetState(
            now,
            timetable(
                item("d1", startsAt = "10:00", endsAt = "10:40"),
                item("d2a", day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40"),
                item("d2b", day = DroidKaigi2026Day.Day2, startsAt = "11:00", endsAt = "11:40"),
            ),
            ids("d2a", "d2b"),
        )
        assertEquals(FavoritesWidgetState.Empty(day = DroidKaigi2026Day.Day1, otherDayFavorites = 2), state)
    }

    @Test
    fun day2_counts_no_other_day_favorites() {
        val now = DroidKaigi2026Day.Day2.at(9, 0)
        val state = computeFavoritesWidgetState(
            now,
            timetable(
                item("d1", startsAt = "10:00", endsAt = "10:40"),
                item("d2", day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40"),
            ),
            ids("d1"),
        )
        assertEquals(FavoritesWidgetState.Empty(day = DroidKaigi2026Day.Day2, otherDayFavorites = 0), state)
    }

    @Test
    fun after_the_conference_thanks_the_visitor() {
        val now = DroidKaigi2026Day.Day2.at(9, 0) + 1.days
        val state = computeFavoritesWidgetState(now, timetable(item("a", startsAt = "10:00", endsAt = "10:40")), ids("a"))
        assertEquals(FavoritesWidgetState.PostConference, state)
    }

    @Test
    fun the_schedule_holds_only_the_favorites_of_the_current_day() {
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
        assertEquals(DroidKaigi2026Day.Day1, schedule.day)
        assertEquals(
            listOf("early", "late"),
            schedule.slots.map { slot -> slot.sessions.single().id.value },
        )
        assertEquals(listOf(false, false), schedule.slots.map(FavoritesWidgetSlot::isLive))
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
                item("a", startsAt = "10:00", endsAt = "10:40", room = SessionRoom.OTTER),
                item("b", startsAt = "10:00", endsAt = "10:40", room = SessionRoom.PANDA),
            ),
            ids("a", "b"),
        )
        val schedule = assertIs<FavoritesWidgetState.Schedule>(state)
        val slot = schedule.slots.single()
        assertEquals(listOf("a", "b"), slot.sessions.map { it.id.value })
    }

    @Test
    fun favorites_that_all_ended_read_as_done_while_the_day_runs_on() {
        val now = DroidKaigi2026Day.Day1.at(11, 0)
        val state = computeFavoritesWidgetState(
            now,
            timetable(
                item("a", startsAt = "10:00", endsAt = "10:40"),
                item("closing", startsAt = "17:00", endsAt = "18:00"),
                item("d2", day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40"),
            ),
            ids("a", "d2"),
        )
        assertEquals(FavoritesWidgetState.TodayDone(day = DroidKaigi2026Day.Day1, otherDayFavorites = 1), state)
    }

    @Test
    fun day1_wraps_up_once_every_day1_session_has_ended() {
        val now = DroidKaigi2026Day.Day1.at(19, 0)
        val state = computeFavoritesWidgetState(
            now,
            timetable(
                item("a", startsAt = "10:00", endsAt = "10:40"),
                item("closing", startsAt = "17:00", endsAt = "18:00"),
                item("d2", day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40"),
            ),
            ids("a", "d2"),
        )
        assertEquals(FavoritesWidgetState.DayWrapUp(tomorrowFavorites = 1), state)
    }

    @Test
    fun day2_reads_as_post_conference_once_its_programme_has_ended() {
        val now = DroidKaigi2026Day.Day2.at(20, 0)
        val state = computeFavoritesWidgetState(
            now,
            timetable(item("a", day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40")),
            ids("a"),
        )
        assertEquals(FavoritesWidgetState.PostConference, state)
    }

    @Test
    fun a_day_with_no_timetable_item_reads_as_empty_rather_than_over() {
        val now = DroidKaigi2026Day.Day1.at(20, 0)
        assertEquals(
            FavoritesWidgetState.Empty(day = DroidKaigi2026Day.Day1, otherDayFavorites = 0),
            computeFavoritesWidgetState(now, Timetable(items = persistentListOf()), ids("a")),
        )
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
    fun before_the_event_day_the_boundary_is_the_next_conference_midnight() {
        val now = DroidKaigi2026Day.Day1.at(9, 0) - 14.days
        val boundary = nextFavoritesWidgetBoundary(now, timetable(item("a", startsAt = "10:00", endsAt = "10:40")), ids("a"))
        assertEquals(DroidKaigi2026Day.Day1.at(0, 0) - 13.days, boundary)
    }

    @Test
    fun the_event_day_is_bounded_by_day1() {
        val boundary = nextFavoritesWidgetBoundary(eventDayStart + 9.hours, timetable(), emptySet())
        assertEquals(DroidKaigi2026Day.Day1.at(0, 0), boundary)
    }

    @Test
    fun during_a_day_the_boundary_is_the_next_favorite_start_or_end() {
        val timetable = timetable(
            item("a", startsAt = "10:00", endsAt = "10:40"),
            item("b", startsAt = "11:00", endsAt = "11:40"),
            item("c", startsAt = "10:20", endsAt = "10:50"),
        )
        assertEquals(DroidKaigi2026Day.Day1.at(10, 0), nextFavoritesWidgetBoundary(DroidKaigi2026Day.Day1.at(9, 0), timetable, ids("a", "b")))
        assertEquals(DroidKaigi2026Day.Day1.at(10, 20), nextFavoritesWidgetBoundary(DroidKaigi2026Day.Day1.at(10, 5), timetable, ids("a", "b", "c")))
        assertEquals(DroidKaigi2026Day.Day1.at(10, 40), nextFavoritesWidgetBoundary(DroidKaigi2026Day.Day1.at(10, 20), timetable, ids("a", "b", "c")))
        assertEquals(DroidKaigi2026Day.Day1.at(11, 0), nextFavoritesWidgetBoundary(DroidKaigi2026Day.Day1.at(10, 40), timetable, ids("a", "b")))
    }

    @Test
    fun the_other_days_favorites_do_not_bound_today() {
        val timetable = timetable(
            item("d1", startsAt = "10:00", endsAt = "10:40"),
            item("d2", day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40"),
        )
        assertEquals(
            DroidKaigi2026Day.Day1.at(10, 0),
            nextFavoritesWidgetBoundary(DroidKaigi2026Day.Day1.at(9, 0), timetable, ids("d1", "d2")),
        )
    }

    @Test
    fun the_days_last_session_end_bounds_a_day_whose_favorites_have_ended() {
        val timetable = timetable(
            item("a", startsAt = "10:00", endsAt = "10:40"),
            item("closing", startsAt = "17:00", endsAt = "18:00"),
        )
        assertEquals(
            DroidKaigi2026Day.Day1.at(18, 0),
            nextFavoritesWidgetBoundary(DroidKaigi2026Day.Day1.at(11, 0), timetable, ids("a")),
        )
    }

    @Test
    fun a_finished_day1_is_bounded_by_the_midnight_that_starts_day2() {
        val timetable = timetable(
            item("closing", startsAt = "17:00", endsAt = "18:00"),
            item("d2", day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40"),
        )
        assertEquals(
            DroidKaigi2026Day.Day2.at(0, 0),
            nextFavoritesWidgetBoundary(DroidKaigi2026Day.Day1.at(19, 0), timetable, ids("d2")),
        )
    }

    @Test
    fun day2_falls_back_to_the_conference_end() {
        val timetable = timetable(item("a", day = DroidKaigi2026Day.Day2, startsAt = "10:00", endsAt = "10:40"))
        assertEquals(conferenceEnd, nextFavoritesWidgetBoundary(DroidKaigi2026Day.Day2.at(11, 0), timetable, ids("a")))
        assertEquals(conferenceEnd, nextFavoritesWidgetBoundary(DroidKaigi2026Day.Day2.at(9, 0), Timetable(items = persistentListOf()), ids("a")))
    }

    @Test
    fun after_the_conference_there_is_no_boundary() {
        val timetable = timetable(item("a", startsAt = "10:00", endsAt = "10:40"))
        assertNull(nextFavoritesWidgetBoundary(conferenceEnd, timetable, ids("a")))
    }
}

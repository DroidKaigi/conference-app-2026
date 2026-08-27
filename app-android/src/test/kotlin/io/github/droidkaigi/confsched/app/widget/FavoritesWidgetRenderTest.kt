package io.github.droidkaigi.confsched.app.widget

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.FavoritesWidgetState
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.SessionType
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemAsset
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableSpeaker
import io.github.droidkaigi.confsched.core.model.TimetableSpeakerId
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesWidgetRenderTest {
    private val sessionId = TimetableItemId("a")
    private val timetable = Timetable(
        items = persistentListOf(
            TimetableItem(
                id = sessionId,
                title = MultiLangText(ja = "セッション A", en = "Session A"),
                room = SessionRoom.OTTER,
                speakers = persistentListOf(
                    TimetableSpeaker(
                        id = TimetableSpeakerId("speaker-a"),
                        name = "Speaker A",
                        tagLine = "",
                        iconUrl = null,
                    ),
                ),
                language = Language.JAPANESE,
                day = DroidKaigi2026Day.Day1,
                startsAt = "10:00",
                endsAt = "10:40",
                sessionType = SessionType.NORMAL,
                startsAtInstant = DroidKaigi2026Day.Day1.at(10, 0),
                endsAtInstant = DroidKaigi2026Day.Day1.at(10, 40),
                description = MultiLangText(ja = "", en = ""),
                targetAudience = MultiLangText(ja = "", en = ""),
                category = null,
                asset = TimetableItemAsset.Empty,
                hasInterpretation = false,
                isCancelled = false,
                message = null,
            ),
        ),
    )
    private val duringDay1 = DroidKaigi2026Day.Day1.at(9, 0)

    private fun renders(
        favoriteIds: MutableStateFlow<Set<TimetableItemId>> = MutableStateFlow(emptySet()),
        colorSchemes: MutableStateFlow<KaigiColorScheme> = MutableStateFlow(KaigiColorScheme.entries.first()),
        clockOffsets: MutableStateFlow<Duration> = MutableStateFlow(Duration.ZERO),
        readTimetable: suspend () -> Timetable? = { timetable },
        now: () -> Instant = { duringDay1 },
    ) = favoritesWidgetRenders(
        favoriteIds = favoriteIds,
        colorSchemes = colorSchemes,
        clockOffsets = clockOffsets,
        readTimetable = readTimetable,
        now = now,
    )

    @Test
    fun a_favorite_starting_while_collected_produces_a_live_render() = runTest {
        var now = duringDay1
        val collected = mutableListOf<FavoritesWidgetRender>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            renders(favoriteIds = MutableStateFlow(setOf(sessionId)), now = { now }).toList(collected)
        }
        assertEquals(1, collected.size)

        now = DroidKaigi2026Day.Day1.at(10, 0)
        advanceTimeBy(1.hours + 1.milliseconds)

        assertEquals(2, collected.size)
        assertTrue((collected.last().state as FavoritesWidgetState.Schedule).slots.single().isLive)
    }

    @Test
    fun a_clock_offset_change_after_the_first_render_produces_a_new_render() = runTest {
        val clockOffsets = MutableStateFlow(Duration.ZERO)
        val collected = mutableListOf<FavoritesWidgetRender>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            renders(clockOffsets = clockOffsets).toList(collected)
        }

        clockOffsets.value = 1.hours

        assertEquals(2, collected.size)
    }

    @Test
    fun a_favorite_added_after_the_first_render_produces_a_new_render() = runTest {
        val favoriteIds = MutableStateFlow<Set<TimetableItemId>>(emptySet())
        val collected = mutableListOf<FavoritesWidgetRender>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            renders(favoriteIds = favoriteIds).toList(collected)
        }

        assertEquals(
            FavoritesWidgetState.Empty(day = DroidKaigi2026Day.Day1, otherDayFavorites = 0),
            collected.single().state,
        )

        favoriteIds.value = setOf(sessionId)

        assertEquals(2, collected.size)
        val slots = (collected.last().state as FavoritesWidgetState.Schedule).slots
        assertEquals(listOf(sessionId), slots.single().sessions.map { it.id })
    }

    @Test
    fun a_color_scheme_change_after_the_first_render_produces_a_new_render() = runTest {
        val colorSchemes = MutableStateFlow(KaigiColorScheme.entries.first())
        val collected = mutableListOf<FavoritesWidgetRender>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            renders(colorSchemes = colorSchemes).toList(collected)
        }

        colorSchemes.value = KaigiColorScheme.entries.last { it != colorSchemes.value }

        assertEquals(2, collected.size)
        assertNotEquals(collected.first().colors, collected.last().colors)
    }

    @Test
    fun each_render_re_reads_the_persisted_timetable() = runTest {
        val favoriteIds = MutableStateFlow<Set<TimetableItemId>>(emptySet())
        var reads = 0
        val collected = mutableListOf<FavoritesWidgetRender>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            renders(favoriteIds = favoriteIds, readTimetable = {
                reads++
                timetable
            }).toList(collected)
        }

        favoriteIds.value = setOf(sessionId)

        assertEquals(2, reads)
    }

    @Test
    fun a_missing_persisted_timetable_still_renders() = runTest {
        val collected = mutableListOf<FavoritesWidgetRender>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            renders(favoriteIds = MutableStateFlow(setOf(sessionId)), readTimetable = { null }).toList(collected)
        }

        assertTrue(collected.single().state is FavoritesWidgetState.Empty)
    }
}

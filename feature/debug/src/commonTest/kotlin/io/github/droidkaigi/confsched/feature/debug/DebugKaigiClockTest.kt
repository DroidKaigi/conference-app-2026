package io.github.droidkaigi.confsched.feature.debug

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class DebugKaigiClockTest {

    private val store = KaigiClockOffsetStore()
    private val clock = DebugKaigiClock(store)

    @Test
    fun reports_system_time_until_it_is_shifted() {
        assertEquals(Duration.ZERO, store.offset.value)
        assertTrue((clock.now() - Clock.System.now()).absoluteValue < tolerance)
    }

    @Test
    fun shifting_moves_now_to_the_target_instant() {
        val target = DroidKaigi2026Day.Day1.at(hour = 10, minute = 0)

        store.shiftTo(target)

        assertTrue((clock.now() - target).absoluteValue < tolerance)
    }

    @Test
    fun a_shifted_clock_stays_ahead_of_the_system_clock_by_the_offset() {
        store.shiftTo(DroidKaigi2026Day.Day1.at(hour = 10, minute = 0))

        val systemNow = Clock.System.now()
        val shiftedNow = clock.now()

        assertTrue((shiftedNow - systemNow - store.offset.value).absoluteValue < tolerance)
    }

    @Test
    fun resetting_returns_to_system_time() {
        store.shiftTo(DroidKaigi2026Day.Day1.at(hour = 10, minute = 0))

        store.reset()

        assertEquals(Duration.ZERO, store.offset.value)
        assertTrue((clock.now() - Clock.System.now()).absoluteValue < tolerance)
    }

    private companion object {
        // shiftTo and now() read the system clock at different moments, so the two straddle a real interval.
        val tolerance = 1.seconds
    }
}

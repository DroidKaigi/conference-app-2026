package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimetableGridScrollStateTest {

    @Test
    fun dragBy_updates_scroll_position_within_bounds() {
        val state = TimetableGridScrollState(initialScrollX = 50f, initialScrollY = 50f)
        state.updateBounds(maxScrollX = 200f, maxScrollY = 200f)

        val consumed = state.dragBy(Offset(20f, 30f))
        assertEquals(20f, consumed.x)
        assertEquals(30f, consumed.y)
        assertEquals(30f, state.scrollX)
        assertEquals(20f, state.scrollY)
    }

    @Test
    fun dragBy_clamps_at_bounds() {
        val state = TimetableGridScrollState(initialScrollX = 10f, initialScrollY = 10f)
        state.updateBounds(maxScrollX = 100f, maxScrollY = 100f)

        val consumed = state.dragBy(Offset(30f, -150f))
        assertEquals(10f, consumed.x)
        assertEquals(-90f, consumed.y)
        assertEquals(0f, state.scrollX)
        assertEquals(100f, state.scrollY)
    }

    @OptIn(
        androidx.compose.ui.test.ExperimentalTestApi::class,
        kotlinx.coroutines.ExperimentalCoroutinesApi::class,
    )
    @Test
    fun fling_decelerates_and_scrolls_content_forward() = runTest {
        withContext(androidx.compose.ui.test.TestMonotonicFrameClock(this)) {
            val state = TimetableGridScrollState(initialScrollX = 100f, initialScrollY = 100f)
            state.updateBounds(maxScrollX = 500f, maxScrollY = 500f)
            val dispatcher = NestedScrollDispatcher()
            val decay = exponentialDecay<Float>()

            state.fling(
                velocity = Velocity(x = -100f, y = -100f),
                decay = decay,
                dispatcher = dispatcher,
            )

            assertTrue(state.scrollX > 100f, "scrollX should have increased during fling")
            assertTrue(state.scrollY > 100f, "scrollY should have increased during fling")
        }
    }
}

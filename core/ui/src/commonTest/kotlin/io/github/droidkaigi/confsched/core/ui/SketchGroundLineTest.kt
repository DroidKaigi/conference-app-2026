package io.github.droidkaigi.confsched.core.ui

import kotlin.test.Test
import kotlin.test.assertContentEquals

class SketchGroundLineTest {
    @Test
    fun positions_keep_the_period_and_leave_only_the_last_cell_short() {
        assertContentEquals(
            expected = floatArrayOf(0f, 26f, 52f, 78f, 104f, 130f, 156f, 182f, 208f, 234f, 252f),
            actual = groundLinePositions(width = 252f, period = 26f),
        )
        assertContentEquals(
            expected = floatArrayOf(
                0f,
                26f,
                52f,
                78f,
                104f,
                130f,
                156f,
                182f,
                208f,
                234f,
                260f,
                284f,
            ),
            actual = groundLinePositions(width = 284f, period = 26f),
        )
    }
}

package io.github.droidkaigi.confsched.core.common

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class StatusBarBandStateTest {

    @Test
    fun the_fallback_stands_while_nothing_reports() {
        val state = StatusBarBandState()
        state.fallback = Color.White

        assertEquals(Color.White, state.bandColor)
    }

    @Test
    fun a_report_overrides_the_fallback() {
        val state = StatusBarBandState()
        state.fallback = Color.White
        state.add(StatusBarBandReport(Color.Black))

        assertEquals(Color.Black, state.bandColor)
    }

    @Test
    fun the_last_of_two_reports_wins() {
        val state = StatusBarBandState()
        val first = StatusBarBandReport(Color.Black)
        val second = StatusBarBandReport(Color.Gray)
        state.add(first)
        state.add(second)

        assertEquals(Color.Gray, state.bandColor)
    }

    @Test
    fun removing_the_last_report_uncovers_the_previous_one() {
        val state = StatusBarBandState()
        val first = StatusBarBandReport(Color.Black)
        val second = StatusBarBandReport(Color.Gray)
        state.add(first)
        state.add(second)
        state.remove(second)

        assertEquals(Color.Black, state.bandColor)
    }

    @Test
    fun removing_every_report_restores_the_fallback() {
        val state = StatusBarBandState()
        state.fallback = Color.White
        val report = StatusBarBandReport(Color.Black)
        state.add(report)
        state.remove(report)

        assertEquals(Color.White, state.bandColor)
    }

    @Test
    fun a_report_carries_its_color_changes() {
        val state = StatusBarBandState()
        val report = StatusBarBandReport(Color.Black)
        state.add(report)
        report.color = Color.Gray

        assertEquals(Color.Gray, state.bandColor)
    }
}

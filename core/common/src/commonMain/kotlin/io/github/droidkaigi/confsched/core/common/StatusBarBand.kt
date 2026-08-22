package io.github.droidkaigi.confsched.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The color filling the band behind the status bar, assembled from what the composition draws
 * there. A top app bar reports its container color through [StatusBarBandEffect] while it is
 * composed; while no report stands, [fallback] — the theme background — fills in.
 *
 * Only a shell that sets status bar icon appearance installs one: the Android activity. The
 * default null everywhere else turns the reports into no-ops.
 */
class StatusBarBandState {
    private val reports = mutableStateListOf<StatusBarBandReport>()

    var fallback: Color by mutableStateOf(Color.Unspecified)

    // Last report wins: reports only disagree while a transition composes two entries at once,
    // and the entering entry reports after the leaving one.
    val bandColor: Color get() = reports.lastOrNull()?.color ?: fallback

    internal fun add(report: StatusBarBandReport) {
        reports.add(report)
    }

    internal fun remove(report: StatusBarBandReport) {
        reports.remove(report)
    }
}

internal class StatusBarBandReport(color: Color) {
    var color: Color by mutableStateOf(color)
}

val LocalStatusBarBandState = staticCompositionLocalOf<StatusBarBandState?> { null }

/** Reports [color] as the band behind the status bar while the caller is composed. */
@Composable
fun StatusBarBandEffect(color: Color) {
    val state = LocalStatusBarBandState.current ?: return
    val report = remember { StatusBarBandReport(color) }
    SideEffect(color) { report.color = color }
    DisposableEffect(state, report) {
        state.add(report)
        onDispose { state.remove(report) }
    }
}

/** Keeps [StatusBarBandState.fallback] at [color], the band shown where nothing reports one. */
@Composable
fun StatusBarBandFallbackEffect(color: Color) {
    val state = LocalStatusBarBandState.current ?: return
    SideEffect(color) { state.fallback = color }
}

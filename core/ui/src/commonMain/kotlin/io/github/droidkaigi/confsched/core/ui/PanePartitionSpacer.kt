package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp

/**
 * Width a pane shown side-by-side reserves toward the shared pane boundary.
 *
 * The scaffold lays panes edge-to-edge so their backgrounds meet without a gap; instead of the
 * scaffold spacing the panes apart, a pane applies this inset to its own content along the
 * boundary edge. The value is ambient for the whole navigation display, so a screen must apply
 * it only while it is actually shown alongside another pane.
 */
val LocalPanePartitionSpacerSize = compositionLocalOf { 0.dp }

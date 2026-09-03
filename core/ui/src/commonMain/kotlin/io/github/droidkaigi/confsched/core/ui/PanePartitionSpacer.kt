package io.github.droidkaigi.confsched.core.ui

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Width a pane shown side-by-side reserves toward the shared pane boundary, applied by the pane
 * itself inside its edge-running backgrounds, only while another pane is beside it.
 */
val LocalPanePartitionSpacerSize = compositionLocalOf { 0.dp }

/**
 * [LocalPanePartitionSpacerSize] while the caller is composed inside a list-detail scaffold, and
 * zero otherwise, for a detail pane to hold its content clear of the boundary it shares with the
 * list. A list pane reads the same scope, so it must not take this inset: its own edge is the
 * window's.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun paneStartInset(): Dp =
    if (LocalListDetailSceneScope.current != null) LocalPanePartitionSpacerSize.current else 0.dp

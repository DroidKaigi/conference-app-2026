package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp

/**
 * Width a pane shown side-by-side reserves toward the shared pane boundary, applied by the pane
 * itself inside its edge-running backgrounds, only while another pane is beside it.
 */
val LocalPanePartitionSpacerSize = compositionLocalOf { 0.dp }

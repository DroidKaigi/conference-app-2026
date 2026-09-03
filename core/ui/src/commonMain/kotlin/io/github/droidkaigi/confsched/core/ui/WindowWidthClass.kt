package io.github.droidkaigi.confsched.core.ui

import androidx.compose.ui.unit.Dp
import androidx.window.core.layout.WindowSizeClass

/**
 * Whether a layout handed this much width has reached the expanded window width class — the same
 * breakpoint the app's adaptive scenes lay a second pane out at, so a layout deciding for itself
 * still turns at the width the rest of the app turns at.
 */
val Dp.isExpandedWindowWidth: Boolean
    get() = value >= WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND

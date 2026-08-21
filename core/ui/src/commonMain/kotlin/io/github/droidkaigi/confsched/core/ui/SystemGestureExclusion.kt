package io.github.droidkaigi.confsched.core.ui

import androidx.compose.ui.Modifier

/**
 * Excludes the element's bounds from the Android system's edge gestures, so a drag starting on
 * an element at the window edge reaches the element instead of the system back gesture. No
 * effect on the other platforms.
 */
expect fun Modifier.androidSystemGestureExclusion(): Modifier

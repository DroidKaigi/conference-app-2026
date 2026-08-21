package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.ui.Modifier

actual fun Modifier.androidSystemGestureExclusion(): Modifier = systemGestureExclusion()

package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable

// The desktop toolkit surfaces no reduced-motion setting.
@Composable
actual fun rememberReducedMotion(): Boolean = false

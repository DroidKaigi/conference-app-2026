package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable

/**
 * Whether the platform asks for reduced motion, tracking the setting while the caller is composed.
 *
 * Compose's animation APIs already stop under this setting through `MotionDurationScale`, so an
 * animation built on them needs no check. Motion driven from outside them, such as a sensor value
 * written into a `graphicsLayer`, reads this instead.
 */
@Composable
expect fun rememberReducedMotion(): Boolean

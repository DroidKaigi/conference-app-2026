package io.github.droidkaigi.confsched.core.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.UiComposable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Renders the time the app is reading while that differs from the system clock. The debug feature
 * module replaces the no-op binding to show it.
 */
interface ClockOverlay {
    /** Rendered on top of the app content. No-op in production builds. */
    @Composable
    @UiComposable
    fun Overlay()
}

@Inject
@ContributesBinding(AppScope::class)
class NoopClockOverlay : ClockOverlay {
    @Composable
    override fun Overlay() = Unit
}

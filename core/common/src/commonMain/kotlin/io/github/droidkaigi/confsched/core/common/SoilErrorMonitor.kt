package io.github.droidkaigi.confsched.core.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.UiComposable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Renders an overlay that surfaces errors reported by Soil queries/mutations/subscriptions.
 * The debug feature module replaces the no-op binding to display the errors.
 */
interface SoilErrorMonitor {
    /** Rendered on top of the app content. No-op in production builds. */
    @Composable
    @UiComposable
    fun Overlay()
}

@Inject
@ContributesBinding(AppScope::class)
class NoopSoilErrorMonitor : SoilErrorMonitor {
    @Composable
    override fun Overlay() = Unit
}

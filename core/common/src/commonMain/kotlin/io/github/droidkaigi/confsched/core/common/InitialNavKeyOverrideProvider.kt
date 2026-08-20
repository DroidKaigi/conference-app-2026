package io.github.droidkaigi.confsched.core.common

import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Overrides the app's initial back stack. The debug feature module replaces the no-op binding
 * to open on its server picker; the web target replaces it to restore the URL's destination.
 */
interface InitialNavKeyOverrideProvider {
    /**
     * Null keeps the app's default initial destination. A non-null list replaces the default
     * initial stack; the list must contain at least one entry.
     */
    val initialNavStackOverride: List<NavKey>?
}

@Inject
@ContributesBinding(AppScope::class)
class NoopInitialNavKeyOverrideProvider : InitialNavKeyOverrideProvider {
    override val initialNavStackOverride: List<NavKey>? = null
}

package io.github.droidkaigi.confsched.core.common

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Synchronises the app's back stack with a platform history mechanism. The web target keeps the
 * browser address bar in step with in-app navigation and translates browser back/forward into
 * back-stack operations; all other targets do nothing.
 */
fun interface HistorySyncEffect {
    @Composable
    operator fun invoke(backStack: NavBackStack<NavKey>)
}

@Inject
@ContributesBinding(AppScope::class)
class NoopHistorySyncEffect : HistorySyncEffect {
    @Composable
    override fun invoke(backStack: NavBackStack<NavKey>) = Unit
}

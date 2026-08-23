package io.github.droidkaigi.confsched.app

import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.InitialNavKeyOverrideProvider
import io.github.droidkaigi.confsched.core.common.NoopInitialNavKeyOverrideProvider
import kotlinx.browser.window

@Inject
@ContributesBinding(AppScope::class, replaces = [NoopInitialNavKeyOverrideProvider::class])
class WebInitialNavStackProvider : InitialNavKeyOverrideProvider {
    override val initialNavStackOverride: List<NavKey> =
        parseUrlHash(window.location.hash)
}

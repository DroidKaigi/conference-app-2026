package io.github.droidkaigi.confsched.feature.debug

import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.InitialNavKeyOverrideProvider
import io.github.droidkaigi.confsched.core.common.NoopInitialNavKeyOverrideProvider

@Inject
@ContributesBinding(AppScope::class, replaces = [NoopInitialNavKeyOverrideProvider::class])
class DebugInitialNavKeyOverrideProvider : InitialNavKeyOverrideProvider {
    override val initialNavStackOverride: List<NavKey> = listOf(ServerEnvironmentNavKey)
}

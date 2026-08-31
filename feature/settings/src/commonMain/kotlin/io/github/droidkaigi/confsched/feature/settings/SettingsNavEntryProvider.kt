package io.github.droidkaigi.confsched.feature.settings

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.consumeListDetailPaneInsets
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.detailPane

@ContributesIntoSet(UiScope::class)
@Inject
class SettingsNavEntryProvider(
    private val screenGraphFactory: SettingsScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<SettingsNavKey>(
            metadata = detailPane() + consumeListDetailPaneInsets(WindowInsetsSides.Start),
        ) {
            val graph = retain(screenGraphFactory::createSettingsScreenGraph)
            context(graph.screenContext) {
                SettingsScreenRoot(
                    onNavigateBack = graph.screenNavigator::back,
                )
            }
        }
    }
}

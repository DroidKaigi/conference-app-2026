package io.github.droidkaigi.confsched.feature.eventmap

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
class StampCollectingNavEntryProvider(
    private val screenGraphFactory: StampCollectingScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<StampCollectingNavKey>(
            metadata = detailPane() + consumeListDetailPaneInsets(WindowInsetsSides.Start),
        ) { key ->
            val graph = retain(screenGraphFactory::createStampCollectingScreenGraph)
            context(graph.screenContext) {
                StampCollectingScreenRoot(
                    onNavigateBack = { graph.screenNavigator.back(origin = key) },
                    onNavigateToPrize = graph.screenNavigator::openPrize,
                )
            }
        }
    }
}

package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.context

@ContributesIntoSet(UiScope::class)
@Inject
class StampCollectingNavEntryProvider(
    private val screenGraphFactory: StampCollectingScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<StampCollectingNavKey> { key ->
            val graph = retain(screenGraphFactory::createStampCollectingScreenGraph)
            context(graph.screenContext) {
                StampCollectingScreenRoot(
                    onNavigateBack = { graph.screenNavigator.back(origin = key) },
                )
            }
        }
    }
}

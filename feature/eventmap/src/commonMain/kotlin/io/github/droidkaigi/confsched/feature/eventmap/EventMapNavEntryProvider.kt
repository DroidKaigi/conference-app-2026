package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.instantNavTransition

@ContributesIntoSet(UiScope::class)
@Inject
class EventMapNavEntryProvider(
    private val screenGraphFactory: EventMapScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<EventMapNavKey>(metadata = instantNavTransition()) {
            val graph = retain(screenGraphFactory::createEventMapScreenGraph)
            context(graph.screenContext) {
                EventMapScreenRoot(
                    onNavigateToStampCollecting = graph.screenNavigator::openStampCollecting,
                )
            }
        }
    }
}

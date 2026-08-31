package io.github.droidkaigi.confsched.feature.favorites

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
import io.github.droidkaigi.confsched.core.common.instantNavTransition
import io.github.droidkaigi.confsched.core.common.listPane

@ContributesIntoSet(UiScope::class)
@Inject
class FavoritesNavEntryProvider(
    private val screenGraphFactory: FavoritesScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<FavoritesNavKey>(
            metadata = listPane() +
                consumeListDetailPaneInsets(WindowInsetsSides.End) +
                instantNavTransition(),
        ) {
            val graph = retain(screenGraphFactory::createFavoritesScreenGraph)
            context(graph.screenContext) {
                FavoritesScreenRoot(
                    onNavigateToDetail = graph.screenNavigator::openSessionDetail,
                )
            }
        }
    }
}

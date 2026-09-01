package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.RootSceneStrategy
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.consumeListDetailPaneInsets
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.instantNavTransition

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@ContributesIntoSet(UiScope::class)
@Inject
class TimetableNavEntryProvider(
    private val screenGraphFactory: TimetableScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<TimetableNavKey>(
            metadata = RootSceneStrategy.root() +
                ListDetailSceneStrategy.listPane() +
                consumeListDetailPaneInsets(WindowInsetsSides.End) +
                instantNavTransition(),
        ) {
            val graph = retain(screenGraphFactory::createTimetableScreenGraph)
            context(graph.screenContext) {
                TimetableScreenRoot(
                    onNavigateToDetail = graph.screenNavigator::openSessionDetail,
                    onNavigateToSearch = graph.screenNavigator::openSearch,
                    onFavoriteAdded = graph.screenNavigator::offerFirstFavoriteGuidance,
                )
            }
        }
    }
}

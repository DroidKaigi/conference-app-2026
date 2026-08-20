package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.context

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@ContributesIntoSet(UiScope::class)
@Inject
class TimetableItemDetailNavEntryProvider(
    private val screenGraphFactory: TimetableItemDetailScreenGraph.Factory,
    private val appNavigator: AppNavigator,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<TimetableItemDetailNavKey>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
            val graph = retain(key) { screenGraphFactory.createTimetableItemDetailScreenGraph(key.id) }
            context(graph.screenContext) {
                TimetableItemDetailScreenRoot(onNavigateBack = { appNavigator.back(origin = key) })
            }
        }
    }
}

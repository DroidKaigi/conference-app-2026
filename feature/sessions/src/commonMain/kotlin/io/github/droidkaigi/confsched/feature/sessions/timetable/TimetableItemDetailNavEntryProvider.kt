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
import io.github.droidkaigi.confsched.core.model.TimetableItemDetailScreenScope

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Inject
@ContributesIntoSet(TimetableItemDetailScreenScope::class)
class TimetableItemDetailNavEntryProvider(
    private val screenGraphFactory: TimetableItemDetailScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<TimetableItemDetailNavKey>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
            val graph = retain(key) { screenGraphFactory.createTimetableItemDetailScreenGraph(key.id) }
            context(graph.screenContext) {
                TimetableItemDetailScreenRoot(onNavigateBack = graph.screenNavigator::back)
            }
        }
    }
}

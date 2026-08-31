package io.github.droidkaigi.confsched.feature.doodle

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
class DoodleNavEntryProvider(
    private val screenGraphFactory: DoodleScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<DoodleNavKey> { key ->
            val graph = retain(key) { screenGraphFactory.createDoodleScreenGraph(key.target) }
            context(graph.screenContext) {
                DoodleScreenRoot(onNavigateBack = { graph.screenNavigator.back(origin = key) })
            }
        }
    }
}

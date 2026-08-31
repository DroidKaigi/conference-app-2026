package io.github.droidkaigi.confsched.feature.about

import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.context

@ContributesIntoSet(UiScope::class)
@Inject
class DoodleNavEntryProvider(
    private val screenGraphFactory: DoodleScreenGraph.Factory,
    private val appNavigator: AppNavigator,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<DoodleNavKey> {
            val graph = retain(screenGraphFactory::createDoodleScreenGraph)
            context(graph.screenContext) {
                DoodleScreenRoot(
                    onNavigateBack = appNavigator::back,
                )
            }
        }
    }
}

package io.github.droidkaigi.confsched.feature.debug

import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.DebugScreenScope

@ContributesIntoSet(UiScope::class)
@Inject
class DebugNavEntryProvider(
    private val screenGraphFactory: DebugScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<DebugNavKey> {
            val graph = retain(screenGraphFactory::createDebugScreenGraph)
            context(graph.screenContext) {
                DebugScreenRoot(
                    onNavigateToSoilErrors = graph.navigator::openSoilErrors,
                    onNavigateBack = graph.navigator::back,
                )
            }
        }
    }
}

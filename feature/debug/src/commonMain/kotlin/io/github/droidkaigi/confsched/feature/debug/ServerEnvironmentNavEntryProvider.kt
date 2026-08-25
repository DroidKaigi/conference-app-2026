package io.github.droidkaigi.confsched.feature.debug

import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.ServerEnvironmentScreenScope

@Inject
@ContributesIntoSet(UiScope::class)
class ServerEnvironmentNavEntryProvider(
    private val screenGraphFactory: ServerEnvironmentScreenGraph.Factory,
) : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.register() {
        entry<ServerEnvironmentNavKey> {
            val graph = retain(screenGraphFactory::createServerEnvironmentScreenGraph)
            context(graph.screenContext) {
                ServerEnvironmentScreenRoot(
                    onNavigateToTimetable = graph.screenNavigator::openTimetable,
                )
            }
        }
    }
}

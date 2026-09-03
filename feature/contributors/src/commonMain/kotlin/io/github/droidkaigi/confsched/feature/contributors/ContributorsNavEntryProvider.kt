package io.github.droidkaigi.confsched.feature.contributors

import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.detailPane

@ContributesIntoSet(UiScope::class)
@Inject
class ContributorsNavEntryProvider(
    private val screenGraphFactory: ContributorsScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<ContributorsNavKey>(
            metadata = detailPane(),
        ) { key ->
            val graph = retain(screenGraphFactory::createContributorsScreenGraph)
            val uriHandler = LocalUriHandler.current
            context(graph.screenContext) {
                ContributorsScreenRoot(
                    onNavigateBack = { graph.screenNavigator.back(origin = key) },
                    onNavigateToContributorProfile = uriHandler::openUri,
                )
            }
        }
    }
}

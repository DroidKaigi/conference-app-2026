package io.github.droidkaigi.confsched.feature.sponsors

import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalUriHandler
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
class SponsorsNavEntryProvider(
    private val screenGraphFactory: SponsorsScreenGraph.Factory,
    private val appNavigator: AppNavigator,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<SponsorsNavKey> { key ->
            val graph = retain(screenGraphFactory::createSponsorsScreenGraph)
            val uriHandler = LocalUriHandler.current
            context(graph.screenContext) {
                SponsorsScreenRoot(
                    onNavigateBack = { appNavigator.back(origin = key) },
                    onNavigateToSponsorSite = uriHandler::openUri,
                )
            }
        }
    }
}

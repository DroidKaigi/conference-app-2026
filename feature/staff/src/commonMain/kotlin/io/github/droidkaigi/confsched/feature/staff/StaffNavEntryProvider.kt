package io.github.droidkaigi.confsched.feature.staff

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.consumeListDetailPaneInsets
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.detailPane

@ContributesIntoSet(UiScope::class)
@Inject
class StaffNavEntryProvider(
    private val screenGraphFactory: StaffScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<StaffNavKey>(
            metadata = detailPane() + consumeListDetailPaneInsets(WindowInsetsSides.Start),
        ) { key ->
            val graph = retain(screenGraphFactory::createStaffScreenGraph)
            val uriHandler = LocalUriHandler.current
            context(graph.screenContext) {
                StaffScreenRoot(
                    onNavigateBack = { graph.screenNavigator.back(origin = key) },
                    onStaffClick = uriHandler::openUri,
                )
            }
        }
    }
}

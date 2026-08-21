package io.github.droidkaigi.confsched.feature.staff

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
class StaffNavEntryProvider(
    private val screenGraphFactory: StaffScreenGraph.Factory,
    private val appNavigator: AppNavigator,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<StaffNavKey> { key ->
            val graph = retain(screenGraphFactory::createStaffScreenGraph)
            val uriHandler = LocalUriHandler.current
            context(graph.screenContext) {
                StaffScreenRoot(
                    onNavigateBack = { appNavigator.back(origin = key) },
                    onStaffClick = uriHandler::openUri,
                )
            }
        }
    }
}

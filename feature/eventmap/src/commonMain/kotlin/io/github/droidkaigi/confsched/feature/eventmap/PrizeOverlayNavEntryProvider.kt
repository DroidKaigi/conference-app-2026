package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.runtime.retain.retain
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.context

@ContributesIntoSet(UiScope::class)
@Inject
class PrizeOverlayNavEntryProvider(
    private val screenGraphFactory: PrizeOverlayScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        // The overlay fills the window, so it opts out of the platform dialog width.
        entry<PrizeOverlayNavKey>(
            metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
        ) { key ->
            val graph = retain(key) { screenGraphFactory.createPrizeOverlayScreenGraph(key) }
            context(graph.screenContext) {
                PrizeOverlayScreenRoot(onNavigateBack = { graph.screenNavigator.back(origin = key) })
            }
        }
    }
}

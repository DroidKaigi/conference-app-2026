package io.github.droidkaigi.confsched.feature.favorites

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
class FirstFavoriteWidgetNavEntryProvider(
    private val screenGraphFactory: FirstFavoriteWidgetScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<FirstFavoriteWidgetNavKey>(
            metadata = DialogSceneStrategy.dialog(
                DialogProperties(dismissOnClickOutside = false, usePlatformDefaultWidth = false),
            ),
        ) { key ->
            val graph = retain(screenGraphFactory::createFirstFavoriteWidgetScreenGraph)
            context(graph.screenContext) {
                FirstFavoriteWidgetScreenRoot(
                    onNavigateBack = { graph.screenNavigator.back(origin = key) },
                )
            }
        }
    }
}

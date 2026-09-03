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
class FirstFavoriteNotificationNavEntryProvider(
    private val screenGraphFactory: FirstFavoriteNotificationScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        // A tap outside is not an answer, so it leaves the dialog standing; back still closes it.
        entry<FirstFavoriteNotificationNavKey>(
            metadata = DialogSceneStrategy.dialog(
                DialogProperties(dismissOnClickOutside = false, usePlatformDefaultWidth = false),
            ),
        ) { key ->
            val graph = retain(screenGraphFactory::createFirstFavoriteNotificationScreenGraph)
            context(graph.screenContext) {
                FirstFavoriteNotificationScreenRoot(
                    mascot = key.mascot,
                    onNavigateToWidgetStep = { graph.screenNavigator.openWidgetStep(mascot = key.mascot) },
                )
            }
        }
    }
}

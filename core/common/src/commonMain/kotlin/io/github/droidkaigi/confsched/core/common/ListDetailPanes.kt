package io.github.droidkaigi.confsched.core.common

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.navigation3.scene.SinglePaneSceneStrategy

private enum class PaneRole { List, Detail }

private data object PaneRoleMetadataKey : NavMetadataKey<PaneRole>

/**
 * NavEntry metadata for the list of a list-detail pair: [ListDetailSceneStrategy.listPane], the
 * role mark [rememberLoneListPaneSceneStrategy] and [paneEntries] read, and the end-edge inset
 * consumption a list pane needs, since only its start edge is the window's.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun listPane(): Map<String, Any> =
    ListDetailSceneStrategy.listPane() +
        metadata { put(PaneRoleMetadataKey, PaneRole.List) } +
        consumeListDetailPaneInsets(WindowInsetsSides.End)

/**
 * NavEntry metadata for the detail of a list-detail pair: [ListDetailSceneStrategy.detailPane],
 * the role mark [paneEntries] reads, and the start-edge inset consumption a detail pane needs.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun detailPane(): Map<String, Any> =
    ListDetailSceneStrategy.detailPane() +
        metadata { put(PaneRoleMetadataKey, PaneRole.Detail) } +
        consumeListDetailPaneInsets(WindowInsetsSides.Start)

internal fun isDetailPane(metadata: Map<String, Any>): Boolean =
    metadata[PaneRoleMetadataKey] == PaneRole.Detail

/**
 * The entries a scene draws, one per pane.
 *
 * A list-detail scene's [Scene.entries] holds every consecutive pane entry on the back stack, so
 * Search open above the Timetable puts both lists in it while only the topmost one is on screen;
 * the last entry of each role is the one its pane shows.
 */
fun <T : Any> Scene<T>.paneEntries(): List<NavEntry<T>> =
    entries.asReversed().distinctBy { it.metadata[PaneRoleMetadataKey] }.asReversed()

/**
 * Renders a [listPane] entry sitting on top of the back stack as a single pane, and must therefore
 * come before the list-detail strategy in `sceneStrategies`.
 *
 * The list-detail scaffold fills every partition the window offers, expanding the detail pane even
 * with no detail entry to put in it, so a list opened with nothing beside it would otherwise shrink
 * to a pane's width against an empty detail placeholder.
 */
@Composable
fun <T : Any> rememberLoneListPaneSceneStrategy(): SceneStrategy<T> =
    remember { LoneListPaneSceneStrategy() }

private class LoneListPaneSceneStrategy<T : Any> : SceneStrategy<T> {
    private val singlePaneSceneStrategy = SinglePaneSceneStrategy<T>()

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val entry = entries.lastOrNull() ?: return null
        if (entry.metadata[PaneRoleMetadataKey] != PaneRole.List) return null
        return with(singlePaneSceneStrategy) { calculateScene(entries) }
    }
}

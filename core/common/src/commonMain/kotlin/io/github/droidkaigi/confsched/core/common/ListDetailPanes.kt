package io.github.droidkaigi.confsched.core.common

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.navigation3.scene.SinglePaneSceneStrategy

private data object ListPaneMetadataKey : NavMetadataKey<Boolean>

/**
 * NavEntry metadata for the list of a list-detail pair: [ListDetailSceneStrategy.listPane] together
 * with the mark [rememberLoneListPaneSceneStrategy] reads.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun listPane(): Map<String, Any> =
    ListDetailSceneStrategy.listPane() + metadata { put(ListPaneMetadataKey, true) }

/** NavEntry metadata for the detail of a list-detail pair. */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun detailPane(): Map<String, Any> = ListDetailSceneStrategy.detailPane()

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
        if (ListPaneMetadataKey !in entry.metadata) return null
        return with(singlePaneSceneStrategy) { calculateScene(entries) }
    }
}

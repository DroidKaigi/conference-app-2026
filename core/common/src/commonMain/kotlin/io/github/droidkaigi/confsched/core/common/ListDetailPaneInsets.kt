package io.github.droidkaigi.confsched.core.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata

private data object ListDetailPaneInsetsKey : NavMetadataKey<WindowInsetsSides>

/**
 * NavEntry metadata naming the window inset edges to drop from an entry while it is a pane beside
 * another pane: [WindowInsetsSides.Start] for a detail pane, [WindowInsetsSides.End] for a list
 * pane. [rememberListDetailPaneInsetsNavEntryDecorator] applies it.
 */
internal fun consumeListDetailPaneInsets(sides: WindowInsetsSides): Map<String, Any> =
    metadata { put(ListDetailPaneInsetsKey, sides) }

/**
 * Consumes the [consumeListDetailPaneInsets] edges of an entry while it is a pane of a live
 * list-detail scaffold ([LocalListDetailSceneScope] is non-null), so a pane offset from a window
 * edge stops inheriting that edge's system-bar or display-cutout inset from the shared window and
 * padding its content against the pane seam.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun <T : Any> rememberListDetailPaneInsetsNavEntryDecorator(): NavEntryDecorator<T> = remember {
    NavEntryDecorator { entry ->
        val sides = entry.metadata[ListDetailPaneInsetsKey]
        if (sides == null) {
            entry.Content()
        } else {
            // Branching on the scene instead would move Content() and drop the entry's retained state.
            val consumed = if (LocalListDetailSceneScope.current != null) {
                WindowInsets.systemBars.union(WindowInsets.displayCutout).only(sides)
            } else {
                WindowInsets(0, 0, 0, 0)
            }
            Box(Modifier.fillMaxSize().consumeWindowInsets(consumed)) {
                entry.Content()
            }
        }
    }
}

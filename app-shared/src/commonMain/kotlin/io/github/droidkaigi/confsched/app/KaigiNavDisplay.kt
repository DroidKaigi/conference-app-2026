package io.github.droidkaigi.confsched.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import io.github.droidkaigi.confsched.core.common.rememberRootSceneStrategy
import io.github.droidkaigi.confsched.core.common.rememberSnackbarNavEntryDecorator
import io.github.droidkaigi.confsched.core.common.retainNavEntryDecorator
import io.github.droidkaigi.confsched.core.ui.LocalPanePartitionSpacerSize

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun KaigiNavDisplay(
    backStack: NavBackStack<NavKey>,
    onBack: () -> Unit,
    onSelectTab: (RootTab) -> Unit,
    entryProvider: (NavKey) -> NavEntry<NavKey>,
) {
    CompositionLocalProvider(LocalPanePartitionSpacerSize provides PanePartitionSpacerSize) {
        // A themed backdrop behind the transition: while entries cross-fade, both are
        // translucent and the bare window (white) would show through as a flash.
        Surface(color = MaterialTheme.colorScheme.background) {
            NavDisplay(
                backStack = backStack,
                onBack = onBack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    retainNavEntryDecorator(),
                    rememberSnackbarNavEntryDecorator(),
                ),
                sceneStrategies = listOf(
                    // An overlay strategy has to come before the strategies that claim a pane.
                    DialogSceneStrategy(),
                    rememberRootSceneStrategy(),
                    rememberKaigiListDetailSceneStrategy(),
                    SinglePaneSceneStrategy(),
                ),
                sceneDecoratorStrategies = listOfNotNull(
                    rememberRootTabSceneDecorator(
                        backStack = backStack,
                        onSelectTab = onSelectTab,
                    ),
                ),
                entryProvider = entryProvider,
            )
        }
    }
}

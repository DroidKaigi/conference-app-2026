package io.github.droidkaigi.confsched.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

@Composable
fun NavigatorEffect(navigator: AppNavigator, backStack: NavBackStack<NavKey>, logger: KaigiLogger) {
    LaunchedEffect(navigator, backStack) {
        navigator.commands.collect { command ->
            when (command) {
                is NavCommand.Push -> {
                    if (backStack.lastOrNull() == command.key) {
                        logger.warn { "Duplicate push of the top NavKey: ${command.key} — likely a caller bug" }
                    } else {
                        backStack.add(command.key)
                    }
                }

                is NavCommand.Pop -> {
                    val top = backStack.lastOrNull()
                    if (command.origin != null && command.origin != top) {
                        logger.warn { "Stale pop from non-top NavKey: ${command.origin}; top is $top" }
                    } else if (backStack.size > 1) {
                        backStack.removeLastOrNull()
                    }
                }

                is NavCommand.MoveToTop -> if (backStack.lastOrNull() != command.key) {
                    backStack.remove(command.key)
                    backStack.add(command.key)
                }
            }
        }
    }
}

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
                    val top = backStack.lastOrNull()
                    when {
                        top == command.key ->
                            logger.warn { "Duplicate push of the top NavKey: ${command.key} — likely a caller bug" }

                        top is DetailPaneNavKey && command.key is DetailPaneNavKey ->
                            backStack[backStack.lastIndex] = command.key

                        else -> backStack.add(command.key)
                    }
                }

                is NavCommand.Pop -> {
                    val origin = command.origin
                    if (origin == null) {
                        if (backStack.size > 1) backStack.removeLastOrNull()
                    } else {
                        // A list pane's back control is tapped with its detail still open beside
                        // it, so the origin pops together with everything above it.
                        val index = backStack.lastIndexOf(origin)
                        if (index < 0) {
                            logger.warn { "Stale pop from a NavKey no longer on the stack: $origin" }
                        } else if (index > 0) {
                            backStack.subList(index, backStack.size).clear()
                        }
                    }
                }

                is NavCommand.MoveToTop -> if (backStack.lastOrNull() != command.key) {
                    backStack.remove(command.key)
                    backStack.add(command.key)
                }

                is NavCommand.ReplaceTop -> if (backStack.isNotEmpty()) {
                    backStack[backStack.lastIndex] = command.key
                }
            }
        }
    }
}

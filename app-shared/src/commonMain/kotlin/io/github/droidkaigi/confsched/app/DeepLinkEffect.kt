package io.github.droidkaigi.confsched.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.github.droidkaigi.confsched.core.common.DeepLink
import io.github.droidkaigi.confsched.core.common.DeepLinkStore
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.StartupNavKey
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.feature.about.AboutNavKey
import io.github.droidkaigi.confsched.feature.favorites.FavoritesNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableNavKey
import kotlinx.coroutines.flow.first

/**
 * The stack a reader would have walked to the linked content: the timetable root, then any
 * surface the link's route names, then the destination.
 */
fun buildSyntheticBackStack(link: DeepLink): List<NavKey> = when (link) {
    is DeepLink.SessionDetail -> listOf(
        TimetableNavKey,
        TimetableItemDetailNavKey(TimetableItemId(link.sessionId)),
    )

    is DeepLink.Favorites -> listOf(
        TimetableNavKey,
        FavoritesNavKey,
    )

    is DeepLink.About -> listOf(
        TimetableNavKey,
        AboutNavKey,
    )

    is DeepLink.FavoriteSessionDetail -> listOf(
        TimetableNavKey,
        FavoritesNavKey,
        TimetableItemDetailNavKey(TimetableItemId(link.sessionId)),
    )
}

private fun NavBackStack<NavKey>.applySyntheticBackStack(stack: List<NavKey>) {
    clear()
    addAll(stack)
}

@Composable
internal fun DeepLinkEffect(
    deepLinkStore: DeepLinkStore,
    backStack: NavBackStack<NavKey>,
    logger: KaigiLogger,
    onNavigate: (NavKey) -> Unit,
) {
    LaunchedEffect(deepLinkStore, backStack, onNavigate) {
        deepLinkStore.deepLinks.collect { link ->
            // Startup flow (e.g. the dev server picker) restores the persisted server
            // environment; resolving while one is still on the stack would query the default
            // environment's timetable and miss the linked session.
            snapshotFlow { backStack.none { key -> key is StartupNavKey } }.first { it }
            val syntheticBackStack = buildSyntheticBackStack(link)
            if (backStack.size <= 1) {
                // A single-entry stack is a launch that has not navigated yet.
                logger.debug { "deep link $link replaces the initial stack" }
                backStack.applySyntheticBackStack(syntheticBackStack)
            } else {
                // A live stack keeps its history: the synthetic root is already beneath every
                // stack, and the rest lands via move-to-top so the same order forms on top.
                syntheticBackStack.drop(1).forEach(onNavigate)
            }
        }
    }
}

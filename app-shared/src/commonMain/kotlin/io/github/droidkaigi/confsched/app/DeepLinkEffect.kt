package io.github.droidkaigi.confsched.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.github.droidkaigi.confsched.core.common.DeepLink
import io.github.droidkaigi.confsched.core.common.DeepLinkStore
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableNavKey
import kotlinx.coroutines.flow.first

/** How a deep link lands on the back stack. */
sealed interface DeepLinkResolution {
    /** The whole stack is replaced, giving the destination a synthesized back history. */
    data class ReplaceStack(val stack: List<NavKey>) : DeepLinkResolution

    /** The destination is pushed onto the existing stack. */
    data class Push(val key: NavKey) : DeepLinkResolution
}

/**
 * A single-entry stack is a launch that has not navigated yet, so the link replaces it and back
 * lands on the timetable. Any later link — including a dev launch whose server-select flow put
 * the timetable above the picker — is an ordinary push.
 */
fun resolveDeepLink(link: DeepLink, backStack: List<NavKey>): DeepLinkResolution = when (link) {
    is DeepLink.SessionDetail -> {
        val detail = TimetableItemDetailNavKey(TimetableItemId(link.sessionId))
        if (backStack.size <= 1) {
            DeepLinkResolution.ReplaceStack(listOf(TimetableNavKey, detail))
        } else {
            DeepLinkResolution.Push(detail)
        }
    }
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
            // A dev build restores the persisted server environment only through its
            // server-select flow; resolving before the timetable root exists would query the
            // default environment's timetable and miss the linked session.
            snapshotFlow { TimetableNavKey in backStack }.first { reached -> reached }
            when (val resolution = resolveDeepLink(link, backStack)) {
                is DeepLinkResolution.ReplaceStack -> {
                    logger.debug { "deep link $link replaces the initial stack" }
                    backStack.clear()
                    backStack.addAll(resolution.stack)
                }

                is DeepLinkResolution.Push -> onNavigate(resolution.key)
            }
        }
    }
}

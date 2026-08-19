package io.github.droidkaigi.confsched.core.common

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/** A navigation request arriving from outside the app's own UI, such as a widget tap or a URL. */
sealed interface DeepLink {
    data class SessionDetail(val sessionId: String) : DeepLink
}

/**
 * Buffers deep links from platform entry points until DeepLinkEffect collects them, so a link
 * delivered before the first composition is not lost.
 */
@Inject
@SingleIn(AppScope::class)
class DeepLinkStore {
    private val channel = Channel<DeepLink>(Channel.BUFFERED)

    val deepLinks: Flow<DeepLink> = channel.receiveAsFlow()

    fun submit(link: DeepLink) {
        channel.trySend(link)
    }
}

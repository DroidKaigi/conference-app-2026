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

    /** The favorites surface itself. */
    data object Favorites : DeepLink

    /** The about surface. */
    data object About : DeepLink

    /** The timetable surface showing one conference day, named by its [DAY1_SEGMENT]-style path segment. */
    data class Timetable(val daySegment: String) : DeepLink

    /** A session reached through the favorites surface, e.g. from the favorites widget. */
    data class FavoriteSessionDetail(val sessionId: String) : DeepLink

    companion object {
        // Year-scoped so next year's app never captures this year's links.
        const val SCHEME: String = "droidkaigi2026"
        const val SESSION_HOST: String = "session"
        const val FAVORITES_HOST: String = "favorites"
        const val ABOUT_HOST: String = "about"
        const val TIMETABLE_HOST: String = "timetable"
        const val DAY1_SEGMENT: String = "day1"
        const val DAY2_SEGMENT: String = "day2"

        /** Parses a deep-link URL; platform entry points delegate here so the grammar has one home. */
        fun parse(url: String): DeepLink? {
            val prefix = "$SCHEME://"
            if (!url.startsWith(prefix)) return null
            val segments = url.removePrefix(prefix)
                .substringBefore('?')
                .substringBefore('#')
                .split('/')
            return when (segments.first()) {
                SESSION_HOST ->
                    sessionId(segments.drop(1))?.let(DeepLink::SessionDetail)

                TIMETABLE_HOST ->
                    daySegment(segments.drop(1))?.let(DeepLink::Timetable)

                ABOUT_HOST ->
                    About.takeIf { segments.drop(1).all(String::isEmpty) }

                FAVORITES_HOST -> when {
                    segments.drop(1).all(String::isEmpty) -> Favorites

                    segments[1] == SESSION_HOST ->
                        sessionId(segments.drop(2))?.let(DeepLink::FavoriteSessionDetail)

                    else -> null
                }

                else -> null
            }
        }

        private fun sessionId(segments: List<String>): String? =
            segments.dropLastWhile(String::isEmpty).singleOrNull()

        private fun daySegment(segments: List<String>): String? = segments
            .dropLastWhile(String::isEmpty)
            .singleOrNull()
            ?.takeIf { it == DAY1_SEGMENT || it == DAY2_SEGMENT }
    }
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

package io.github.droidkaigi.confsched.app

import androidx.navigation3.runtime.NavKey
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.feature.about.AboutNavKey
import io.github.droidkaigi.confsched.feature.eventmap.EventMapNavKey
import io.github.droidkaigi.confsched.feature.favorites.FavoritesNavKey
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableNavKey

internal fun NavKey.toUrlHash(): String = when (this) {
    is TimetableNavKey -> "#/timetable"
    is TimetableItemDetailNavKey -> "#/session/${id.value}"
    is EventMapNavKey -> "#/event-map"
    is FavoritesNavKey -> "#/favorites"
    is AboutNavKey -> "#/about"
    is ProfileCardNavKey -> "#/profile"
    else -> "#/timetable"
}

/** Parses a URL hash fragment into the back stack it represents, root tab first. */
internal fun parseUrlHash(hash: String): List<NavKey> {
    val path = hash.removePrefix("#").removePrefix("/")
    val segments = path.split("/")
    return when {
        segments.isEmpty() || segments[0] == "timetable" || segments[0] == "" ->
            listOf(TimetableNavKey)

        segments[0] == "session" && segments.size >= 2 && segments[1].isNotEmpty() ->
            listOf(TimetableNavKey, TimetableItemDetailNavKey(TimetableItemId(segments[1])))

        segments[0] == "event-map" -> listOf(EventMapNavKey)

        segments[0] == "favorites" -> listOf(FavoritesNavKey)

        segments[0] == "about" -> listOf(AboutNavKey)

        segments[0] == "profile" -> listOf(ProfileCardNavKey)

        else -> listOf(TimetableNavKey)
    }
}

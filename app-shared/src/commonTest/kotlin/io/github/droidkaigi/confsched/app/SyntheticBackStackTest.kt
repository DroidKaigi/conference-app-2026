package io.github.droidkaigi.confsched.app

import io.github.droidkaigi.confsched.core.common.DeepLink
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.feature.about.AboutNavKey
import io.github.droidkaigi.confsched.feature.favorites.FavoritesNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableNavKey
import kotlin.test.Test
import kotlin.test.assertEquals

class SyntheticBackStackTest {
    @Test
    fun a_session_link_synthesizes_the_timetable_path_to_its_detail() {
        assertEquals(
            listOf(
                TimetableNavKey,
                TimetableItemDetailNavKey(TimetableItemId("abc")),
            ),
            buildSyntheticBackStack(DeepLink.SessionDetail("abc")),
        )
    }

    @Test
    fun a_favorites_link_synthesizes_the_timetable_path_to_the_favorites_tab() {
        assertEquals(
            listOf(TimetableNavKey, FavoritesNavKey),
            buildSyntheticBackStack(DeepLink.Favorites),
        )
    }

    @Test
    fun an_about_link_synthesizes_the_timetable_path_to_the_about_tab() {
        assertEquals(
            listOf(TimetableNavKey, AboutNavKey),
            buildSyntheticBackStack(DeepLink.About),
        )
    }

    @Test
    fun a_favorite_session_link_synthesizes_the_favorites_path_to_its_detail() {
        assertEquals(
            listOf(
                TimetableNavKey,
                FavoritesNavKey,
                TimetableItemDetailNavKey(TimetableItemId("abc")),
            ),
            buildSyntheticBackStack(DeepLink.FavoriteSessionDetail("abc")),
        )
    }
}

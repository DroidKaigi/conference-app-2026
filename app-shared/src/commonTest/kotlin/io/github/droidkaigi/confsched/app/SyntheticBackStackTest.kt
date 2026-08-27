package io.github.droidkaigi.confsched.app

import io.github.droidkaigi.confsched.core.common.DeepLink
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.feature.about.AboutNavKey
import io.github.droidkaigi.confsched.feature.favorites.FavoritesNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableNavKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
    fun a_timetable_link_synthesizes_the_timetable_root_alone() {
        assertEquals(
            listOf(TimetableNavKey),
            buildSyntheticBackStack(DeepLink.Timetable("day2")),
        )
    }

    @Test
    fun a_timetable_link_names_the_day_its_segment_holds() {
        assertEquals(DroidKaigi2026Day.Day1, timetableDeepLinkDay(DeepLink.Timetable("day1")))
        assertEquals(DroidKaigi2026Day.Day2, timetableDeepLinkDay(DeepLink.Timetable("day2")))
        assertNull(timetableDeepLinkDay(DeepLink.Timetable("day3")))
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

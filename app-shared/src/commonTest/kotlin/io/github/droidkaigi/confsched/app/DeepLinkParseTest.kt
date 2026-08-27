package io.github.droidkaigi.confsched.app

import io.github.droidkaigi.confsched.core.common.DeepLink
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeepLinkParseTest {
    @Test
    fun a_session_url_parses_to_its_session_id() {
        assertEquals(
            DeepLink.SessionDetail("abc123"),
            DeepLink.parse("droidkaigi2026://session/abc123"),
        )
    }

    @Test
    fun query_fragment_and_trailing_slash_do_not_change_the_id() {
        assertEquals(DeepLink.SessionDetail("abc"), DeepLink.parse("droidkaigi2026://session/abc/"))
        assertEquals(DeepLink.SessionDetail("abc"), DeepLink.parse("droidkaigi2026://session/abc?ref=widget"))
        assertEquals(DeepLink.SessionDetail("abc"), DeepLink.parse("droidkaigi2026://session/abc#top"))
    }

    @Test
    fun a_session_url_with_extra_segments_does_not_parse() {
        assertNull(DeepLink.parse("droidkaigi2026://session/abc/extra"))
        assertNull(DeepLink.parse("droidkaigi2026://favorites/session/abc/extra"))
    }

    @Test
    fun a_favorites_session_url_parses_to_the_favorites_route() {
        assertEquals(
            DeepLink.FavoriteSessionDetail("abc123"),
            DeepLink.parse("droidkaigi2026://favorites/session/abc123"),
        )
    }

    @Test
    fun the_bare_favorites_url_parses_to_the_favorites_surface() {
        assertEquals(DeepLink.Favorites, DeepLink.parse("droidkaigi2026://favorites"))
        assertEquals(DeepLink.Favorites, DeepLink.parse("droidkaigi2026://favorites/"))
    }

    @Test
    fun the_bare_about_url_parses_to_the_about_surface() {
        assertEquals(DeepLink.About, DeepLink.parse("droidkaigi2026://about"))
        assertNull(DeepLink.parse("droidkaigi2026://about/extra"))
    }

    @Test
    fun favorites_routes_with_a_broken_session_path_do_not_parse() {
        assertNull(DeepLink.parse("droidkaigi2026://favorites/session"))
        assertNull(DeepLink.parse("droidkaigi2026://favorites/session/"))
        assertNull(DeepLink.parse("droidkaigi2026://favorites/abc"))
    }

    @Test
    fun a_timetable_url_parses_to_its_day() {
        assertEquals(DeepLink.Timetable("day1"), DeepLink.parse("droidkaigi2026://timetable/day1"))
        assertEquals(DeepLink.Timetable("day2"), DeepLink.parse("droidkaigi2026://timetable/day2/"))
    }

    @Test
    fun a_timetable_url_without_a_known_day_does_not_parse() {
        assertNull(DeepLink.parse("droidkaigi2026://timetable"))
        assertNull(DeepLink.parse("droidkaigi2026://timetable/"))
        assertNull(DeepLink.parse("droidkaigi2026://timetable/day3"))
        assertNull(DeepLink.parse("droidkaigi2026://timetable/day1/day2"))
    }

    @Test
    fun foreign_schemes_and_hosts_do_not_parse() {
        assertNull(DeepLink.parse("https://session/abc"))
        assertNull(DeepLink.parse("droidkaigi://session/abc"))
        assertNull(DeepLink.parse("xdroidkaigi2026://session/abc"))
        assertNull(DeepLink.parse("droidkaigi2026://session"))
        assertNull(DeepLink.parse("droidkaigi2026://session/"))
        assertNull(DeepLink.parse(""))
    }
}

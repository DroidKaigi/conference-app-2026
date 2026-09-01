package io.github.droidkaigi.confsched.app

import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.feature.eventmap.EventMapNavKey
import io.github.droidkaigi.confsched.feature.eventmap.PrizeOverlayNavKey
import io.github.droidkaigi.confsched.feature.eventmap.StampCollectingNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableNavKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RootTabInTopPanesTest {
    @Test
    fun a_list_pane_beside_a_detail_names_its_own_tab() {
        assertEquals(
            RootTab.Timetable,
            listOf(TimetableNavKey, TimetableItemDetailNavKey(TimetableItemId("abc")))
                .rootTabInTopPanes(paneCount = 2),
        )
    }

    @Test
    fun a_detail_drawn_on_its_own_names_no_tab() {
        assertNull(
            listOf(TimetableNavKey, TimetableItemDetailNavKey(TimetableItemId("abc")))
                .rootTabInTopPanes(paneCount = 1),
        )
    }

    @Test
    fun an_overlay_above_a_list_detail_pair_names_the_tab_of_the_pair() {
        assertEquals(
            RootTab.EventMap,
            listOf(TimetableNavKey, EventMapNavKey, StampCollectingNavKey, PrizeOverlayNavKey(page = 0))
                .rootTabInTopPanes(paneCount = 2),
        )
    }

    @Test
    fun an_overlay_above_a_lone_root_names_its_tab() {
        assertEquals(
            RootTab.EventMap,
            listOf(TimetableNavKey, EventMapNavKey, PrizeOverlayNavKey(page = 0))
                .rootTabInTopPanes(paneCount = 1),
        )
    }
}

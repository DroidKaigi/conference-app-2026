package io.github.droidkaigi.confsched.app

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
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
    private val entryProvider: (NavKey) -> NavEntry<NavKey> = { key ->
        NavEntry(
            key = key,
            metadata = if (key is PrizeOverlayNavKey) DialogSceneStrategy.dialog() else emptyMap(),
        ) {}
    }

    @Test
    fun a_list_pane_beside_a_detail_names_its_own_tab() {
        assertEquals(
            RootTab.Timetable,
            listOf(TimetableNavKey, TimetableItemDetailNavKey(TimetableItemId("abc")))
                .rootTabInTopPanes(paneCount = 2, entryProvider = entryProvider),
        )
    }

    @Test
    fun a_detail_drawn_on_its_own_names_no_tab() {
        assertNull(
            listOf(TimetableNavKey, TimetableItemDetailNavKey(TimetableItemId("abc")))
                .rootTabInTopPanes(paneCount = 1, entryProvider = entryProvider),
        )
    }

    @Test
    fun a_dialog_above_a_list_detail_pair_names_the_tab_of_the_pair() {
        assertEquals(
            RootTab.EventMap,
            listOf(TimetableNavKey, EventMapNavKey, StampCollectingNavKey, PrizeOverlayNavKey(page = 0))
                .rootTabInTopPanes(paneCount = 2, entryProvider = entryProvider),
        )
    }

    @Test
    fun a_dialog_above_a_lone_root_names_its_tab() {
        assertEquals(
            RootTab.EventMap,
            listOf(TimetableNavKey, EventMapNavKey, PrizeOverlayNavKey(page = 0))
                .rootTabInTopPanes(paneCount = 1, entryProvider = entryProvider),
        )
    }
}

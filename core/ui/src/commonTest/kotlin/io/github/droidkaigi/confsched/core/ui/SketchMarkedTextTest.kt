package io.github.droidkaigi.confsched.core.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class SketchMarkedTextTest {

    @Test
    fun extending_a_mark_continues_the_reveal() {
        assertEquals(
            MarkerRevealTransition.Continue,
            markerRevealTransition(previousMark = "Com", mark = "Compose"),
        )
    }

    @Test
    fun shortening_a_mark_keeps_the_revealed_prefix() {
        assertEquals(
            MarkerRevealTransition.Shorten,
            markerRevealTransition(previousMark = "Compose", mark = "Com"),
        )
    }

    @Test
    fun changing_only_letter_case_continues_the_reveal() {
        assertEquals(
            MarkerRevealTransition.Continue,
            markerRevealTransition(previousMark = "Compose", mark = "compose"),
        )
    }

    @Test
    fun replacing_a_mark_restarts_the_reveal() {
        assertEquals(
            MarkerRevealTransition.Restart,
            markerRevealTransition(previousMark = "Compose", mark = "Kotlin"),
        )
    }

    @Test
    fun the_first_mark_starts_a_new_reveal() {
        assertEquals(
            MarkerRevealTransition.Restart,
            markerRevealTransition(previousMark = "", mark = "Compose"),
        )
    }

    @Test
    fun sketch_seed_is_stable_for_a_session_id() {
        assertEquals(-1717638163, stableSketchSeed("session-123"))
    }
}

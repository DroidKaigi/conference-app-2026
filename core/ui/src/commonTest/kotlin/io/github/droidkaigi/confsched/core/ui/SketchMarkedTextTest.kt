package io.github.droidkaigi.confsched.core.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class SketchMarkedTextTest {

    @Test
    fun extending_a_mark_continues_the_reveal() {
        assertEquals(
            MarkerRevealTransition.Continue,
            markerRevealTransition(
                previousMark = "Com",
                mark = "Compose",
                previousMatchStarts = listOf(11),
                matchStarts = listOf(11),
            ),
        )
    }

    @Test
    fun extending_a_mark_restarts_when_a_match_disappears() {
        assertEquals(
            MarkerRevealTransition.Restart,
            markerRevealTransition(
                previousMark = "Com",
                mark = "Comp",
                previousMatchStarts = listOf(0, 11),
                matchStarts = listOf(11),
            ),
        )
    }

    @Test
    fun shortening_a_mark_keeps_the_revealed_prefix() {
        assertEquals(
            MarkerRevealTransition.Shorten,
            markerRevealTransition(
                previousMark = "Compose",
                mark = "Com",
                previousMatchStarts = listOf(11),
                matchStarts = listOf(11),
            ),
        )
    }

    @Test
    fun changing_only_letter_case_continues_the_reveal() {
        assertEquals(
            MarkerRevealTransition.Continue,
            markerRevealTransition(
                previousMark = "Compose",
                mark = "compose",
                previousMatchStarts = listOf(11),
                matchStarts = listOf(11),
            ),
        )
    }

    @Test
    fun replacing_a_mark_restarts_the_reveal() {
        assertEquals(
            MarkerRevealTransition.Restart,
            markerRevealTransition(
                previousMark = "Compose",
                mark = "Kotlin",
                previousMatchStarts = listOf(11),
                matchStarts = listOf(24),
            ),
        )
    }

    @Test
    fun the_first_mark_starts_a_new_reveal() {
        val transition = markerRevealTransition(
            previousMark = "",
            mark = "Compose",
            previousMatchStarts = emptyList(),
            matchStarts = listOf(11),
        )

        assertEquals(MarkerRevealTransition.Start, transition)
        assertEquals(
            expected = 0f,
            actual = markerRevealStart(
                transition = transition,
                targetCharacterCount = 7f,
                revealedCharacters = 7f,
            ),
        )
        assertEquals(1f, markerCrossfadeStart(transition, crossfade = 0f))
    }

    @Test
    fun sketch_seed_is_stable_for_a_session_id() {
        assertEquals(-1717638163, stableSketchSeed("session-123"))
    }
}

package io.github.droidkaigi.confsched.app

import androidx.navigation3.runtime.NavKey
import io.github.droidkaigi.confsched.core.common.DeepLink
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableNavKey
import kotlin.test.Test
import kotlin.test.assertEquals

class DeepLinkResolutionTest {
    private data object OtherNavKey : NavKey

    @Test
    fun a_link_on_the_initial_stack_synthesizes_timetable_then_detail() {
        val resolution = resolveDeepLink(DeepLink.SessionDetail("abc"), listOf(TimetableNavKey))
        assertEquals(
            DeepLinkResolution.ReplaceStack(
                listOf(TimetableNavKey, TimetableItemDetailNavKey(TimetableItemId("abc"))),
            ),
            resolution,
        )
    }

    @Test
    fun a_link_on_a_dev_stack_with_server_select_beneath_pushes_the_detail() {
        val resolution = resolveDeepLink(
            DeepLink.SessionDetail("abc"),
            listOf(OtherNavKey, TimetableNavKey),
        )
        assertEquals(
            DeepLinkResolution.Push(TimetableItemDetailNavKey(TimetableItemId("abc"))),
            resolution,
        )
    }

    @Test
    fun a_link_on_a_navigated_stack_pushes_the_detail() {
        val resolution = resolveDeepLink(
            DeepLink.SessionDetail("abc"),
            listOf(TimetableNavKey, OtherNavKey),
        )
        assertEquals(
            DeepLinkResolution.Push(TimetableItemDetailNavKey(TimetableItemId("abc"))),
            resolution,
        )
    }
}

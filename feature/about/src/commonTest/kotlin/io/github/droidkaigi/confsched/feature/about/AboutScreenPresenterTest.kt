package io.github.droidkaigi.confsched.feature.about

import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleEdit
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AboutScreenPresenterTest {

    private val graph = createGraph<AboutScreenTestGraph>()

    @Test
    fun the_saved_wall_doodle_reaches_the_ui_state() {
        val saved = Doodle.fake()
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> aboutScreenPresenter(channel, saved) },
        ) {
            val uiState = uiStates.awaitItem()
            assertEquals(saved, uiState.doodle)
            assertFalse(uiState.isDoodlingWall)
        }
    }

    @Test
    fun finishing_a_wall_doodle_writes_it_and_leaves_the_mode() {
        val drawn = Doodle.fake()
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> aboutScreenPresenter(channel, Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(AboutScreenAction.StartDoodling)
            assertTrue(uiStates.awaitItem().isDoodlingWall)
            send(AboutScreenAction.SaveWallDoodle(drawn))
            assertEquals(
                listOf(DoodleEdit(target = DoodleTarget.AboutWall, doodle = drawn)),
                graph.doodleMutationKey.invocations.receive(),
            )
            assertFalse(uiStates.awaitItem().isDoodlingWall)
        }
    }

    @Test
    fun a_wall_doodle_save_that_fails_reports_the_error_and_stays_in_the_mode() {
        graph.doodleMutationKey.failWith(IllegalStateException("the doodle could not be written"))
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> aboutScreenPresenter(channel, Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(AboutScreenAction.StartDoodling)
            assertTrue(uiStates.awaitItem().isDoodlingWall)
            send(AboutScreenAction.SaveWallDoodle(Doodle.fake()))
            assertIs<AboutScreenActionResult.ShowMessage>(results.awaitItem())
            // The mode is unchanged, so the presenter emits no further state.
            uiStates.expectNoEvents()
        }
    }

    @Test
    fun cancelling_a_wall_doodle_leaves_the_mode_without_writing_anything() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> aboutScreenPresenter(channel, Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(AboutScreenAction.StartDoodling)
            assertTrue(uiStates.awaitItem().isDoodlingWall)
            send(AboutScreenAction.CancelDoodling)
            assertFalse(uiStates.awaitItem().isDoodlingWall)
            assertTrue(graph.doodleMutationKey.invocations.isEmpty)
        }
    }
}

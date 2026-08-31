package io.github.droidkaigi.confsched.feature.about

import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DoodleScreenPresenterTest {

    private val graph = createGraph<DoodleScreenTestGraph>()

    private val sampleDoodle = Doodle.fake()

    @Test
    fun saving_hands_the_drawn_doodle_to_the_mutation() {
        runPresenterTest<DoodlePresenterContext, DoodleScreenAction, DoodleScreenActionResult, DoodleScreenUiState>(
            presenterContext = graph.presenterContext,
            presenter = { channel -> doodleScreenPresenter(channel, Doodle.Empty) },
        ) {
            assertEquals(Doodle.Empty, uiStates.awaitItem().savedDoodle)
            send(DoodleScreenAction.Save(sampleDoodle))
            assertEquals(sampleDoodle, graph.doodleMutationKey.invocations.receive())
            assertEquals(DoodleScreenActionResult.Saved, results.awaitItem())
        }
    }

    @Test
    fun a_failing_save_emits_a_message() {
        graph.doodleMutationKey.failWith(IllegalStateException("boom"))

        runPresenterTest<DoodlePresenterContext, DoodleScreenAction, DoodleScreenActionResult, DoodleScreenUiState>(
            presenterContext = graph.presenterContext,
            presenter = { channel -> doodleScreenPresenter(channel, Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(DoodleScreenAction.Save(sampleDoodle))
            assertIs<DoodleScreenActionResult.ShowMessage>(results.awaitItem())
        }
    }
}

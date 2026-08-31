package io.github.droidkaigi.confsched.feature.doodle

import dev.zacsweers.metro.createGraphFactory
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleEdit
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DoodleScreenPresenterTest {

    private fun graphFor(target: DoodleTarget): DoodleScreenTestGraph =
        createGraphFactory<DoodleScreenTestGraph.Factory>().create(target)

    @Test
    fun saving_hands_the_drawn_doodle_to_the_mutation() {
        val graph = graphFor(DoodleTarget.AboutWall)
        val drawn = Doodle.fake()

        runPresenterTest<DoodlePresenterContext, DoodleScreenAction, DoodleScreenActionResult, DoodleScreenUiState>(
            presenterContext = graph.presenterContext,
            presenter = { channel -> doodleScreenPresenter(channel, Doodle.Empty, card = null) },
        ) {
            assertEquals(Doodle.Empty, uiStates.awaitItem().savedDoodle)
            send(DoodleScreenAction.Save(drawn))
            assertEquals(
                DoodleEdit(target = DoodleTarget.AboutWall, doodle = drawn),
                graph.doodleMutationKey.invocations.receive(),
            )
            assertEquals(DoodleScreenActionResult.Saved, results.awaitItem())
        }
    }

    @Test
    fun saving_a_card_face_carries_that_face_as_the_target() {
        val graph = graphFor(DoodleTarget.ProfileCardBack)
        val drawn = Doodle.fakeOnCardFace()

        runPresenterTest<DoodlePresenterContext, DoodleScreenAction, DoodleScreenActionResult, DoodleScreenUiState>(
            presenterContext = graph.presenterContext,
            presenter = { channel -> doodleScreenPresenter(channel, Doodle.Empty, card = null) },
        ) {
            assertEquals(DoodleTarget.ProfileCardBack, uiStates.awaitItem().target)
            send(DoodleScreenAction.Save(drawn))
            assertEquals(
                DoodleEdit(target = DoodleTarget.ProfileCardBack, doodle = drawn),
                graph.doodleMutationKey.invocations.receive(),
            )
        }
    }

    @Test
    fun a_failing_save_emits_a_message() {
        val graph = graphFor(DoodleTarget.AboutWall)
        graph.doodleMutationKey.failWith(IllegalStateException("boom"))

        runPresenterTest<DoodlePresenterContext, DoodleScreenAction, DoodleScreenActionResult, DoodleScreenUiState>(
            presenterContext = graph.presenterContext,
            presenter = { channel -> doodleScreenPresenter(channel, Doodle.Empty, card = null) },
        ) {
            uiStates.awaitItem()
            send(DoodleScreenAction.Save(Doodle.fake()))
            assertIs<DoodleScreenActionResult.ShowMessage>(results.awaitItem())
        }
    }
}

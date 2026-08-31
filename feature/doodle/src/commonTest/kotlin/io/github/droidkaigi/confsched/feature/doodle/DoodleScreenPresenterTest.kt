package io.github.droidkaigi.confsched.feature.doodle

import dev.zacsweers.metro.createGraphFactory
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleEdit
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import kotlinx.collections.immutable.persistentMapOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DoodleScreenPresenterTest {

    private fun graphFor(target: DoodleTarget): DoodleScreenTestGraph =
        createGraphFactory<DoodleScreenTestGraph.Factory>().create(target)

    @Test
    fun saving_the_wall_hands_the_drawn_doodle_to_the_mutation() {
        val graph = graphFor(DoodleTarget.AboutWall)
        val drawn = Doodle.fake()

        runPresenterTest<DoodlePresenterContext, DoodleScreenAction, DoodleScreenActionResult, DoodleScreenUiState>(
            presenterContext = graph.presenterContext,
            presenter = { channel -> doodleScreenPresenter(channel, persistentMapOf(), card = null) },
        ) {
            val uiState = assertIs<DoodleScreenUiState.Wall>(uiStates.awaitItem())
            assertEquals(Doodle.Empty, uiState.savedDoodle)
            send(DoodleScreenAction.SaveWall(drawn))
            assertEquals(
                listOf(DoodleEdit(target = DoodleTarget.AboutWall, doodle = drawn)),
                graph.doodleMutationKey.invocations.receive(),
            )
            assertEquals(DoodleScreenActionResult.Saved, results.awaitItem())
        }
    }

    @Test
    fun a_card_target_exposes_both_faces_and_opens_on_the_one_it_was_given() {
        val graph = graphFor(DoodleTarget.ProfileCardBack)
        val front = Doodle.fakeOnCardFace()

        runPresenterTest<DoodlePresenterContext, DoodleScreenAction, DoodleScreenActionResult, DoodleScreenUiState>(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                doodleScreenPresenter(
                    channel,
                    persistentMapOf(DoodleTarget.ProfileCardFront to front),
                    card = null,
                )
            },
        ) {
            val uiState = assertIs<DoodleScreenUiState.Card>(uiStates.awaitItem())
            assertEquals(front, uiState.frontDoodle)
            assertEquals(Doodle.Empty, uiState.backDoodle)
            assertEquals(DoodleCardFace.Back, uiState.initialFace)
        }
    }

    @Test
    fun saving_a_card_writes_both_faces_as_one_mutation() {
        val graph = graphFor(DoodleTarget.ProfileCardFront)
        val front = Doodle.fakeOnCardFace()

        runPresenterTest<DoodlePresenterContext, DoodleScreenAction, DoodleScreenActionResult, DoodleScreenUiState>(
            presenterContext = graph.presenterContext,
            presenter = { channel -> doodleScreenPresenter(channel, persistentMapOf(), card = null) },
        ) {
            uiStates.awaitItem()
            send(DoodleScreenAction.SaveCard(frontDoodle = front, backDoodle = Doodle.Empty))
            assertEquals(
                listOf(
                    DoodleEdit(target = DoodleTarget.ProfileCardFront, doodle = front),
                    DoodleEdit(target = DoodleTarget.ProfileCardBack, doodle = Doodle.Empty),
                ),
                graph.doodleMutationKey.invocations.receive(),
            )
            assertEquals(DoodleScreenActionResult.Saved, results.awaitItem())
        }
    }

    @Test
    fun a_failing_save_emits_a_message() {
        val graph = graphFor(DoodleTarget.AboutWall)
        graph.doodleMutationKey.failWith(IllegalStateException("boom"))

        runPresenterTest<DoodlePresenterContext, DoodleScreenAction, DoodleScreenActionResult, DoodleScreenUiState>(
            presenterContext = graph.presenterContext,
            presenter = { channel -> doodleScreenPresenter(channel, persistentMapOf(), card = null) },
        ) {
            uiStates.awaitItem()
            send(DoodleScreenAction.SaveWall(Doodle.fake()))
            assertIs<DoodleScreenActionResult.ShowMessage>(results.awaitItem())
        }
    }
}

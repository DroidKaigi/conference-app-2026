package io.github.droidkaigi.confsched.feature.eventmap

import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.model.PrizeGroup
import io.github.droidkaigi.confsched.core.model.Prizes
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StampCollectingScreenPresenterTest {

    private val graph = createGraph<StampCollectingScreenTestGraph>()

    @Test
    fun initial_state_groups_the_prizes_in_group_order() {
        runPresenterTest<StampCollectingPresenterContext, Unit, Unit, StampCollectingScreenUiState>(
            presenterContext = graph.presenterContext,
            presenter = { _ -> stampCollectingScreenPresenter(prizes = Prizes.fake()) },
        ) {
            val initial = uiStates.awaitItem()
            assertEquals(listOf(PrizeGroup.A, PrizeGroup.B, PrizeGroup.C), initial.prizeGroups.map { it.group })
        }
    }
}

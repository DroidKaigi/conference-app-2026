package io.github.droidkaigi.confsched.feature.eventmap

import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EventMapScreenPresenterTest {

    private val graph = createGraph<EventMapScreenTestGraph>()

    @Test
    fun initial_state_defaults_to_ground_floor_and_selecting_a_floor_updates_state() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> eventMapScreenPresenter(screenChannel = channel) },
        ) {
            val initial = uiStates.awaitItem()
            assertEquals(Floor.Ground, initial.selectedFloor)

            send(EventMapScreenAction.SelectFloor(Floor.Basement))
            val onBasement = uiStates.awaitItem()
            assertEquals(Floor.Basement, onBasement.selectedFloor)
        }
    }
}

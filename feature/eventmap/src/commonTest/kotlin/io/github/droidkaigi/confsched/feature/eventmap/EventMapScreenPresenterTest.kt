package io.github.droidkaigi.confsched.feature.eventmap

import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Project
import io.github.droidkaigi.confsched.core.model.ProjectId
import io.github.droidkaigi.confsched.core.model.Projects
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals

class EventMapScreenPresenterTest {

    private val graph = createGraph<EventMapScreenTestGraph>()

    private val sampleProjects = Projects(
        items = persistentListOf(
            Project(
                id = ProjectId("1"),
                title = MultiLangText(ja = "Meetup", en = "Meetup"),
                description = MultiLangText(ja = "Description", en = "Description"),
                room = Room.NARWHAL,
            ),
        ),
    )

    @Test
    fun initial_state_defaults_to_ground_floor_and_selecting_a_floor_updates_state() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                eventMapScreenPresenter(
                    screenChannel = channel,
                    projects = sampleProjects,
                )
            },
        ) {
            val initial = uiStates.awaitItem()
            assertEquals(Floor.Ground, initial.selectedFloor)

            send(EventMapScreenAction.SelectFloor(Floor.Basement))
            val onBasement = uiStates.awaitItem()
            assertEquals(Floor.Basement, onBasement.selectedFloor)
        }
    }
}

package io.github.droidkaigi.confsched.feature.favorites

import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FirstFavoriteNotificationScreenPresenterTest {

    private val graph = createGraph<FirstFavoriteNotificationScreenTestGraph>()

    @Test
    fun turning_notifications_on_asks_the_platform_then_answers_the_step() {
        var requested = 0
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                firstFavoriteNotificationScreenPresenter(
                    screenChannel = channel,
                    requestNotificationPermission = { requested++ },
                )
            },
        ) {
            assertEquals(false, uiStates.awaitItem().isAnswering)
            send(FirstFavoriteNotificationScreenAction.TurnOnNotifications)
            assertEquals(FirstFavoriteNotificationScreenActionResult.Answered, results.awaitItem())
            assertEquals(1, requested)
            graph.guidanceMutationKey.invocations.receive()
        }
    }

    @Test
    fun answering_later_records_the_answer_without_asking_the_platform() {
        var requested = 0
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                firstFavoriteNotificationScreenPresenter(
                    screenChannel = channel,
                    requestNotificationPermission = { requested++ },
                )
            },
        ) {
            uiStates.awaitItem()
            send(FirstFavoriteNotificationScreenAction.Later)
            assertEquals(FirstFavoriteNotificationScreenActionResult.Answered, results.awaitItem())
            assertEquals(0, requested)
            graph.guidanceMutationKey.invocations.receive()
        }
    }
}

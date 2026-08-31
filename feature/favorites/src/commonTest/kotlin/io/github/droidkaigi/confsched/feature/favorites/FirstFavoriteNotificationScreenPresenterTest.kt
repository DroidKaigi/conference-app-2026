package io.github.droidkaigi.confsched.feature.favorites

import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.model.Mascot
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
                    areNotificationsOn = false,
                    mascot = Mascot.E,
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
    fun moving_on_records_the_answer_without_asking_the_platform() {
        var requested = 0
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                firstFavoriteNotificationScreenPresenter(
                    screenChannel = channel,
                    requestNotificationPermission = { requested++ },
                    areNotificationsOn = false,
                    mascot = Mascot.E,
                )
            },
        ) {
            uiStates.awaitItem()
            send(FirstFavoriteNotificationScreenAction.Continue)
            assertEquals(FirstFavoriteNotificationScreenActionResult.Answered, results.awaitItem())
            assertEquals(0, requested)
            graph.guidanceMutationKey.invocations.receive()
        }
    }

    @Test
    fun the_step_moves_on_without_asking_when_notifications_are_already_on() {
        var requested = 0
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                firstFavoriteNotificationScreenPresenter(
                    screenChannel = channel,
                    requestNotificationPermission = { requested++ },
                    areNotificationsOn = true,
                    mascot = Mascot.E,
                )
            },
        ) {
            assertEquals(true, uiStates.awaitItem().areNotificationsOn)
            send(FirstFavoriteNotificationScreenAction.Continue)
            assertEquals(FirstFavoriteNotificationScreenActionResult.Answered, results.awaitItem())
            assertEquals(0, requested)
            graph.guidanceMutationKey.invocations.receive()
        }
    }
}

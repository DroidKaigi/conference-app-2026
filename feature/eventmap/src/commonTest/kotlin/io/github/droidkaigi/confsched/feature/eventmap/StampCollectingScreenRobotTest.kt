package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.Prizes
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class StampCollectingScreenRobotTest : RobotTest() {

    @Test
    fun stamp_collecting_screen_behaviour() = runRobotTest(
        robotFactory = { StampCollectingScreenRobot(this) },
    ) {
        describe("when the screen opens") {
            doIt {
                setupPrizes(Prizes.fake())
                setupContent()
            }
            itShould("show where and when a prize can be claimed") {
                checkTextDisplayed("WHERE TO CLAIM YOUR PRIZE")
                checkTextDisplayed("Exhibition Area (B1F)")
                checkTextDisplayed("PRIZE EXCHANGE HOURS")
                checkTextDisplayed("9/2 11:00-17:00")
            }
            describe("and the list is scrolled to the prizes") {
                doIt {
                    scrollToText("Prize 1")
                }
                itShould("list the first prize group") {
                    checkTextDisplayed("Group A")
                    checkTextDisplayed("Prize 1")
                }
            }
            describe("and back is tapped") {
                doIt {
                    clickBack()
                }
                itShould("leave the screen once") {
                    checkBackInvoked(times = 1)
                }
            }
        }
    }
}

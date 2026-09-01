package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.PrizeGroup
import io.github.droidkaigi.confsched.core.model.PrizeId
import io.github.droidkaigi.confsched.core.model.Prizes
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class StampCollectingScreenRobotTest : RobotTest() {

    private val prizes = Prizes.fake()

    @Test
    fun stamp_collecting_screen_behaviour() = runRobotTest(
        robotFactory = { StampCollectingScreenRobot(this) },
    ) {
        describe("when the screen opens") {
            doIt {
                setupPrizes(prizes)
                setupContent()
            }
            itShould("show where and when a prize can be claimed") {
                checkExchangePlaceSectionDisplayed()
                checkExchangeHoursSectionDisplayed()
            }
            describe("and the list is scrolled to the prizes") {
                doIt {
                    scrollToPrize(PrizeId("prize-1"))
                }
                itShould("list the first prize group") {
                    checkPrizeGroupDisplayed(PrizeGroup.A)
                    checkPrizeDisplayed(prizes.items.first())
                }
            }
            describe("and a prize card is tapped") {
                doIt {
                    scrollToPrize(PrizeId("prize-1"))
                    clickPrize(PrizeId("prize-1"))
                }
                itShould("ask for the overlay on that prize") {
                    checkPrizeOpened(page = 0)
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

        describe("when the prize overlay opens on the second prize") {
            doIt {
                setupOverlayContent(initialPage = 1)
            }
            itShould("show that prize with its group and its position") {
                checkPrizePageDisplayed(prizes.items[1])
                checkTextDisplayed("2 / 8")
            }
            describe("and close is tapped") {
                doIt {
                    clickOverlayClose()
                }
                itShould("ask to close once") {
                    checkOverlayCloseInvoked(times = 1)
                }
            }
        }
    }
}

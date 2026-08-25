package io.github.droidkaigi.confsched.feature.about

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_view_map
import io.github.droidkaigi.confsched.feature.about.generated.resources.code_of_conduct
import io.github.droidkaigi.confsched.feature.about.generated.resources.contributors
import io.github.droidkaigi.confsched.feature.about.generated.resources.debug_menu
import io.github.droidkaigi.confsched.feature.about.generated.resources.licenses
import io.github.droidkaigi.confsched.feature.about.generated.resources.privacy_policy
import io.github.droidkaigi.confsched.feature.about.generated.resources.settings
import io.github.droidkaigi.confsched.feature.about.generated.resources.sponsors
import io.github.droidkaigi.confsched.feature.about.generated.resources.staff
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class AboutScreenRobotTest : RobotTest() {

    @Test
    fun about_screen_behaviour() = runRobotTest(
        robotFactory = { AboutScreenRobot(this) },
    ) {
        describe("when the debug menu is available") {
            doIt {
                setupContent(isDebugMenuAvailable = true)
            }
            itShould("list every row under Credits and Others") {
                checkRowDisplayed(Res.string.contributors)
                checkRowDisplayed(Res.string.staff)
                checkRowDisplayed(Res.string.sponsors)
                checkRowDisplayed(Res.string.code_of_conduct)
                checkRowDisplayed(Res.string.licenses)
                checkRowDisplayed(Res.string.privacy_policy)
                checkRowDisplayed(Res.string.settings)
                checkRowDisplayed(Res.string.debug_menu)
            }
            describe("and the venue card is tapped") {
                doIt { clickRow(Res.string.about_view_map) }
                itShould("open the venue map once") {
                    checkInvokedOnce(AboutScreenRobot.VENUE)
                }
            }
            describe("and each Credits row is tapped") {
                doIt {
                    clickRow(Res.string.contributors)
                    clickRow(Res.string.staff)
                    clickRow(Res.string.sponsors)
                }
                itShould("open each destination once") {
                    checkInvokedOnce(AboutScreenRobot.CONTRIBUTORS)
                    checkInvokedOnce(AboutScreenRobot.STAFF)
                    checkInvokedOnce(AboutScreenRobot.SPONSORS)
                }
            }
            describe("and each Others row is tapped") {
                doIt {
                    clickRow(Res.string.code_of_conduct)
                    clickRow(Res.string.licenses)
                    clickRow(Res.string.privacy_policy)
                    clickRow(Res.string.settings)
                    clickRow(Res.string.debug_menu)
                }
                itShould("open each destination once") {
                    checkInvokedOnce(AboutScreenRobot.CODE_OF_CONDUCT)
                    checkInvokedOnce(AboutScreenRobot.LICENSES)
                    checkInvokedOnce(AboutScreenRobot.PRIVACY)
                    checkInvokedOnce(AboutScreenRobot.SETTINGS)
                    checkInvokedOnce(AboutScreenRobot.DEBUG)
                }
            }
            describe("and each social mark is tapped") {
                doIt {
                    clickSocial("YouTube")
                    clickSocial("X")
                    clickSocial("Medium")
                }
                itShould("open each link once") {
                    checkInvokedOnce(AboutScreenRobot.YOUTUBE)
                    checkInvokedOnce(AboutScreenRobot.X)
                    checkInvokedOnce(AboutScreenRobot.MEDIUM)
                }
            }
        }

        describe("when the debug menu is unavailable") {
            doIt {
                setupContent(isDebugMenuAvailable = false)
            }
            itShould("hide the debug row") {
                checkRowDoesNotExist(Res.string.debug_menu)
            }
        }
    }
}

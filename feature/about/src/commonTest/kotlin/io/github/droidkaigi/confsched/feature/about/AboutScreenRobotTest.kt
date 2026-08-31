package io.github.droidkaigi.confsched.feature.about

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleInk
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_draw_on_the_wall
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_view_map
import io.github.droidkaigi.confsched.feature.about.generated.resources.code_of_conduct
import io.github.droidkaigi.confsched.feature.about.generated.resources.contributors
import io.github.droidkaigi.confsched.feature.about.generated.resources.debug_menu
import io.github.droidkaigi.confsched.feature.about.generated.resources.doodle_done
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
                setupSavedWallDoodle(Doodle.Empty)
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
                setupSavedWallDoodle(Doodle.Empty)
                setupContent(isDebugMenuAvailable = false)
            }
            itShould("hide the debug row") {
                checkRowDoesNotExist(Res.string.debug_menu)
            }
        }
    }

    @Test
    fun about_screen_wall_doodling() = runRobotTest(
        robotFactory = { AboutScreenRobot(this) },
    ) {
        describe("when the wall carries no doodle") {
            doIt {
                setupSavedWallDoodle(Doodle.Empty)
                setupContent(isDebugMenuAvailable = false)
            }
            itShould("offer the sign rather than the editor") {
                checkTextDoesNotExist(Res.string.doodle_done)
            }
            describe("and the sign is tapped") {
                doIt { clickSign(Res.string.about_draw_on_the_wall) }
                itShould("open the wall editor with nothing to undo yet") {
                    checkButtonDisplayed(Res.string.doodle_done)
                }
                describe("and a stroke is drawn before Done is tapped") {
                    doIt {
                        drawStroke()
                        clickButton(Res.string.doodle_done)
                    }
                    itShould("save the stroke that was drawn") {
                        checkSavedWallStrokeCount(count = 1)
                        checkTextDoesNotExist(Res.string.doodle_done)
                    }
                }
                describe("and a stroke is drawn in the accent ink before Done is tapped") {
                    doIt {
                        clickAccentInk()
                        drawStroke()
                        clickButton(Res.string.doodle_done)
                    }
                    itShould("save the stroke in the accent ink") {
                        checkSavedWallStrokeInks(listOf(DoodleInk.Accent))
                    }
                }
                describe("and a stroke is drawn before back is pressed") {
                    doIt {
                        drawStroke()
                        pressSystemBack()
                    }
                    itShould("discard the stroke and close the editor") {
                        checkNothingSaved()
                        checkTextDoesNotExist(Res.string.doodle_done)
                    }
                }
            }
        }

        describe("when the wall already carries a doodle") {
            doIt {
                setupSavedWallDoodle(Doodle.fake())
                setupContent(isDebugMenuAvailable = false)
                clickSign(Res.string.about_draw_on_the_wall)
            }
            describe("and Done is tapped straight away") {
                doIt { clickButton(Res.string.doodle_done) }
                itShould("start the edit from the saved strokes and write them back") {
                    checkSavedWallDoodle(Doodle.fake())
                }
            }
            describe("and the save fails") {
                doIt {
                    setupFailingSave()
                    clickButton(Res.string.doodle_done)
                }
                itShould("stay in the editor") {
                    checkButtonDisplayed(Res.string.doodle_done)
                }
            }
        }
    }
}

package io.github.droidkaigi.confsched.feature.about

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.doodle_clear
import io.github.droidkaigi.confsched.feature.about.generated.resources.doodle_save
import io.github.droidkaigi.confsched.feature.about.generated.resources.doodle_undo
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class DoodleScreenRobotTest : RobotTest() {

    @Test
    fun doodle_screen_behaviour() = runRobotTest(
        robotFactory = { DoodleScreenRobot(this) },
    ) {
        describe("when nothing has been drawn yet") {
            doIt {
                setupSavedDoodle(Doodle.Empty)
                setupContent()
            }
            itShould("offer save while undo and clear have nothing to act on") {
                checkButtonDisplayed(Res.string.doodle_save)
                checkButtonEnabled(Res.string.doodle_save)
                checkButtonDisabled(Res.string.doodle_undo)
                checkButtonDisabled(Res.string.doodle_clear)
            }
            describe("and a stroke is drawn") {
                doIt { drawStroke() }
                itShould("let that stroke be undone") {
                    checkButtonEnabled(Res.string.doodle_undo)
                }
            }
            describe("and back is tapped") {
                doIt { clickBack() }
                itShould("leave the screen once without saving") {
                    checkBackInvoked(times = 1)
                    checkNothingSaved()
                }
            }
        }

        describe("when a doodle has been saved before") {
            doIt {
                setupSavedDoodle(Doodle.fake())
                setupContent()
            }
            itShould("start the edit from the saved strokes") {
                checkButtonEnabled(Res.string.doodle_undo)
                checkButtonEnabled(Res.string.doodle_clear)
            }
            describe("and save is tapped") {
                doIt { clickButton(Res.string.doodle_save) }
                itShould("write the saved strokes back and leave the screen") {
                    checkSavedDoodle(Doodle.fake())
                    checkBackInvoked(times = 1)
                }
            }
            describe("and the canvas is cleared before saving") {
                doIt {
                    clickButton(Res.string.doodle_clear)
                    clickButton(Res.string.doodle_save)
                }
                itShould("save an empty doodle") {
                    checkSavedDoodle(Doodle.Empty)
                }
            }
            describe("and the save fails") {
                doIt {
                    setupFailingSave()
                    clickButton(Res.string.doodle_save)
                }
                itShould("stay on the screen") {
                    checkBackInvoked(times = 0)
                }
            }
        }
    }
}

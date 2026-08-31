package io.github.droidkaigi.confsched.feature.doodle

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleEdit
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.Res
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_clear
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_face_back
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_face_front
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_save
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_undo
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class DoodleScreenRobotTest : RobotTest() {

    @Test
    fun doodle_screen_behaviour() = runRobotTest(
        robotFactory = { DoodleScreenRobot(this, DoodleTarget.AboutWall) },
    ) {
        describe("when nothing has been drawn yet") {
            doIt {
                setupSavedDoodle(Doodle.Empty)
                setupCard(null)
                setupContent()
            }
            itShould("offer save while undo and clear have nothing to act on") {
                checkButtonDisplayed(Res.string.doodle_save)
                checkButtonEnabled(Res.string.doodle_save)
                checkButtonDisabled(Res.string.doodle_undo)
                checkButtonDisabled(Res.string.doodle_clear)
            }
            describe("and a stroke is drawn") {
                doIt { drawStroke(canvasIndex = 0) }
                itShould("let that stroke be undone") {
                    checkButtonEnabled(Res.string.doodle_undo)
                }
                describe("and save is tapped") {
                    doIt { clickButton(Res.string.doodle_save) }
                    itShould("save the stroke where it was drawn") {
                        checkWallStrokeDrawnAcrossTheCenter(zoom = 1f)
                    }
                }
            }
            describe("and the canvas is pinched open") {
                doIt { pinchOpen(canvasIndex = 0) }
                itShould("record no stroke for the pinch") {
                    checkButtonDisabled(Res.string.doodle_undo)
                }
                describe("and a stroke is drawn on the magnified canvas") {
                    doIt {
                        drawStroke(canvasIndex = 0)
                        clickButton(Res.string.doodle_save)
                    }
                    itShould("save the stroke where the magnified canvas showed it") {
                        checkWallStrokeDrawnAcrossTheCenter(zoom = 3f)
                    }
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
                setupCard(null)
                setupContent()
            }
            itShould("start the edit from the saved strokes") {
                checkButtonEnabled(Res.string.doodle_undo)
                checkButtonEnabled(Res.string.doodle_clear)
            }
            describe("and save is tapped") {
                doIt { clickButton(Res.string.doodle_save) }
                itShould("write the saved strokes back and leave the screen") {
                    checkSavedDoodles(DoodleEdit(target = DoodleTarget.AboutWall, doodle = Doodle.fake()))
                    checkBackInvoked(times = 1)
                }
            }
            describe("and the canvas is cleared before saving") {
                doIt {
                    clickButton(Res.string.doodle_clear)
                    clickButton(Res.string.doodle_save)
                }
                itShould("save an empty doodle") {
                    checkSavedDoodles(DoodleEdit(target = DoodleTarget.AboutWall, doodle = Doodle.Empty))
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

    @Test
    fun card_doodle_screen_behaviour() = runRobotTest(
        robotFactory = { DoodleScreenRobot(this, DoodleTarget.ProfileCardBack) },
    ) {
        describe("when the card back carries a doodle") {
            doIt {
                setupSavedDoodle(Doodle.fakeOnCardFace())
                setupCard(sampleCard)
                setupContent()
            }
            itShould("open on that face alone, with the other face reachable") {
                checkCanvasCount(count = 1)
                checkButtonDisplayed(Res.string.doodle_face_front)
                checkButtonEnabled(Res.string.doodle_undo)
            }
            describe("and save is tapped") {
                doIt { clickButton(Res.string.doodle_save) }
                itShould("write both faces, the untouched one as it was") {
                    checkSavedDoodles(
                        DoodleEdit(target = DoodleTarget.ProfileCardFront, doodle = Doodle.Empty),
                        DoodleEdit(target = DoodleTarget.ProfileCardBack, doodle = Doodle.fakeOnCardFace()),
                    )
                    checkBackInvoked(times = 1)
                }
            }
        }

        describe("when neither face carries a doodle") {
            doIt {
                setupNoSavedDoodles()
                setupCard(sampleCard)
                setupContent()
            }
            describe("and a stroke is drawn before switching to the other face") {
                doIt {
                    drawStroke(canvasIndex = 0)
                    clickButton(Res.string.doodle_face_front)
                }
                itShould("show the other face with nothing on it") {
                    checkButtonDisabled(Res.string.doodle_undo)
                }
                describe("and the first face is selected again") {
                    doIt { clickButton(Res.string.doodle_face_back) }
                    itShould("still hold the stroke drawn on it") {
                        checkButtonEnabled(Res.string.doodle_undo)
                    }
                }
            }
        }
    }

    @Test
    fun card_doodle_screen_on_a_wide_window() = runRobotTest(
        robotFactory = { DoodleScreenRobot(this, DoodleTarget.ProfileCardFront) },
    ) {
        describe("when the window is wide enough for both faces") {
            doIt {
                setupNoSavedDoodles()
                setupCard(sampleCard)
                setupExpandedWindowContent()
            }
            itShould("lay both faces out, with no face switch to make") {
                checkCanvasCount(count = 2)
                checkButtonDisplayed(Res.string.doodle_save)
                checkTextAbsent(Res.string.doodle_face_front)
                checkTextAbsent(Res.string.doodle_face_back)
            }
            describe("and a stroke is drawn on each face before saving") {
                doIt {
                    drawStroke(canvasIndex = 0)
                    drawStroke(canvasIndex = 1)
                    clickButton(Res.string.doodle_save)
                }
                itShould("save a stroke against each face") {
                    checkSavedFaceStrokeCounts(front = 1, back = 1)
                    checkBackInvoked(times = 1)
                }
            }
        }
    }
}

private val sampleCard = ProfileCard(
    nickName = "Speaker A",
    occupation = "Software Engineer",
    link = "https://example.com/user",
    mascot = ProfileCard.DefaultMascot,
    sketchiness = ProfileCard.DefaultSketchiness,
    avatarImage = null,
)

package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleInk
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import kotlinx.collections.immutable.persistentMapOf
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ProfileCardScreenRobotTest : RobotTest() {

    private val storedCard = ProfileCard(
        nickName = "Speaker A",
        occupation = "Software Engineer",
        link = "https://example.com/a",
        mascot = Mascot.C,
        sketchiness = Sketchiness.Normal,
        // A 1x1 PNG: the screen only needs a decodable avatar present, not the sample picture.
        avatarImage = AvatarImage(
            byteArrayOf(
                -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13,
                73, 72, 68, 82, 0, 0, 0, 1, 0, 0, 0, 1,
                8, 2, 0, 0, 0, -112, 119, 83, -34, 0, 0, 0,
                12, 73, 68, 65, 84, 120, -38, 99, -88, -81, -81, 7,
                0, 2, -2, 1, 126, -124, -40, -93, -119, 0, 0, 0,
                0, 73, 69, 78, 68, -82, 66, 96, -126,
            ),
        ),
    )

    @Test
    fun profile_card_screen_behaviour() = runRobotTest(
        robotFactory = { ProfileCardScreenRobot(this) },
    ) {
        describe("when no card is stored") {
            doIt {
                setupStoredCard(null)
                setupContent()
            }
            itShould("show the empty form") {
                checkFormDisplayed()
                checkAddImageButtonDisplayed()
            }
            describe("and Create is tapped with every field but the image filled") {
                doIt {
                    inputNickName("Speaker B")
                    inputOccupation("Designer")
                    inputLink("https://example.com/b")
                    clickMascot(Mascot.E)
                    clickSketchiness(Sketchiness.Playful)
                    clickCreate()
                }
                itShould("report the image the card still needs and write nothing") {
                    checkAvatarImageErrorDisplayed()
                    checkNoCardWritten()
                }
            }
        }
        describe("when a card is stored") {
            doIt {
                setupStoredCard(storedCard)
                setupDoodles(persistentMapOf(DoodleTarget.ProfileCardFront to Doodle.fakeOnCardFace()))
                setupContent()
            }
            itShould("show the card rather than the form") {
                checkCardDisplayed()
                checkFormDoesNotExist()
            }
            describe("and the doodle button is tapped") {
                doIt { clickDoodle() }
                itShould("swap the card actions for the doodle controls") {
                    checkTextDisplayed("Done")
                    checkCardDoesNotExist()
                }
                describe("and a stroke is drawn on each face before Done is tapped") {
                    doIt {
                        drawStroke()
                        clickFlipToBack()
                        drawStroke()
                        clickFlipToFront()
                        clickDone()
                    }
                    itShould("keep both faces' strokes and save them together") {
                        checkSavedFaceStrokeCounts(front = 5, back = 1)
                        checkCardDisplayed()
                    }
                }
                describe("and the accent ink is picked before a stroke is drawn on each face") {
                    doIt {
                        clickAccentInk()
                        drawStroke()
                        clickFlipToBack()
                        drawStroke()
                        clickFlipToFront()
                        clickDone()
                    }
                    itShould("keep the accent ink across the flip and save both faces in it") {
                        checkLastSavedStrokeInks(front = DoodleInk.Accent, back = DoodleInk.Accent)
                    }
                }
                describe("and the chalk ink is picked on the back face") {
                    doIt {
                        drawStroke()
                        clickFlipToBack()
                        clickChalkInk()
                        drawStroke()
                        clickFlipToFront()
                        clickDone()
                    }
                    itShould("save the back face's stroke in the chalk ink") {
                        checkLastSavedStrokeInks(front = DoodleInk.Default, back = DoodleInk.Chalk)
                    }
                }
                describe("and back is pressed after a stroke is drawn") {
                    doIt {
                        drawStroke()
                        pressSystemBack()
                    }
                    itShould("discard the stroke and show the card actions again") {
                        checkNoDoodleSaved()
                        checkCardDisplayed()
                    }
                }
                describe("and the save fails") {
                    doIt {
                        setupFailingDoodleSave()
                        drawStroke()
                        clickDone()
                    }
                    itShould("stay in the doodle controls") {
                        checkTextDisplayed("Done")
                    }
                }
            }
            describe("and Edit is tapped") {
                doIt { clickEdit() }
                itShould("open the form on the stored card") {
                    checkFormDisplayed()
                    checkNickNameShows("Speaker A")
                }
                describe("and every field is filled again before Create is tapped") {
                    doIt {
                        inputNickName("Speaker B")
                        inputOccupation("Designer")
                        inputLink("https://example.com/b")
                        clickMascot(Mascot.E)
                        clickSketchiness(Sketchiness.Playful)
                        clickCreate()
                    }
                    itShould("write the edited card and show a card again") {
                        checkCardWritten(
                            ProfileCard(
                                nickName = "Speaker B",
                                occupation = "Designer",
                                link = "https://example.com/b",
                                mascot = Mascot.E,
                                sketchiness = Sketchiness.Playful,
                                avatarImage = storedCard.avatarImage,
                            ),
                        )
                        checkFormDoesNotExist()
                        checkCardDisplayed()
                    }
                }
            }
        }
    }
}

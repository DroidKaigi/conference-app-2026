package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ProfileCardScreenRobotTest : RobotTest() {

    private val storedCard = ProfileCard(
        nickName = "Speaker A",
        occupation = "Software Engineer",
        link = "https://example.com/a",
        mascot = Mascot.Koala,
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
                checkTextDisplayed("Nickname")
                checkTextDisplayed("Add image")
                checkTextDisplayed("Create Card")
            }
            describe("and Create is tapped with every field but the image filled") {
                doIt {
                    inputNickName("Speaker B")
                    inputOccupation("Designer")
                    inputLink("https://example.com/b")
                    clickMascot("Meerkat")
                    clickSketchiness("Playful")
                    clickCreate()
                }
                itShould("report the image the card still needs and write nothing") {
                    checkTextDisplayed("Please add an image")
                    checkNoCardWritten()
                }
            }
        }
        describe("when a card is stored") {
            doIt {
                setupStoredCard(storedCard)
                setupContent()
            }
            itShould("show the card rather than the form") {
                checkTextDisplayed("Share")
                checkTextDisplayed("Edit")
                checkTextDoesNotExist("Create Card")
            }
            describe("and Edit is tapped") {
                doIt { clickEdit() }
                itShould("open the form on the stored card") {
                    checkTextDisplayed("Speaker A")
                    checkTextDisplayed("Create Card")
                }
                describe("and every field is filled again before Create is tapped") {
                    doIt {
                        inputNickName("Speaker B")
                        inputOccupation("Designer")
                        inputLink("https://example.com/b")
                        clickMascot("Meerkat")
                        clickSketchiness("Playful")
                        clickCreate()
                    }
                    itShould("write the edited card and show a card again") {
                        checkCardWritten(
                            ProfileCard(
                                nickName = "Speaker B",
                                occupation = "Designer",
                                link = "https://example.com/b",
                                mascot = Mascot.Meerkat,
                                sketchiness = Sketchiness.Playful,
                                avatarImage = storedCard.avatarImage,
                            ),
                        )
                        checkTextDoesNotExist("Create Card")
                        checkTextDisplayed("Share")
                    }
                }
            }
        }
    }
}

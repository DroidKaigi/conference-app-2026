package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import io.github.droidkaigi.confsched.feature.profilecard.component.sampleAvatarImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ProfileCardScreenRobotTest : RobotTest() {

    private val storedCard = ProfileCard(
        nickName = "Speaker A",
        occupation = "Software Engineer",
        link = "https://example.com/a",
        mascot = Mascot.Koala,
        sketchiness = Sketchiness.Normal,
        avatarImage = sampleAvatarImage(),
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

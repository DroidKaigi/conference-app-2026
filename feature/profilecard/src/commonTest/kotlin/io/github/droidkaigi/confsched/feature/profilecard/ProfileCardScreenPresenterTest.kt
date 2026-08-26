package io.github.droidkaigi.confsched.feature.profilecard

import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ProfileCardScreenPresenterTest {

    private val graph = createGraph<ProfileCardScreenTestGraph>()

    private val storedCard = ProfileCard(
        nickName = "Speaker A",
        occupation = "Software Engineer",
        link = "https://example.com",
        mascot = Mascot.Ladybug,
        sketchiness = Sketchiness.Playful,
        avatarImage = AvatarImage(byteArrayOf(1, 2, 3)),
    )

    @Test
    fun the_form_is_shown_while_no_card_is_stored() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = null) },
        ) {
            assertEquals(ProfileCardScreenUiState.Form(), uiStates.awaitItem())
        }
    }

    @Test
    fun the_stored_card_is_shown_once_one_exists() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = storedCard) },
        ) {
            val uiState = assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem())
            assertEquals("Speaker A", uiState.nickName)
            assertEquals(Mascot.Ladybug, uiState.mascot)
            assertEquals(Sketchiness.Playful, uiState.sketchiness)
            assertEquals(storedCard.avatarImage, uiState.avatarImage)
        }
    }

    @Test
    fun editing_a_field_reaches_the_form() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = null) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.UpdateNickName("Speaker B"))
            assertEquals("Speaker B", assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem()).nickName)
        }
    }

    @Test
    fun submitting_writes_the_card_the_form_holds() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = null) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.UpdateNickName("Speaker B"))
            send(ProfileCardScreenAction.UpdateOccupation("Designer"))
            send(ProfileCardScreenAction.UpdateLink("https://example.com/b"))
            send(ProfileCardScreenAction.UpdateMascot(Mascot.Meerkat))
            send(ProfileCardScreenAction.UpdateSketchiness(Sketchiness.Subtle))
            send(ProfileCardScreenAction.UpdateAvatarImage(AvatarImage(byteArrayOf(4, 5))))
            send(ProfileCardScreenAction.Submit)
            assertEquals(
                ProfileCard(
                    nickName = "Speaker B",
                    occupation = "Designer",
                    link = "https://example.com/b",
                    mascot = Mascot.Meerkat,
                    sketchiness = Sketchiness.Subtle,
                    avatarImage = AvatarImage(byteArrayOf(4, 5)),
                ),
                graph.profileCardMutationKey.invocations.receive(),
            )
        }
    }

    @Test
    fun editing_a_stored_card_opens_the_form_prefilled() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = storedCard) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.EditCard)
            val uiState = assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem())
            assertEquals(
                ProfileCardScreenUiState.Form(
                    nickName = "Speaker A",
                    occupation = "Software Engineer",
                    link = "https://example.com",
                    mascot = Mascot.Ladybug,
                    sketchiness = Sketchiness.Playful,
                    avatarImage = storedCard.avatarImage,
                ),
                uiState,
            )
        }
    }
}
